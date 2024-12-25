package org.example.command.service;

import lombok.RequiredArgsConstructor;
import org.example.command.model.User;
import org.example.command.model.UserCreatedEvent;
import org.example.command.model.UserUpdatedEvent;
import org.example.command.producer.KafkaEventProducer;
import org.example.command.repository.UserRepository;
import org.springframework.stereotype.Service;

// This is a class using the command pattern to create, update, and delete users
@Service
@RequiredArgsConstructor
public class UserCommandServiceKafkaProducer {

  private final UserRepository userRepository;
  private final KafkaEventProducer kafkaEventProducer;

  // This method creates a new user via the userRepository
  public User createUser(User user) {
    user.setVersion(1L);
    User savedUser = userRepository.save(user);
    kafkaEventProducer.sendUserCreatedEvent(
        (new UserCreatedEvent(
            savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getVersion())));
    return savedUser;
  }

  // This method updates a user via the userRepository, updates the version, and publishes a
  // UserUpdatedEvent
  public User updateUser(Long id, User user) {
    User existingUser =
        userRepository
            .findById(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    existingUser.setName(user.getName());
    existingUser.setEmail(user.getEmail());
    existingUser.setVersion(existingUser.getVersion() + 1);
    User savedUser = userRepository.save(existingUser);
    kafkaEventProducer.sendUserUpdatedEvent(
        new UserUpdatedEvent(savedUser.getId(), savedUser.getName(), savedUser.getVersion()));
    return savedUser;
  }

  // This method deletes a user via the userRepository
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }
}
