package com.ecom.app.service;


import com.ecom.app.dto.OrderItemsDTO;
import com.ecom.app.dto.OrderResponse;
import com.ecom.app.model.*;
import com.ecom.app.repository.OrderRepository;
import com.ecom.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserRepository userRepository;



    //Implementing create order
    public Optional<OrderResponse> createOrder(String userId) {

        //validate cart item
        List<CartItem> items = cartService.fetchAllCartItems(userId);

        if(items.isEmpty()){

            return Optional.empty();

        }

        // validate user
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));

        if(userOpt.isEmpty()){

            return Optional.empty();

        }

        User user = userOpt.get();


        //get the total amount
        BigDecimal totalPrice = items.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO,BigDecimal::add);


        //create order
        Order order = new Order();

        order.setUser(user);
        order.setTotalAmount(totalPrice);
        order.setStatus(OrderStatus.CONFIRMED);

        List<OrderItems> orderItems = items.stream()
                .map(cartItem -> new OrderItems(
                        null,
                        cartItem.getProduct(),
                        cartItem.getPrice(),
                        cartItem.getQuantity(),
                        order
                )).toList();

        order.setItems(orderItems);

        Order saveOrder = orderRepository.save(order);


        //clear the cart
        cartService.clearCart(userId);

        return Optional.of(mapToOrderResponse(saveOrder));


    }


    //creating the response for the order
    private OrderResponse mapToOrderResponse(Order saveOrder) {

        OrderResponse response = new OrderResponse();
        response.setId(saveOrder.getId());
        response.setItems(saveOrder.getItems().stream().map(
                orderItems -> new OrderItemsDTO(
                        orderItems.getId(),
                        orderItems.getProduct().getId(),
                        orderItems.getPrice(),
                        orderItems.getQuantity(),
                        orderItems.getPrice().multiply(BigDecimal.valueOf(orderItems.getQuantity()))
                )
        ).toList());
        response.setPrice(saveOrder.getTotalAmount());
        response.setStatus(saveOrder.getStatus());
        response.setCreatedAt(saveOrder.getCreatedAt());

        return response;

    }
}
