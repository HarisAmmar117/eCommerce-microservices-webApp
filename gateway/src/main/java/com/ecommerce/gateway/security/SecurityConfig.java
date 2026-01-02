package com.ecommerce.gateway.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain webFilterChain(ServerHttpSecurity http){

        return http
                .csrf(csrfSpec -> csrfSpec.disable())
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
//                        .pathMatchers("/api/products/**").hasRole("PRODUCT")
//                        .pathMatchers("/api/users/**").hasRole("USER")
//                        .pathMatchers("/api/orders/**").hasRole("ORDER")
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec
                        .jwt(
                                jwtSpec -> jwtSpec.jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                        ))
                .build();

    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {

        ReactiveJwtAuthenticationConverter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {

            List<String> roles = jwt.getClaimAsMap("resource_access")
                    .entrySet().stream()
                    .filter(entry -> entry.getKey().equals("oauth2-pkce"))
                    .flatMap(entry -> ((Map<String, List<String>>) entry.getValue())
                            .get("roles").stream())
                    .toList();

            System.out.println("Extracted Roles : " + roles);

            return Flux.fromIterable(roles)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role));
        });

        System.out.println("Failed");
        return jwtAuthenticationConverter;
    }

}
