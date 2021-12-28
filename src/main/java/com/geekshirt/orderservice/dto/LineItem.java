package com.geekshirt.orderservice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@ApiModel(description = "Class that represents an item included in the order")
@Data
@AllArgsConstructor
public class LineItem {

    @ApiModelProperty(notes = "UPC (Universal Product Code), Lenght 12 Digits", example = "123456123456", required = true, position = 0)
    private String upc;

    @ApiModelProperty(notes = "Quantity", example = "5", required = true, position = 1)
    private int quantity;

    @ApiModelProperty(notes = "Price", example = "100.00", required = true, position = 2)
    private double price;
}
