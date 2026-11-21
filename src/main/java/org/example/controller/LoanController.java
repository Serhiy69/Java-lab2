package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.CreateLoanRequest;
import org.example.model.Loan;
import org.example.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<Loan> issueBook(@Valid @RequestBody CreateLoanRequest request) {
        Loan createdLoan = loanService.issueBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLoan);
    }

    @PostMapping("/{loanId}/return")
    public Loan returnBook(@PathVariable Long loanId) {
        return loanService.returnBook(loanId);
    }
}