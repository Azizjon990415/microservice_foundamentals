package com.epam.resourceservice.service;

import com.epam.resourceservice.service.ResourceService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ResourceProcessorListener {

    @Autowired
    private ResourceService resourceService;

    @KafkaListener(topics = "${kafka.topic.resource-processed}", groupId = "resource-service-group")
    public void onFileProcessed(ConsumerRecord<String, String> record) {
        Header traceIdHeader = record.headers().lastHeader("X-Trace-Id");
        String traceId = traceIdHeader != null ? new String(traceIdHeader.value()) : "default-trace-id";
        MDC.put("traceId", traceId);
        try {
        resourceService.handleFileProcessed(Long.valueOf(record.value()));
        } finally {
            MDC.clear();
        }
    }


}