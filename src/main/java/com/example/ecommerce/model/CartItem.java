package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity = 1;

    @Transient
    public BigDecimal getSubtotal() {
        if (product == null || product.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
} 