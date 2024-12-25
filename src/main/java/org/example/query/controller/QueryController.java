package org.example.query.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.query.model.UserMongoProjection;
import org.example.query.service.QueryMongoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// This controller will have methods to query, read the data
// This is the query side of the CQRS pattern
@RestController
@RequestMapping("/api/query/users")
@RequiredArgsConstructor
public class QueryController {

  private final QueryMongoService queryMongoService;

  // This method returns all users
  @GetMapping
  public ResponseEntity<List<UserMongoProjection>> getAllUsers() {
    return ResponseEntity.ok(queryMongoService.getAllUsers());
  }

  // This method returns a user by id
  @GetMapping("/{id}")
  public ResponseEntity<UserMongoProjection> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(queryMongoService.getUserById(id));
  }
}
