package com.ecommerce.order.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class OrderItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productId;
    private BigDecimal price;
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;


}
