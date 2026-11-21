package org.example.service.impl;

import org.example.dto.CreateReaderRequest;
import org.example.model.Reader;
import org.example.repository.ReaderRepository;
import org.example.service.ReaderService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ReaderServiceImpl implements ReaderService {

    private final ReaderRepository readerRepository;

    public ReaderServiceImpl(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    @Override
    public Reader createReader(CreateReaderRequest request) {
        if (readerRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Читач з таким email вже існує");
        }
        Reader reader = new Reader(request.getFullName(), request.getEmail());
        return readerRepository.save(reader);
    }

    @Override
    public Reader getReaderById(Long readerId) {
        Optional<Reader> readerOptional = readerRepository.findById(readerId);
        if (readerOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Читача з id " + readerId + " не знайдено");
        }
        return readerOptional.get();
    }
}