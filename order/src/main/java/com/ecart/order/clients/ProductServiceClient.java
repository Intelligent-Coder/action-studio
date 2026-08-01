package com.ecart.order.clients;

import com.ecart.order.dto.ProductResponse;
import com.ecart.order.dto.StockUpdateRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface ProductServiceClient {
    @GetExchange("/api/products/{id}")
    ProductResponse getProductById(@PathVariable String id);

    @PostExchange("/api/products/{id}/decrement-stock")
    void decrementStock(@PathVariable Long id, @RequestBody StockUpdateRequest request);
}
