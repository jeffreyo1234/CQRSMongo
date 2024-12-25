package org.example.listener;

import java.util.Optional;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.command.exceptions.EventProcessingException;
import org.example.command.model.UserCreatedEvent;
import org.example.command.model.UserUpdatedEvent;
import org.example.query.model.UserMongoProjection;
import org.example.query.repo.QueryMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventListener {

  private final QueryMongoRepository queryMongoRepository;
  private static final Logger logger = LoggerFactory.getLogger(KafkaEventListener.class);

  @Transactional
  @KafkaListener(
      topics = "user-events",
      groupId = "${spring.kafka.consumer.group-id}",
      errorHandler = "kafkaErrorHandler")
  public void handleUserEvents(@Payload ConsumerRecord<String, Object> record) {
    Object event = record.value();
    String eventId = record.key();
    logger.info(
        "Received event: {} with key: {}, value type: {}",
        event,
        eventId,
        event != null ? event.getClass().getName() : "null");

    try {
      if (event == null) {
        logger.error("Received null event");
        return;
      }

      if (record.value() instanceof UserCreatedEvent createdEvent) {
        logger.info("Processing UserCreatedEvent: {}", createdEvent);
        handleUserCreated(createdEvent);
      } else if (record.value() instanceof UserUpdatedEvent updatedEvent) {
        logger.info("Processing UserUpdatedEvent: {}", updatedEvent);
        handleUserUpdated(updatedEvent);
      } else {
        logger.warn("Unknown event type: {}", event.getClass().getName());
      }
    } catch (Exception e) {
      logger.error("Error processing event: {} with key: {}", event, eventId, e);
      throw e;
    }
  }

  @Transactional
  private void handleUserCreated(UserCreatedEvent event) {
    // Save to MongoDB repository
    logger.info("Handling UserMongoProjection: {}", event);

    Optional<UserMongoProjection> existingMongoProjection =
        queryMongoRepository.findById(event.getId());
    if (existingMongoProjection.isPresent()) {
      UserMongoProjection projection = existingMongoProjection.get();
      if (event.getVersion() > projection.getVersion()) {
        projection.setName(event.getName());
        projection.setEmail(event.getEmail());
        projection.setVersion(event.getVersion());
        queryMongoRepository.save(projection);
        logger.info("Updated existing UserMongoProjection: {}", projection);
      } else {
        logger.info("Skipping duplicate event with same or lower version: {}", event);
      }
    } else {
      try {
        UserMongoProjection userMongoProjection =
            new UserMongoProjection(
                event.getId(), event.getName(), event.getEmail(), event.getVersion());
        UserMongoProjection savedProjection = queryMongoRepository.save(userMongoProjection);
        logger.info("Saved new UserMongoProjection with ID {}: {}", event.getId(), savedProjection);
      } catch (Exception e) {
        logger.error(
            "Failed to save UserMongoProjection for event ID {}: {}",
            event.getId(),
            e.getMessage(),
            e);
        throw new EventProcessingException("Failed to save UserMongoProjection", e);
      }
    }
  }

  // This method will be called when a UserUpdatedEvent is published
  @Transactional
  private void handleUserUpdated(UserUpdatedEvent event) {
    logger.info("Handling UserUpdatedEvent: {}", event);

    Optional<UserMongoProjection> existingMongoProjection =
        queryMongoRepository.findById(event.getId());
    if (existingMongoProjection.isPresent()) {
      UserMongoProjection projection = existingMongoProjection.get();
      if (event.getVersion() > projection.getVersion()) {
        projection.setName(event.getName());
        projection.setVersion(event.getVersion());
        queryMongoRepository.save(projection);
        logger.info("Updated existing UserMongoProjection: {}", projection);
      } else {
        logger.info("Skipping duplicate event with same or lower version: {}", event);
      }
    } else {
      logger.warn("UserMongoProjection not found for ID: {}", event.getId());
    }
  }
}
