package com.epam.resourceservice.config;

import com.fasterxml.jackson.databind.ser.std.NumberSerializers;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topic.resource}")
    private String topic;

    @Bean
    public NewTopic resourceTopic() {
        return new NewTopic(topic, 1, (short) 1);
    }

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;



    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory producerFactory) {
        return new KafkaTemplate<String, String>(producerFactory);
    }
}