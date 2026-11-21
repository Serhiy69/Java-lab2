package org.example.service;

import org.example.dto.CreateBookRequest;
import org.example.model.Book;

import java.util.List;

public interface BookService {
    Book createBook(CreateBookRequest request);
    List<Book> getAllBooks();
    Book getBookById(Long bookId);
}