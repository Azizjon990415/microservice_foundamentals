package com.epam.resourceservice.unitTest;

import com.epam.resourceservice.DTO.StorageDTO;
import com.epam.resourceservice.service.StorageServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StorageServiceClientTest {

    @Autowired
    private StorageServiceClient storageServiceClient;

    @Test
    void testFallbackGetStorageByType() {
        StorageDTO stagingStorage = storageServiceClient.getStorageByType("STAGING");
        assertNotNull(stagingStorage);
        assertEquals("STAGING", stagingStorage.getStorageType());

        StorageDTO permanentStorage = storageServiceClient.getStorageByType("PERMANENT");
        assertNotNull(permanentStorage);
        assertEquals("PERMANENT", permanentStorage.getStorageType());
    }
}