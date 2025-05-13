package com.epam.resourceservice.service;

import com.epam.resourceservice.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ResourceProcessorListener {

    @Autowired
    private ResourceService resourceService;

    @KafkaListener(topics = "${kafka.topic.resource-processed}", groupId = "resource-service-group")
    public void onFileProcessed(Long resourceId) {
        resourceService.handleFileProcessed(resourceId);
    }


}