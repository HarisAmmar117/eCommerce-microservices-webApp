package com.ecommerce.order.service;



//import com.ecom.app.repository.UserRepository;
import com.ecommerce.order.dto.OrderCreatedEvent;
import com.ecommerce.order.dto.OrderItemsDTO;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.models.CartItem;
import com.ecommerce.order.models.Order;
import com.ecommerce.order.models.OrderItems;
import com.ecommerce.order.models.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    private final StreamBridge streamBridge;



    //Implementing create order
    public Optional<OrderResponse> createOrder(String userId) {

        //validate cart item
        List<CartItem> items = cartService.fetchAllCartItems(userId);

        if(items.isEmpty()){

            return Optional.empty();

        }

        // validate user
//        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
//
//        if(userOpt.isEmpty()){
//
//            return Optional.empty();
//
//        }
//
//        User user = userOpt.get();


        //get the total amount
        BigDecimal totalPrice = items.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO,BigDecimal::add);


        //create order
        Order order = new Order();

        order.setUserId(userId);
        order.setTotalAmount(totalPrice);
        order.setStatus(OrderStatus.CONFIRMED);


        List<OrderItems> orderItems = items.stream()
                .map(cartItem -> new OrderItems(
                        null,
                        cartItem.getProductId(),
                        cartItem.getPrice(),
                        cartItem.getQuantity(),
                        order
                )).toList();

        order.setItems(orderItems);

        Order saveOrder = orderRepository.save(order);


        //clear the cart
        cartService.clearCart(userId);

        OrderCreatedEvent event = new  OrderCreatedEvent(
                saveOrder.getId(),
                saveOrder.getUserId(),
                saveOrder.getStatus(),
                mapToOrderItemDTO(saveOrder.getItems()),
                saveOrder.getTotalAmount(),
                saveOrder.getCreatedAt()
        );

       streamBridge.send("createOrder-out-0", event);

        return Optional.of(mapToOrderResponse(saveOrder));


    }

    private List<OrderItemsDTO> mapToOrderItemDTO(List<OrderItems> items){

        return items.stream()
                .map(item -> new OrderItemsDTO(

                        item.getId(),
                        item.getProductId(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))

                )).collect(Collectors.toList());


    }


    //creating the response for the order
    private OrderResponse mapToOrderResponse(Order saveOrder) {

        OrderResponse response = new OrderResponse();
        response.setId(saveOrder.getId());
        response.setItems(saveOrder.getItems().stream().map(
                orderItems -> new OrderItemsDTO(
                        orderItems.getId(),
                        orderItems.getProductId(),
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
