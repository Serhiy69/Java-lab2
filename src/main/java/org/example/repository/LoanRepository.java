package org.example.repository;

import org.example.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByReaderIdAndReturnDateIsNull(Long readerId);

    List<Loan> findByReaderIdAndReturnDateBetween(Long readerId, LocalDate startDate, LocalDate endDate);
}