package com.sulzer.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sulzer.bookstore.constants.Category;
import com.sulzer.bookstore.service.BookStoreService;
import com.sulzer.bookstore.service.dto.BookDto;
import com.sulzer.bookstore.service.dto.SellDto;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BookStoreControllerTest {
    private final Long id = 2134L;
    private final String title = "title";
    private final String author = "author";
    private final float price = 25;
    private final Category category = Category.DRAMA;
    private final int totalCount = 2;
    private final int sold = 2;
    private final int addByNum = 1;
    private final String keyword = "aut";

    private MockMvc mockMvc;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookStoreController bookStoreController;

    @MockBean
    private BookStoreService bookStoreService;

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(this.bookStoreController).build();
    }

    @Test
    public void testAddNewBook() throws Exception {
        //Arrange
        BookDto bookDto = BookDto.builder()
                .id(id).title(title).author(author)
                .category(category).price(price)
                .totalCount(totalCount).build();

        doNothing().when(bookStoreService).addNewBook(bookDto);

        //Act and Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/add-new-book")
                .content(objectMapper.writeValueAsBytes(bookDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testAddBook() throws Exception {
        //Arrange
        String url = "/api/add-book/" + id + "/" + addByNum;

        doNothing().when(bookStoreService).addBook(id, addByNum);

        //Act and Assert
        mockMvc.perform(MockMvcRequestBuilders
                .put(url))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBookById() throws Exception {
        //Arrange
        String url = "/api/book/" + id;
        BookDto bookDto = createBookDto();
        when(bookStoreService.getBookById(id)).thenReturn(bookDto);

        //Act and Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author").value(author));
    }

    @Test
    public void testGetAllBooks() throws Exception {
        //Arrange
        List<BookDto> bookDtoList = Arrays.asList(createBookDto());
        when(bookStoreService.getAllBooks()).thenReturn(bookDtoList);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/book-list")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*].id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*].author").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].author").value(author))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*].category").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*].totalCount").isNotEmpty());
    }

    @Test
    public void testGetNumberOfBooksById() throws Exception {
        //Arrange
        String url = "/api/number-of-books/" + id;
        when(bookStoreService.getNumberOfBooksById(id)).thenReturn(totalCount);
        //Act and Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .accept(MediaType.ALL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.valueOf(totalCount)));
    }

    @Test
    public void testUpdateBook() throws Exception {
        //Arrange
        String url = "/api/books/" + id;
        BookDto bookDto = createBookDto();
        doNothing().when(bookStoreService).updateBook(id, bookDto);

        //Act and Assert
        mockMvc.perform(MockMvcRequestBuilders
                .put(url)
                .content(objectMapper.writeValueAsBytes(bookDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void testSellBook() throws Exception {
        //Arrange url
        String url = "/api/sell-book/" + id;
        doNothing().when(bookStoreService).sellBook(id);

        //Act and Assert
        mockMvc.perform(MockMvcRequestBuilders
                .put(url))
                .andExpect(status().isOk());
    }

    @Test
    public void testSellBooks() throws Exception {
        //Arrange
        SellDto sellDto = SellDto.builder()
                .bookId(id)
                .quantity(sold)
                .build();
        List<SellDto> sellDtoList = new ArrayList<>();
        sellDtoList.add(sellDto);
        doNothing().when(bookStoreService).sellBooks(sellDtoList);

        //Act and Assert
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/sell-books")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(sellDtoList)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBookByCategoryKeyword() throws Exception {
        //Arrange
        String url = "/api/books?"
                + "keyword=" + keyword
                + "&category=" + category;
        List<BookDto> bookDtoList = Arrays.asList(createBookDto());
        when(bookStoreService.getBookByCategoryKeyWord(keyword, category)).thenReturn(bookDtoList);

        //Act and Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title").value(title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].author").value(author))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].category").value(category.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].totalCount").value(totalCount));
    }

    @Test
    public void testGetNumberOfBooksSoldByCategoryAndKeyword() throws Exception {
        //Arrange
        String url = "/api/number-of-books?"
                + "keyword=" + keyword
                + "&category=" + category;

        when(bookStoreService.getNumberOfBooksSoldByCategoryAndKeyword(keyword, category)).thenReturn(sold);

        //Act and Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .accept(MediaType.ALL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.valueOf(sold)));

    }


    private BookDto createBookDto() {
        return BookDto.builder()
                .id(id).title(title).author(author)
                .category(category).price(price)
                .totalCount(totalCount).build();
    }


}
