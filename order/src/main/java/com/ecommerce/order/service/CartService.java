package com.ecommerce.order.service;



import com.ecommerce.order.clients.ProductServiceClient;
import com.ecommerce.order.clients.UserServiceClient;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.models.CartItem;
import com.ecommerce.order.repository.CartItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;


    //implementing the add to cart method
    //@CircuitBreaker(name = "productService", fallbackMethod = "fallbackAddToCart")
    @Retry(name = "retryBreaker", fallbackMethod = "fallbackAddToCart")
    public boolean addToCart(String userId, CartItemRequest request) {

        //checking if the product exists
        ProductResponse productResponse = productServiceClient.getProductDetails(request.getProductId());
        if(productResponse == null || productResponse.getQuantity()< request.getQuantity())
            return false;



        //checking if the user esxits
        UserResponse userResponse = userServiceClient.getUserDetails(userId);
        if(userResponse == null)
            return false;

        //getting the user to whom the cart to add


        //checking whether the product already exists in the cart
        CartItem existingCartItem = cartRepository.findByUserIdAndProductId(userId, String.valueOf(request.getProductId()));
        if(existingCartItem != null){

            //update the existing cart item
            existingCartItem.setQuantity(existingCartItem.getQuantity()+ request.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartRepository.save(existingCartItem);
        }
        else{

            //create a new cart item
            CartItem newCartItem = new CartItem();
            newCartItem.setUserId(userId);
            newCartItem.setProductId(request.getProductId());
            newCartItem.setQuantity(request.getQuantity());
            newCartItem.setPrice(BigDecimal.valueOf(1000.00));

            cartRepository.save(newCartItem);

        }

        return true;


    }


    public boolean fallbackAddToCart(String userId, CartItemRequest request, Exception exception){

        exception.printStackTrace();
        return false;
    }



    //implementing the remove from cart method
    public boolean deleteItemFromCart(String userId, String productId) {

//        Optional<Product> productOpt = productRepository.findById(productId);
//
//        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));

        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId, productId);


        //removing the item
        if(cartItem != null){

            cartRepository.delete(cartItem);
            return true;
        }

        return false;
    }

    //Implementing method to get all cart item for a user
    public List<CartItem> fetchAllCartItems(String userId) {

        return cartRepository.findAllByUserId(userId);


    }


    public void clearCart(String userId) {

        cartRepository.deleteByUserId(userId);
    }
}
