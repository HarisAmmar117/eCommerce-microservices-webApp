package com.ecom.app.controller;

import com.ecom.app.dto.UserRequest;
import com.ecom.app.dto.UserResponse;
import com.ecom.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;



    //getting all users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){

        List<UserResponse> userList = userService.fetchAllUsers();

        if(userList.isEmpty()){

            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id){

       return userService.fetchSingleUser(id)
               .map(ResponseEntity::ok)
               .orElseGet(()->ResponseEntity.notFound().build());

    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id,@RequestBody UserRequest userRequest){

        boolean isUserUdated = userService.editUser(id,userRequest);

        if(isUserUdated){

            return ResponseEntity.ok("User Updated Successfully");


        }


        return ResponseEntity.notFound().build();

    }

    //adding a user and getting all users
    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody UserRequest userRequest){

        userService.createUser(userRequest);

        return ResponseEntity.ok("User Added Successfully");
    }
}
