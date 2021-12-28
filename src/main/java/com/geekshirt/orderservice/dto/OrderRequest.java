package com.geekshirt.orderservice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description = "Class representing an order to processed")
public class OrderRequest {
    @NotBlank
    @NotNull
    @ApiModelProperty(notes = "Account Id", example = "987162991271", required = true)
    //Describe en Swagger si el atributo es obligatorio u opcional
    private String accountId;

    @ApiModelProperty(notes = "Items included in the order", required = true)
    @NotNull
    private List<LineItem> items;

}
