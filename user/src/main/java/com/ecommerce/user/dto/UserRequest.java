package com.ecommerce.user.dto;

import lombok.Data;

@Data
public class UserRequest {

    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private AddressDTO address;
}
