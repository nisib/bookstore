package com.sulzer.bookstore.service;

import com.sulzer.bookstore.constants.Category;
import com.sulzer.bookstore.domain.Book;
import com.sulzer.bookstore.domain.BookRepository;
import com.sulzer.bookstore.service.dto.BookDto;
import com.sulzer.bookstore.service.dto.SellDto;
import com.sulzer.bookstore.service.exceptions.BadRequestException;
import com.sulzer.bookstore.service.exceptions.BookNotFoundException;
import com.sulzer.bookstore.service.exceptions.DuplicateResourceException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceImplTest {
    private final Long id = 1234L;
    private final Category category = Category.ACTION;
    private final int totalCount = 2;
    private final int sold = 2;
    private final String keyword = "keyword";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private BookRepository bookRepository;


    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookStoreServiceImpl sut;

    @Test
    public void testAddNewBook() {
        //Arrange
        BookDto bookDto = mock(BookDto.class);
        Book book = mock(Book.class);
        when(bookDto.getId()).thenReturn(id);
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        when(modelMapper.map(bookDto, Book.class)).thenReturn(book);

        //Act
        sut.addNewBook(bookDto);

        //Verify
        verify(bookRepository).save(book);
    }

    @Test
    public void testAddNewBook_Given_IdIsPresent_Then_ThrowsDuplicateResourceException() {
        thrown.expect(DuplicateResourceException.class);
        thrown.expectMessage("Book with same id present. " +
                "Either use update methods to update the book counts or use addBook(Long id, int quantityToAdd) methods");

        //Arrange
        BookDto bookDto = mock(BookDto.class);
        Book book = mock(Book.class);
        when(bookDto.getId()).thenReturn(id);
        when(bookRepository.findById(id)).thenReturn(Optional.ofNullable(book));

        //Act
        sut.addNewBook(bookDto);
    }

    @Test
    public void testAddBook() {
        //Arrange
        Book book = mock(Book.class);
        when(bookRepository.findById(id)).thenReturn(Optional.ofNullable(book));
        when(book.getTotalCount()).thenReturn(totalCount);

        //Act
        sut.addBook(id, 1);

        //Verify
        verify(bookRepository).save(book);
    }

    @Test
    public void testAddBook_Given_NoBookIsFoundById_Then_ThrowsBookNotFoundException() {
        thrown.expect(BookNotFoundException.class);
        thrown.expectMessage("Book with id:" + id + " is not registered.");
        //Arrange
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        sut.addBook(id, 1);

    }

    @Test
    public void testGetBookById() {
        //Arrange
        Book book = mock(Book.class);
        BookDto bookDto = mock(BookDto.class);
        when(bookRepository.findById(id)).thenReturn(Optional.ofNullable(book));
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        //Act
        BookDto actualBookDto = sut.getBookById(id);

        //Assert
        assertEquals(bookDto, actualBookDto);
    }

    @Test
    public void testGetBookById_Given_NoBookIsFoundForId_Then_ThrowsBookNotFoundException() {
        thrown.expect(BookNotFoundException.class);
        thrown.expectMessage("Book with id:" + id + " is not found.");
        //Arrange
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        sut.getBookById(id);
    }

    @Test
    public void testGetAllBooks() {
        //Arrange
        Book book = mock(Book.class);
        List<Book> bookList = new ArrayList<>();
        bookList.add(book);

        BookDto bookDto = mock(BookDto.class);
        List<BookDto> bookDtoList = new ArrayList<>();
        bookDtoList.add(bookDto);
        when(bookRepository.findAll()).thenReturn(bookList);
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        //Act
        List<BookDto> actualBookDto = sut.getAllBooks();

        //Assert
        assertEquals(bookDtoList, actualBookDto);
    }

    @Test
    public void testGetNumberOfBooksById() {
        //Arrange
        Book book = mock(Book.class);
        when(bookRepository.findById(id)).thenReturn(Optional.ofNullable(book));
        when(book.getTotalCount()).thenReturn(totalCount);

        //Act
        int actualNumberOfBooks = sut.getNumberOfBooksById(id);

        //Assert
        assertEquals(totalCount, actualNumberOfBooks);
    }

    @Test
    public void testGetNumberOfBooksById_Given_NoBookIsPresent() {
        //Arrange
        Book book = mock(Book.class);
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        int actualNumberOfBooks = sut.getNumberOfBooksById(id);

        //Assert
        assertEquals(0, actualNumberOfBooks);
    }

    @Test
    public void testUpdateBook() {
        //Arrange
        BookDto bookDto = mock(BookDto.class);
        Book book = mock(Book.class);
        when(modelMapper.map(bookDto, Book.class)).thenReturn(book);
        when(bookDto.getId()).thenReturn(id);
        Book bookFromRepo = mock(Book.class);
        when(bookRepository.getOne(id)).thenReturn(bookFromRepo);
        when(bookFromRepo.getSold()).thenReturn(sold);

        //Act
        sut.updateBook(id, bookDto);

        //Assert
        verify(bookRepository).save(book);
    }

    @Test
    public void testUpdateBook_Given_IdIsChange_Then_ThrowsBadRequestException() {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Id cannot be updated.");
        //Arrange
        BookDto bookDto = mock(BookDto.class);
        Book book = mock(Book.class);
        when(modelMapper.map(bookDto, Book.class)).thenReturn(book);
        when(bookDto.getId()).thenReturn(43L);
        //Act
        sut.updateBook(id, bookDto);
    }

    @Test
    public void testSellBook() {
        //Arrange
        Book book = mock(Book.class);
        when(bookRepository.findById(id)).thenReturn(Optional.ofNullable(book));
        when(book.getTotalCount()).thenReturn(totalCount);
        when(book.getSold()).thenReturn(sold);

        //Act
        sut.sellBook(id);

        //Verify
        verify(bookRepository).save(book);
    }

    @Test
    public void testSellBook_Given_NoBookIsPresent_Then_ThrowsBookNotFoundException() {
        thrown.expect(BookNotFoundException.class);
        thrown.expectMessage("Book with id: " + id + " is not found.");

        //Arrange
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        sut.sellBook(id);

    }

    @Test
    public void testSellBook_Given_NotSufficientBook_Then_ThrowsBadRequestException() {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("TotalCount cannot be negative. Not enough book in store to sell.");

        //Arrange
        Book book = mock(Book.class);
        when(bookRepository.findById(id)).thenReturn(Optional.ofNullable(book));
        when(book.getTotalCount()).thenReturn(0);

        //Act
        sut.sellBook(id);

    }

    @Test
    public void testSellBooks() {
        //Arrange
        long bookId1 = 12L;
        long bookId2 = 14L;
        int totalCount1 = 12;
        int totalCount2 = 11;
        int quantity1 = 1;
        int quantity2 = 2;
        int sold1 = 2;
        int sold2 = 3;

        Book book1 = mock(Book.class);
        Book book2 = mock(Book.class);
        SellDto sellDto1 = mock(SellDto.class);
        SellDto sellDto2 = mock(SellDto.class);
        List<SellDto> sellDtos = new ArrayList<>();
        sellDtos.add(sellDto1);
        sellDtos.add(sellDto2);

        when(sellDto1.getBookId()).thenReturn(bookId1);
        when(sellDto2.getBookId()).thenReturn(bookId2);
        when(bookRepository.findById(bookId1)).thenReturn(Optional.ofNullable(book1));
        when(bookRepository.findById(bookId2)).thenReturn(Optional.ofNullable(book2));
        when(book1.getTotalCount()).thenReturn(totalCount1);
        when(book2.getTotalCount()).thenReturn(totalCount2);
        when(sellDto1.getQuantity()).thenReturn(quantity1);
        when(sellDto2.getQuantity()).thenReturn(quantity2);
        when(book1.getSold()).thenReturn(sold1);
        when(book2.getSold()).thenReturn(sold2);

        //Act
        sut.sellBooks(sellDtos);

        //Verify
        verify(bookRepository, times(2)).save(any(Book.class));
    }

    @Test
    public void testSellBooks_Given_BookIsNotPresent_Then_ThrowsBookNotFoundException() {
        thrown.expect(BookNotFoundException.class);
        thrown.expectMessage("Book with id: " + id + " is not found.");

        //Arrange
        SellDto sellDto = mock(SellDto.class);
        List<SellDto> sellDtos = new ArrayList<>();
        sellDtos.add(sellDto);
        when(sellDto.getBookId()).thenReturn(id);
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        //Act
        sut.sellBooks(sellDtos);
    }

    @Test
    public void testSellBooks_Given_NotSufficientBook_Then_ThrowsBadRequestException() {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("TotalCount cannot be negative. Not enough book in store to sell.");

        //Arrange
        Book book = mock(Book.class);
        SellDto sellDto = mock(SellDto.class);
        List<SellDto> sellDtos = new ArrayList<>();
        sellDtos.add(sellDto);
        when(sellDto.getBookId()).thenReturn(id);
        when(bookRepository.findById(id)).thenReturn(Optional.ofNullable(book));
        when(book.getTotalCount()).thenReturn(totalCount);
        when(sellDto.getQuantity()).thenReturn(4);

        //Act
        sut.sellBooks(sellDtos);
    }

    @Test
    public void testGetBookByCategoryKeyword() {
        //Arrange
        Book book = mock(Book.class);
        List<Book> books = new ArrayList<>();
        books.add(book);

        BookDto bookDto = mock(BookDto.class);
        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(bookDto);
        when(bookRepository.findAllBookByCategoryAndKeyword(keyword.toLowerCase(), category.getValue())).thenReturn(books);
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        //Act
        List<BookDto> actualBookDtos = sut.getBookByCategoryKeyWord(keyword, category);

        //Assert
        assertEquals(bookDtos, actualBookDtos);

    }

    @Test
    public void testGetNumberOfBooksSoldByCategoryAndKeyword() {
        //Arrange
        Long count = Long.valueOf(totalCount);
        when(bookRepository.countNumberOfBooksSold(keyword.toLowerCase(), category.getValue())).thenReturn(count);

        //Act
        int actualCount = sut.getNumberOfBooksSoldByCategoryAndKeyword(keyword, category);

        assertEquals(totalCount, actualCount);

    }


}
