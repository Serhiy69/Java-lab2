package org.example.dto;

import jakarta.validation.constraints.NotNull;

public class CreateLoanRequest {

    @NotNull(message = "Потрібно вказати readerId")
    private Long readerId;

    @NotNull(message = "Потрібно вказати bookId")
    private Long bookId;

    public CreateLoanRequest() {
    }

    public CreateLoanRequest(Long readerId, Long bookId) {
        this.readerId = readerId;
        this.bookId = bookId;
    }

    public Long getReaderId() {
        return readerId;
    }

    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}