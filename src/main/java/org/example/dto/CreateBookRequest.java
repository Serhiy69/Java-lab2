package org.example.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateBookRequest {

    @NotBlank(message = "Назва книги не може бути порожньою")
    private String title;

    @NotBlank(message = "Автор не може бути порожнім")
    private String author;

    public CreateBookRequest() {
    }

    public CreateBookRequest(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}