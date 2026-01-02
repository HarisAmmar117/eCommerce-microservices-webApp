package com.ecommerce.order.controller;


import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.models.CartItem;
import com.ecommerce.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<String> addToCart(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody CartItemRequest request){

        if(!cartService.addToCart(userId, request))
            return ResponseEntity.badRequest().body("Not able to complete the request");


        return ResponseEntity.status(HttpStatus.CREATED).build();


    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable Long productId){

        boolean result = cartService.deleteItemFromCart(userId, String.valueOf(productId));

        return result?ResponseEntity.noContent().build()
                :ResponseEntity.notFound().build();


    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getAllCartItems(
            @RequestHeader("X-User-ID") String userId

            ){

        return new ResponseEntity<>(cartService.fetchAllCartItems(userId),HttpStatus.OK);

    }
}
