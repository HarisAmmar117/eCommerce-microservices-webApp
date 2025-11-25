package com.ecom.app.controller;

import com.ecom.app.dto.CartItemRequest;
import com.ecom.app.dto.ProductResponse;
import com.ecom.app.model.CartItem;
import com.ecom.app.service.CartService;
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
            return ResponseEntity.badRequest().body("Product out of stock or Product not found or User not found");


        return ResponseEntity.status(HttpStatus.CREATED).build();


    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable Long productId){

        boolean result = cartService.deleteItemFromCart(userId, productId);

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
