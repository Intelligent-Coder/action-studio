package com.ecart.order.controller;

import com.ecart.order.dto.CartItemRequest;
import com.ecart.order.dto.CartItemResponse;
import com.ecart.order.exception.ErrorResponse;
import com.ecart.order.exception.ValidationErrorResponse;
import com.ecart.order.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart Management", description = "APIs for managing a user's cart")
public class CartController {
    private final CartService cartService;

    @PostMapping
    @Operation(summary = "Add an item to the cart")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item added to cart"),
            @ApiResponse(responseCode = "400", description = "Invalid cart item data",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User or product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Insufficient stock",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "User or product service unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> addToCart(@RequestHeader("X-User-ID") String userId,
                                          @Valid @RequestBody CartItemRequest cartRequest) {
        cartService.addToCart(userId, cartRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove an item from the cart")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item removed from cart"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    public ResponseEntity<String> removeFromCart(@RequestHeader("X-User-ID") String userId,
                                                  @PathVariable String productId) {
        return cartService.removeFromCart(userId, productId) ? ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Get cart items")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart items retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartItemResponse.class))),
            @ApiResponse(responseCode = "404", description = "A product in the cart was not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Product service unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<CartItemResponse>> getCartItems(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }
}
