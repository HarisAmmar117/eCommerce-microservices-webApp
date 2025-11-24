package com.ecom.app.service;

import com.ecom.app.dto.CartItemRequest;
import com.ecom.app.model.CartItem;
import com.ecom.app.model.Product;
import com.ecom.app.model.User;
import com.ecom.app.repository.CartItemRepository;
import com.ecom.app.repository.ProductRepository;
import com.ecom.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    //implementing the add to cart method
    public boolean addToCart(String userId, CartItemRequest request) {

        //checking if the product exists
        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        if(productOpt.isEmpty())
            return false;

        //getting the product to add to cart
        Product product = productOpt.get();
        if(product.getQuantity()< request.getQuantity())
            return false;


        //checking if the user esxits
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if(userOpt.isEmpty())
            return false;

        //getting the user to whom the cart to add
        User user = userOpt.get();

        //checking whether the product already exists in the cart
        CartItem existingCartItem = cartRepository.findByUserAndProduct(user,product);
        if(existingCartItem != null){

            //update the existing cart item
            existingCartItem.setQuantity(existingCartItem.getQuantity()+ request.getQuantity());
            existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartRepository.save(existingCartItem);
        }
        else{

            //create a new cart item
            CartItem newCartItem = new CartItem();
            newCartItem.setUser(user);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(request.getQuantity());
            newCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));

            cartRepository.save(newCartItem);

        }

        return true;


    }



    //implementing the remove from cart method
    public boolean deleteItemFromCart(String userId, Long productId) {
        //checking if the product exists
        Optional<Product> productOpt = productRepository.findById(productId);
        //checking if the user exists
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));


        //removing the item
        if(productOpt.isPresent()&&userOpt.isPresent()){

            cartRepository.deleteByUserAndProduct(userOpt.get(),productOpt.get());
            return true;
        }

        return false;
    }
}
