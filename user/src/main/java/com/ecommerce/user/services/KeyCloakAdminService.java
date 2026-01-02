package com.ecommerce.user.services;

import com.ecommerce.user.dto.UserRequest;
import jakarta.ws.rs.core.MultivaluedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeyCloakAdminService {

    @Value("${keycloak.admin.username}")
    private String adminUserName;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Value("${keycloak.admin.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-uid}")
    private String clientUid;

    private final RestTemplate restTemplate = new RestTemplate();


    public String getAdminAccessToken(){

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("username",adminUserName);
        params.add("password",adminPassword);
        params.add("grant_type","password");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, httpHeaders);

        String url = keycloakServerUrl+"/realms/"+realm+"/protocol/openid-connect/token";
        ResponseEntity<Map> response =restTemplate.postForEntity(

                url,
                entity,
                Map.class
        );

        return (String) response.getBody().get("access_token");



    }

    public String createUser(String token, UserRequest userRequest){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("username", userRequest.getUserName());
        userPayload.put("email", userRequest.getEmail());
        userPayload.put("enabled", true);
        userPayload.put("firstName", userRequest.getFirstName());
        userPayload.put("lastName", userRequest.getLastName());

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", userRequest.getPassword());
        credentials.put("temporary",false);

        userPayload.put("credentials", List.of(credentials));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userPayload, headers);


        String url = keycloakServerUrl+"/admin/realms/"+realm+"/users";

        ResponseEntity<Map> response =restTemplate.postForEntity(

                url,
                entity,
                Map.class
        );

        if(!HttpStatus.CREATED.equals(response.getStatusCode())){

            throw new RuntimeException("Failed to create user in keycloak "+response.getBody());
        }

        //Extract Key Cloak user Id
        URI location = response.getHeaders().getLocation();
        if(location == null){

            throw new RuntimeException("Did not return location header"+response.getBody());

        }

        String path = location.getPath();

        return path.substring(path.lastIndexOf("/")+1);
    }

    private Map<String, Object> getRealmRoleRepresentation(String token, String roleName){

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = keycloakServerUrl+"/admin/realms/"+realm+"/clients/"+clientUid+"/roles/"+roleName;
        ResponseEntity<Map> response = restTemplate.exchange(

                url,
                HttpMethod.GET,
                entity,
                Map.class


        );

                return response.getBody();

    }

    public void assignRealmRoleToUser(String userName, String roleName, String userId){

        String token = getAdminAccessToken();

        Map<String, Object> roleRep = getRealmRoleRepresentation(
                token, roleName
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<List<Map<String, Object>>> entity =  new HttpEntity<>(List.of(roleRep), headers);

        String url = keycloakServerUrl+"/admin/realms/"+realm+"/users/"+userId+"/role-mappings/clients/"+clientUid;

        ResponseEntity<Void> response = restTemplate.postForEntity(

                url,
                entity,
                Void.class
        );

        if(!response.getStatusCode().is2xxSuccessful()){

            throw new RuntimeException(
                    "Failed to assign role "+roleName+" to user "+userName+": HTTP "+response.getStatusCode()
            );
        }



    }



}
