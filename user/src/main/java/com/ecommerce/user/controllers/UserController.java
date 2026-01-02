package com.ecommerce.user.controllers;


import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;


//    private static Logger logger = LoggerFactory.getLogger(UserController.class);



    //getting all users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){

        List<UserResponse> userList = userService.fetchAllUsers();

        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id){

        log.info("Request recieved for user: {}",id);
       return userService.fetchSingleUser(id)
               .map(ResponseEntity::ok)
               .orElseGet(()->ResponseEntity.notFound().build());

    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable String id,@RequestBody UserRequest userRequest){

        boolean isUserUdated = userService.editUser(id,userRequest);

        if(isUserUdated){

            return ResponseEntity.ok("User Updated Successfully");


        }


        return ResponseEntity.notFound().build();

    }


    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody UserRequest userRequest){

        userService.createUser(userRequest);

        return ResponseEntity.ok("User Added Successfully");
    }
}
