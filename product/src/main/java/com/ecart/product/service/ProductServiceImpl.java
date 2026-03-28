package com.ecart.product.service;

import com.ecart.product.dto.ProductRequestDto;
import com.ecart.product.dto.ProductResponseDto;
import com.ecart.product.entity.Product;
import com.ecart.product.exception.DuplicateResourceException;
import com.ecart.product.exception.ResourceNotFoundException;
import com.ecart.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findByActiveTrue()
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .map(this::convertToDto).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        if (productRepository.findByName(productRequestDto.getName()).isPresent()) {
            throw new DuplicateResourceException("Product already exists");
        }
        final Product product = new Product();
        mapToEntity(productRequestDto, product);
        product.setActive(true);
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    mapToEntity(productRequestDto, existingProduct);
                    return convertToDto(productRepository.save(existingProduct));
                }).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id).map(product -> {
            product.setActive(false);
            productRepository.save(product);
            return true;
        }).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public List<ProductResponseDto> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::convertToDto).toList();
    }

    private void mapToEntity(ProductRequestDto productRequestDto, Product product) {
        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setPrice(productRequestDto.getPrice());
        product.setStockQuantity(productRequestDto.getStockQuantity());
        product.setCategory(productRequestDto.getCategory());
        product.setImageUrl(productRequestDto.getImageUrl());
    }

    private ProductResponseDto convertToDto(Product product) {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(product.getId());
        productResponseDto.setName(product.getName());
        productResponseDto.setDescription(product.getDescription());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setStockQuantity(product.getStockQuantity());
        productResponseDto.setImageUrl(product.getImageUrl());
        productResponseDto.setCategory(product.getCategory());
        productResponseDto.setActive(product.isActive());
        return productResponseDto;
    }
}
