package com.ecommerce.user.services;


import com.ecommerce.user.dto.AddressDTO;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.models.Address;
import com.ecommerce.user.models.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final KeyCloakAdminService keyCloakAdminService;
  //  private List<User> userList = new ArrayList<>();
    private Long nextId = 1L;

    //getting all users
    public List<UserResponse> fetchAllUsers(){

        List<User> userList=userRepository.findAll();

        return userList.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public Optional<UserResponse> fetchSingleUser(String id) {

        return userRepository.findById(id)
                .map(this::mapToUserResponse);

    }


    public boolean editUser(String id, UserRequest updateUserRequest){

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
        String token = keyCloakAdminService.getAdminAccessToken();
        String keycloackUserId = keyCloakAdminService.createUser(token, userRequest);
        User user = new User();
        updateUserFromRequest(userRequest, user);
        user.setKeycloakId(keycloackUserId);

        keyCloakAdminService.assignRealmRoleToUser(userRequest.getUserName(), "USER", keycloackUserId);
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
        response.setKeycloakId(user.getKeycloakId());
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
