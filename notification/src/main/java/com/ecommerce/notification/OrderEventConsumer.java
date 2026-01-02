package com.ecommerce.notification;


import com.ecommerce.notification.payload.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

@Service
@Slf4j
public class OrderEventConsumer {

//    @RabbitListener(queues = "${rabbitmq.queue.name}")
//    public void orderEventHandle(OrderCreatedEvent orderEvent){
//
//        System.out.println("Received Order Event : "+orderEvent);
//
//        long orderId = orderEvent.getOrderId();
//        OrderStatus orderStatus = orderEvent.getOrderStatus();
//
//        System.out.println("Order ID : "+orderId);
//        System.out.println("Order Status : "+orderStatus);

        //can update database
        //can send notifications
        //can send email confirmations
        //generate invoice


 //   }

    @Bean
    public Consumer<OrderCreatedEvent> orderCreated(){

        return event ->{


            log.info("Received Order created event for Order Id : {}",event.getOrderId());
            log.info("Received Order created event for User Id : {}",event.getUserId());
        };
    }
}
