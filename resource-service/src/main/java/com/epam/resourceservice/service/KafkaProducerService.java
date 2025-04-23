package com.epam.resourceservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;

import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class  KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.resource}")
    private String topic;

    @Retryable(
            include = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    public void sendResourceUploadedMessage(Long resourceId) {
        kafkaTemplate.send(topic, resourceId.toString());
    }
}