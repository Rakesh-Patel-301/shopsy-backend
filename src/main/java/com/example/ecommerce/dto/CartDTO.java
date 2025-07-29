package com.example.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private String sessionId;
    private List<CartItemDTO> items;
    private BigDecimal total;

    @Data
    public static class CartItemDTO {
        private Long id;
        private ProductDTO product;
        private Integer quantity;
        private BigDecimal subtotal;
    }

    @Data
    public static class ProductDTO {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private String imageUrl;
        private String brand;
        private String model;
    }
} 