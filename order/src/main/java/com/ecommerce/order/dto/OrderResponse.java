package com.ecommerce.order.dto;

import com.ecommerce.order.models.OrderStatus;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {

    private Long id;
    private BigDecimal price;
    private OrderStatus status;
    private List<OrderItemsDTO> items;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
