package org.example.service;

import org.example.dto.ReliabilityResponse;

public interface ReliabilityService {
    ReliabilityResponse checkReliability(Long readerId);
    int getMaxActiveLoans(Long readerId);
}