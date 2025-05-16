package com.epam.resourceservice.service;

import com.epam.resourceservice.DTO.StorageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class StorageServiceClient {

    @Value("${storage.service.url}")
    private String storageServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<StorageDTO> getAllStorages() {
        String url = storageServiceUrl ;
        return List.of(restTemplate.getForObject(url, StorageDTO[].class));

    }

    @CircuitBreaker(include = Exception.class, maxAttempts = 5, resetTimeout = 20000)
    @Retryable(value = {Exception.class, ResourceAccessException.class,Throwable.class,RuntimeException.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public StorageDTO getStorageByType(String storageType) {
        return getAllStorages().stream()
                .filter(storage -> storage.getBucket().equalsIgnoreCase(storageType))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Storage type not found: " + storageType));
    }


    // Fallback for getStorageByType
    @Recover
    public StorageDTO fallbackGetStorageByType(String storageType, Throwable throwable) {
        if ("staging-bucket".equalsIgnoreCase(storageType)) {
            return new StorageDTO(1L, "staging-bucket", "staging-bucket", "/staging");
        } else if ("permanent-bucket".equalsIgnoreCase(storageType)) {
            return new StorageDTO(2L, "permanent-bucket", "permanent-bucket", "/permanent");
        }
        throw new RuntimeException("Fallback: Storage type not found: " + storageType);
    }
}