package org.example.query.service;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.query.model.UserMongoProjection;
import org.example.query.repo.QueryMongoRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class QueryMongoService {
  private final QueryMongoRepository queryMongoRepository;

  public UserMongoProjection getUserById(Long id) {
    return queryMongoRepository.findById(id).orElse(null);
  }

  public List<UserMongoProjection> getAllUsers() {
    return queryMongoRepository.findAll();
  }
}
