package com.ecart.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequestDto {
    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;
    private String description;
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;
    @NotNull(message = "Stock Quantity is required")
    @Positive(message = "Stock Quantity must be greater than 0")
    private Integer stockQuantity;
    private String imageUrl;
    @NotBlank(message = "Category is required")
    private String category;
}
