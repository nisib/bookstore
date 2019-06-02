package com.sulzer.bookstore.service.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

/**
 * Data transfer object for sell
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellDto {

    //book id
    @ApiModelProperty(value = "Id of the book to be sold")
    private long bookId;

    //book name
    @ApiModelProperty(value = "Number of copies of the book to be sold.")
    @Min(value = 0, message = "Total sell should be positive value")
    private int quantity;
}

