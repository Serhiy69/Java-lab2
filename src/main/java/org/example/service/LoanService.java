package org.example.service;

import org.example.dto.CreateLoanRequest;
import org.example.model.Loan;

import java.util.List;

public interface LoanService {
    Loan issueBook(CreateLoanRequest request);
    Loan returnBook(Long loanId);
    List<Loan> getActiveLoansByReader(Long readerId);
}