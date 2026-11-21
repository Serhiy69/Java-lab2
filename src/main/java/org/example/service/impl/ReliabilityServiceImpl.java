package org.example.service.impl;

import org.example.dto.ReliabilityResponse;
import org.example.model.Loan;
import org.example.repository.LoanRepository;
import org.example.service.ReaderService;
import org.example.service.ReliabilityService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReliabilityServiceImpl implements ReliabilityService {

    private final LoanRepository loanRepository;
    private final ReaderService readerService;

    public ReliabilityServiceImpl(LoanRepository loanRepository, ReaderService readerService) {
        this.loanRepository = loanRepository;
        this.readerService = readerService;
    }

    @Override
    public ReliabilityResponse checkReliability(Long readerId) {

        readerService.getReaderById(readerId);

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);


        List<Loan> returnedLoans = loanRepository.findByReaderIdAndReturnDateBetween(readerId, thirtyDaysAgo, today);


        boolean allReturnedOnTime = true;
        for (Loan loan : returnedLoans) {
            if (loan.getReturnDate().isAfter(loan.getDueDate())) {
                allReturnedOnTime = false;
                break;
            }
        }


        List<Loan> activeLoans = loanRepository.findByReaderIdAndReturnDateIsNull(readerId);
        boolean hasOverdueActiveLoans = false;
        for (Loan loan : activeLoans) {
            if (today.isAfter(loan.getDueDate())) {
                hasOverdueActiveLoans = true;
                break;
            }
        }

        boolean isReliable = returnedLoans.size() >= 3 && allReturnedOnTime && !hasOverdueActiveLoans;
        int maxActiveLoans = isReliable ? 5 : 2;

        return new ReliabilityResponse(
                readerId,
                isReliable,
                maxActiveLoans,
                returnedLoans.size(),
                hasOverdueActiveLoans
        );
    }

    @Override
    public int getMaxActiveLoans(Long readerId) {
        ReliabilityResponse response = checkReliability(readerId);
        return response.getMaxActiveLoans();
    }
}