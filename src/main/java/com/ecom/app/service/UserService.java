package com.ecom.app.service;

import com.ecom.app.dto.AddressDTO;
import com.ecom.app.dto.UserRequest;
import com.ecom.app.dto.UserResponse;
import com.ecom.app.model.Address;
import com.ecom.app.model.User;
import com.ecom.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
  //  private List<User> userList = new ArrayList<>();
    private Long nextId = 1L;

    //getting all users
    public List<UserResponse> fetchAllUsers(){

        List<User> userList=userRepository.findAll();

        return userList.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public Optional<UserResponse> fetchSingleUser(@PathVariable Long id) {

        return userRepository.findById(id)
                .map(this::mapToUserResponse);

    }


    public boolean editUser(Long id, UserRequest updateUserRequest){

        User user = new User();
        updateUserFromRequest(updateUserRequest, user);

        return userRepository.findById(id)
                .map(userExist ->{

                    updateUserFromRequest(updateUserRequest,userExist);
                    userRepository.save(userExist);
                    return true;
                })
                .orElse(false);
    }


    //auto generate id

    //adding a user and getting all users
    public void createUser(UserRequest userRequest){
        User user = new User();
        updateUserFromRequest(userRequest, user);
        userRepository.save(user);
    }

    private void updateUserFromRequest(UserRequest userRequest, User user) {

        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());

        if(userRequest.getAddress()!=null){

            Address address = new Address();
            address.setStreet(userRequest.getAddress().getStreet());
            address.setCity(userRequest.getAddress().getStreet());
            address.setCountry(userRequest.getAddress().getCountry());
            address.setPostalCode(userRequest.getAddress().getPostalCode());
            user.setAddress(address);

        }
    }


    private UserResponse mapToUserResponse(User user){

        UserResponse response = new UserResponse();
        response.setId(String.valueOf(user.getId()));
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getLastName());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());

        if(user.getAddress()!=null){

            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setCountry(user.getAddress().getCountry());
            addressDTO.setPostalCode(user.getAddress().getPostalCode());
            response.setAddress(addressDTO);
        }

        return response;

    }
}
