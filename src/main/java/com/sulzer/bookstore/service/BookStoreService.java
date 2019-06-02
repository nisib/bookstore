package com.sulzer.bookstore.service;

import com.sulzer.bookstore.constants.Category;
import com.sulzer.bookstore.service.dto.BookDto;
import com.sulzer.bookstore.service.dto.SellDto;

import java.util.List;

public interface BookStoreService {
    void addNewBook(BookDto bookDto);

    void addBook(Long id, int quantityToAdd);

    BookDto getBookById(Long id);

    List<BookDto> getAllBooks();

    int getNumberOfBooksById(Long id);

    void updateBook(Long id, BookDto bookDto);

    void sellBook(Long id);

    void sellBooks(List<SellDto> sellDtos);

    List<BookDto> getBookByCategoryKeyWord(String keyword, Category category);

    int getNumberOfBooksSoldByCategoryAndKeyword(String keyword, Category category);

}
