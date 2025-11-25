package com.ecom.app.dto;

import com.ecom.app.model.OrderStatus;
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
