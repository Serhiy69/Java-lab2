package org.example.service;

import org.example.dto.CreateReaderRequest;
import org.example.model.Reader;

public interface ReaderService {
    Reader createReader(CreateReaderRequest request);
    Reader getReaderById(Long readerId);
}