package com.ecommerce.notification.payload;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItemsDTO {

    private Long id;
    private String productId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subTotal;

}
