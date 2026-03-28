package com.ecart.product.service;

import com.ecart.product.dto.ProductRequestDto;
import com.ecart.product.dto.ProductResponseDto;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductResponseDto> getAllProducts();

    ProductResponseDto getProductById(Long id);

    ProductResponseDto createProduct(ProductRequestDto productRequestDto);

    ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto);

    boolean deleteProduct(Long id);

    List<ProductResponseDto> searchProducts(String keyword);
}
