package org.example.command.producer;


import lombok.RequiredArgsConstructor;
import org.example.command.model.UserCreatedEvent;
import org.example.command.model.UserUpdatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// This is a producer class that will send messages to the Kafka topic
@RequiredArgsConstructor
@Component
public class KafkaEventProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendUserCreatedEvent(UserCreatedEvent event) {
    kafkaTemplate.send("user-events", event);
  }

  public void sendUserUpdatedEvent(UserUpdatedEvent event) {
    kafkaTemplate.send("user-events", event);
  }
}
