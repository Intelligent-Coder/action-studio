package com.ecart.product.service;

import com.ecart.product.dto.ProductRequest;
import com.ecart.product.dto.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    ProductResponse createProduct(ProductRequest productRequest);

    Optional<ProductResponse> updateProduct(Long id, ProductRequest productRequest);

    boolean deleteProduct(Long id);

    List<ProductResponse> searchProducts(String keyword);
}
