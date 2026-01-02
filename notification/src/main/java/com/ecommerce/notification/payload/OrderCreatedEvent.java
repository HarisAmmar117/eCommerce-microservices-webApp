package com.ecommerce.notification.payload;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private Long orderId;
    private String userId;
    private OrderStatus orderStatus;
    private List<OrderItemsDTO> items;
    private BigDecimal price;
    private LocalDateTime createdAt;

}
