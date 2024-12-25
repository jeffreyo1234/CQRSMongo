package org.example.command.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;


import lombok.RequiredArgsConstructor;
import org.example.command.model.User;
import org.example.command.service.UserCommandServiceKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// This controller has POST, PUT and DELETE methods to create, update and delete users.
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/command/users/kafka")
public class UserCommandControllerKakfa {
  private static final Logger logger = LoggerFactory.getLogger(UserCommandControllerKakfa.class);

  private final UserCommandServiceKafkaProducer userCommandServiceKafkaProducer;

  // This method creates a new user
  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody User user) {
    logger.info("User Command Controller Creating user: {}", user);

    User createdUser = userCommandServiceKafkaProducer.createUser(user);
    logger.info("Created user: {}", user);

    return ResponseEntity.ok().contentType(APPLICATION_JSON).body(createdUser);
  }

  // This method updates a user
  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
    logger.info("Updating user: {}", user);
    return ResponseEntity.ok(userCommandServiceKafkaProducer.updateUser(id, user));
  }

  // This method deletes a user
  @DeleteMapping
  public ResponseEntity<Void> deleteUser(@RequestBody Long id) {
    logger.info("Deleting user with Id: {}", id);
    userCommandServiceKafkaProducer.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  // This method prints helloworld
  @GetMapping("/hello")
  public ResponseEntity<String> helloWorld() {
    return ResponseEntity.ok("Hello World");
  }
}
