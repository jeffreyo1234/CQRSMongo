package org.example.query.repo;

import org.example.query.model.UserMongoProjection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryMongoRepository extends MongoRepository<UserMongoProjection, Long> {}
