package org.example.service.impl;

import org.example.dto.CreateLoanRequest;
import org.example.model.Book;
import org.example.model.BookStatus;
import org.example.model.Loan;
import org.example.model.Reader;
import org.example.repository.BookRepository;
import org.example.repository.LoanRepository;
import org.example.service.BookService;
import org.example.service.LoanService;
import org.example.service.ReaderService;
import org.example.service.ReliabilityService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final ReaderService readerService;
    private final BookService bookService;
    private final ReliabilityService reliabilityService;

    public LoanServiceImpl(LoanRepository loanRepository,
                           BookRepository bookRepository,
                           ReaderService readerService,
                           BookService bookService,
                           ReliabilityService reliabilityService) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.readerService = readerService;
        this.bookService = bookService;
        this.reliabilityService = reliabilityService;
    }

    @Override
    @Transactional
    public Loan issueBook(CreateLoanRequest request) {
        Reader reader = readerService.getReaderById(request.getReaderId());
        Book book = bookService.getBookById(request.getBookId());

        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Книга вже видана іншому читачу");
        }

        List<Loan> activeLoans = loanRepository.findByReaderIdAndReturnDateIsNull(reader.getId());
        int maxAllowedLoans = reliabilityService.getMaxActiveLoans(reader.getId());

        if (activeLoans.size() >= maxAllowedLoans) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Перевищено ліміт активних позик. Дозволено максимум: " + maxAllowedLoans
            );
        }

        book.setStatus(BookStatus.BORROWED);
        bookRepository.save(book);

        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(14);

        Loan loan = new Loan(book, reader, loanDate, dueDate);
        return loanRepository.save(loan);
    }

    @Override
    @Transactional
    public Loan returnBook(Long loanId) {
        Optional<Loan> loanOptional = loanRepository.findById(loanId);
        if (loanOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Позику з id " + loanId + " не знайдено");
        }

        Loan loan = loanOptional.get();
        if (loan.getReturnDate() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ця книга вже була повернута");
        }

        loan.setReturnDate(LocalDate.now());

        Book book = loan.getBook();
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    @Override
    public List<Loan> getActiveLoansByReader(Long readerId) {
        readerService.getReaderById(readerId);
        return loanRepository.findByReaderIdAndReturnDateIsNull(readerId);
    }
}