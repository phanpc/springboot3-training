package com.phanpc.springboot3.example.simpleservice.domain.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import com.phanpc.springboot3.example.simpleservice.domain.model.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, String> {
    Mono<User> findByEmail(String email);
}