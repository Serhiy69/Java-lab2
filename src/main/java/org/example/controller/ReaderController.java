package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.CreateReaderRequest;
import org.example.dto.ReliabilityResponse;
import org.example.model.Loan;
import org.example.model.Reader;
import org.example.service.LoanService;
import org.example.service.ReaderService;
import org.example.service.ReliabilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    private final ReaderService readerService;
    private final ReliabilityService reliabilityService;
    private final LoanService loanService;

    public ReaderController(ReaderService readerService,
                            ReliabilityService reliabilityService,
                            LoanService loanService) {
        this.readerService = readerService;
        this.reliabilityService = reliabilityService;
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<Reader> createReader(@Valid @RequestBody CreateReaderRequest request) {
        Reader savedReader = readerService.createReader(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReader);
    }

    @GetMapping("/{readerId}/reliability")
    public ReliabilityResponse checkReliability(@PathVariable Long readerId) {
        return reliabilityService.checkReliability(readerId);
    }

    @GetMapping("/{readerId}/loans/active")
    public List<Loan> getActiveLoans(@PathVariable Long readerId) {
        return loanService.getActiveLoansByReader(readerId);
    }
}