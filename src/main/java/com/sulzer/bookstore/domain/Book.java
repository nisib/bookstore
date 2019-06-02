package com.sulzer.bookstore.domain;

import com.sulzer.bookstore.constants.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Representation of Book Table
 **/
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    /**
     * Unique Book Number given by company.
     * Eg: ISBN number
     */
    @Id
    private Long id;

    /**
     * title of the book
     */
    @NotNull
    private String title;

    /**
     * author of the book
     */
    private String author;

    /**
     * category of the book
     * Eg: Novel, Fiction, etc
     */
    private Category category;

    /**
     * price of the book
     */
    @Min(value = 0, message = "Price should be positive value.")
    private float price;

    /**
     * Amount of book available
     */
    @Min(value = 0, message = "Total Count should be positive value.")
    private int totalCount;

    /**
     * Total copies of book sold
     */
    @Min(value = 0, message = "Total sell should be positive value.")
    private int sold;
}

