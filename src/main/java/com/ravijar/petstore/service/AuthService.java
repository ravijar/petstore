package com.ravijar.petstore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ravijar.petstore.model.User;
import com.ravijar.petstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private static final String USERINFO_ENDPOINT = "https://www.googleapis.com/oauth2/v3/userinfo";

    @Autowired
    private UserRepository userRepository;

    private final WebClient webClient = WebClient.create();

    public Mono<User> authenticateAndSaveIfNeeded(String accessToken) {
        return webClient.get()
                .uri(USERINFO_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(json -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode node = mapper.readTree(json);

                        String userId = node.get("sub").asText();
                        String email = node.get("email").asText();
                        String name = node.get("name").asText();

                        User user = new User();
                        user.setUserId(userId);
                        user.setEmail(email);
                        user.setName(name);
                        user.setRole("user");

                        return userRepository.findById(userId)
                                .switchIfEmpty(userRepository.save(user));

                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Failed to parse user info", e));
                    }
                });
    }
}
