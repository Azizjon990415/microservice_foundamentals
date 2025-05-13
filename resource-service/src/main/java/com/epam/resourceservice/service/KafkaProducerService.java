package com.epam.resourceservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
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
        String traceId = MDC.get("traceId");
        kafkaTemplate.send(topic, resourceId.toString()).addCallback(
                success -> success.getProducerRecord().headers().add("X-Trace-Id", traceId.getBytes()),
                failure -> {}
        );
    }
}