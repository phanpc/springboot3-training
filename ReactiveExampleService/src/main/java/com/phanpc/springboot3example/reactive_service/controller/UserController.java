package com.phanpc.springboot3example.reactive_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phanpc.springboot3example.reactive_service.domain.model.User;
import com.phanpc.springboot3example.reactive_service.domain.repository.UserRepository;
import com.phanpc.springboot3example.reactive_service.exceptions.EmailUniquenessException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//phanpc: khai báo một Controller với Reactive cũng tương tự như khai báo Controller thông thường.
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    

    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable String id) {
        return userRepository.findById(id)
                .doOnSuccess(user -> log.info("Found user: " + user))
                .doOnError(error -> log.error("Error finding user", error));
    }

    // phanpc: trong reactive thì tất cả các method đều là async, 
    // return type is Mono<User>, indicating it’s a single asynchronous operation
    @PostMapping
    public Mono<ResponseEntity<User>> createUser(@RequestBody User user) {
        // public Mono<User> createUser(@RequestBody User user) {
        // return userRepository.save(user);

        return userRepository.findByEmail(user.email())
                .flatMap(existingUser -> Mono.error(new EmailUniquenessException("Email already exists!")))
                .then(userRepository.save(user)) // Save the new user if the email doesn't exist
                .map(ResponseEntity::ok) // Map the saved user to a ResponseEntity
                .doOnNext(savedUser -> System.out.println("New user created: " + savedUser)) // Logging or further action
                .onErrorResume(e -> { // Handling errors, such as email uniqueness violation
                    System.out.println("An exception has occured: " + e.getMessage());
                    if (e instanceof EmailUniquenessException) {
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.CONFLICT).build());
                    } else {
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build());
                    }
                });
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable String id) {
        return userRepository.deleteById(id);
    }

    //phanpc: Implementing backpressure in a reactive stream
    //Sử dụng cơ chế backpressure để giảm tải cho server khi có nhiều request đến cùng một lúc. (overmemory, poor performance, system crash)
    @GetMapping
    public Flux<User> getAllUsers() {
        long start = System.currentTimeMillis();
        return userRepository.findAll()
                .doOnSubscribe(subscription -> log.debug("Subscribed to User stream!"))
                .doOnNext(user -> log.debug("Processed User: {} in {} ms", user.name(), System.currentTimeMillis() - start))
                .doOnComplete(() -> log.info("Finished streaming users for getAllUsers in {} ms", System.currentTimeMillis() - start));
    }

    //phanpc: Implementing backpressure in a reactive stream using built-in mechanisms
    // the backpressure is allowing the system to handle the load more gracefully
    // cách này giúp giảm tải cho server khi có nhiều request đến cùng một lúc. so với cách trên làm tăng khả năng stress test cho hệ thống
    @GetMapping("/stream")
    public Flux<User> streamUsers() {
        long start = System.currentTimeMillis();
        return userRepository.findAll()
                .onBackpressureBuffer()  // Buffer strategy for back-pressure
                .doOnNext(user -> log.debug("Processed User: {} in {} ms", user.name(), System.currentTimeMillis() - start))
                .doOnError(error -> log.error("Error streaming users", error))
                .doOnComplete(() -> log.info("Finished streaming users for streamUsers in {} ms", System.currentTimeMillis() - start));
    }
}