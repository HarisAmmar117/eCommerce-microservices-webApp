package com.ecom.app.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItemsDTO {

    private Long id;
    private Long productId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subTotal;

}
