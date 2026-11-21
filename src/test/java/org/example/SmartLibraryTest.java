package org.example;

import org.example.dto.CreateBookRequest;
import org.example.dto.CreateLoanRequest;
import org.example.dto.CreateReaderRequest;
import org.example.dto.ReliabilityResponse;
import org.example.model.Book;
import org.example.model.BookStatus;
import org.example.model.Loan;
import org.example.model.Reader;
import org.example.repository.BookRepository;
import org.example.repository.LoanRepository;
import org.example.repository.ReaderRepository;
import org.example.service.BookService;
import org.example.service.LoanService;
import org.example.service.ReaderService;
import org.example.service.ReliabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class SmartLibraryTest {

    @Autowired
    private ReaderService readerService;

    @Autowired
    private BookService bookService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private ReliabilityService reliabilityService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @BeforeEach
    void clearDatabase() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        readerRepository.deleteAll();
    }

    @Test
    void testUniqueReaderEmail() {
        readerService.createReader(new CreateReaderRequest("Serhiy", "serhiy@test.com"));

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> {
            readerService.createReader(new CreateReaderRequest("Serhiy02", "serhiy@test.com"));
        });

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
    }

    @Test
    void testCreateAndGetBooks() {
        bookService.createBook(new CreateBookRequest("Кобзар", "Тарас Шевченко"));
        bookService.createBook(new CreateBookRequest("Захар Беркут", "Іван Франко"));

        List<Book> books = bookService.getAllBooks();

        assertEquals(2, books.size());
        assertEquals(BookStatus.AVAILABLE, books.get(0).getStatus());
    }

    @Test
    void testRegularReaderLimitAndBookAvailability() {
        Reader reader1 = readerService.createReader(new CreateReaderRequest("Oleg", "oleg@test.com"));
        Reader reader2 = readerService.createReader(new CreateReaderRequest("Maria", "maria@test.com"));

        Book book1 = bookService.createBook(new CreateBookRequest("Book1", "Author1"));
        Book book2 = bookService.createBook(new CreateBookRequest("Book2", "Author2"));
        Book book3 = bookService.createBook(new CreateBookRequest("Book3", "Author3"));

        Loan loan1 = loanService.issueBook(new CreateLoanRequest(reader1.getId(), book1.getId()));
        assertEquals(loan1.getLoanDate().plusDays(14), loan1.getDueDate());
        assertEquals(BookStatus.BORROWED, bookService.getBookById(book1.getId()).getStatus());

        ResponseStatusException alreadyBorrowed = assertThrows(ResponseStatusException.class, () -> {
            loanService.issueBook(new CreateLoanRequest(reader2.getId(), book1.getId()));
        });
        assertEquals(HttpStatus.CONFLICT, alreadyBorrowed.getStatusCode());

        loanService.issueBook(new CreateLoanRequest(reader1.getId(), book2.getId()));
        assertEquals(2, loanService.getActiveLoansByReader(reader1.getId()).size());

        ResponseStatusException limitExceeded = assertThrows(ResponseStatusException.class, () -> {
            loanService.issueBook(new CreateLoanRequest(reader1.getId(), book3.getId()));
        });
        assertEquals(HttpStatus.BAD_REQUEST, limitExceeded.getStatusCode());
    }

    @Test
    void testReturnBookAndHistorySaved() {
        Reader reader = readerService.createReader(new CreateReaderRequest("Anna", "anna@test.com"));
        Book book = bookService.createBook(new CreateBookRequest("Тіні забутих предків", "Михайло Коцюбинський"));

        Loan loan = loanService.issueBook(new CreateLoanRequest(reader.getId(), book.getId()));

        Loan returnedLoan = loanService.returnBook(loan.getId());
        assertNotNull(returnedLoan.getReturnDate());
        assertEquals(BookStatus.AVAILABLE, bookService.getBookById(book.getId()).getStatus());

        assertTrue(loanRepository.findById(loan.getId()).isPresent());
        assertEquals(0, loanService.getActiveLoansByReader(reader.getId()).size());

        ResponseStatusException alreadyReturned = assertThrows(ResponseStatusException.class, () -> {
            loanService.returnBook(loan.getId());
        });
        assertEquals(HttpStatus.BAD_REQUEST, alreadyReturned.getStatusCode());
    }

    @Test
    void testReliableReaderGetsLimitOf5Books() {
        Reader reader = readerService.createReader(new CreateReaderRequest("Serhiy", "serhiy@test.com"));

        for (int i = 1; i <= 3; i++) {
            Book book = bookService.createBook(new CreateBookRequest("Book" + i, "Author"));
            Loan loan = loanService.issueBook(new CreateLoanRequest(reader.getId(), book.getId()));
            loanService.returnBook(loan.getId());
        }

        ReliabilityResponse reliability = reliabilityService.checkReliability(reader.getId());
        assertTrue(reliability.isReliable());
        assertEquals(5, reliability.getMaxActiveLoans());
        assertEquals(3, reliability.getReturnedLast30Days());
        assertFalse(reliability.isHasOverdueActiveLoans());

        for (int i = 1; i <= 5; i++) {
            Book book = bookService.createBook(new CreateBookRequest("New book" + i, "Author"));
            loanService.issueBook(new CreateLoanRequest(reader.getId(), book.getId()));
        }
        assertEquals(5, loanService.getActiveLoansByReader(reader.getId()).size());

        Book sixthBook = bookService.createBook(new CreateBookRequest("Book6", "Author"));
        ResponseStatusException limitExceeded = assertThrows(ResponseStatusException.class, () -> {
            loanService.issueBook(new CreateLoanRequest(reader.getId(), sixthBook.getId()));
        });
        assertEquals(HttpStatus.BAD_REQUEST, limitExceeded.getStatusCode());
    }

    @Test
    void testReaderLosesReliabilityWhenActiveLoanIsOverdue() {
        Reader reader = readerService.createReader(new CreateReaderRequest("Dmytro", "dmytro@test.com"));

        for (int i = 1; i <= 3; i++) {
            Book book = bookService.createBook(new CreateBookRequest("Book" + i, "Author"));
            Loan loan = loanService.issueBook(new CreateLoanRequest(reader.getId(), book.getId()));
            loanService.returnBook(loan.getId());
        }
        assertTrue(reliabilityService.checkReliability(reader.getId()).isReliable());

        Book overdueBook = bookService.createBook(new CreateBookRequest("OverdueBook", "Author"));
        Loan activeLoan = loanService.issueBook(new CreateLoanRequest(reader.getId(), overdueBook.getId()));
        activeLoan.setDueDate(LocalDate.now().minusDays(2));
        loanRepository.save(activeLoan);

        ReliabilityResponse reliability = reliabilityService.checkReliability(reader.getId());
        assertFalse(reliability.isReliable());
        assertTrue(reliability.isHasOverdueActiveLoans());
        assertEquals(2, reliability.getMaxActiveLoans());
    }
}