package com.epam.resourceservice.component;

import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.service.AWSS3Service;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class AWSS3ServiceComponentTest {

    private AWSS3Service awsS3Service;
    private S3Mock s3Mock;
    private final String TEST_BUCKET = "test-bucket";
    private final int PORT = 8001;
    private final String ENDPOINT = "http://localhost:" + PORT;

    @BeforeEach
    public void setUp() {
        // Start S3 mock server
        s3Mock = new S3Mock.Builder()
                .withPort(PORT)
                .withInMemoryBackend()
                .build();
        s3Mock.start();

        // Initialize the service with mock configuration
        awsS3Service = new AWSS3Service(
                TEST_BUCKET,
                ENDPOINT,
                "test-secret-key",
                "test-access-key"
        );

        // Create test bucket
        awsS3Service.s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(TEST_BUCKET)
                .build());
    }

    @AfterEach
    public void tearDown() {
        if (s3Mock != null) {
            s3Mock.stop();
        }
    }

    @Test
    public void testSaveSongFile() {
        // Prepare
        Resource resource = new Resource();
        resource.setId(123L);
        byte[] testFile = "test file content".getBytes();

        // Execute
        Boolean result = awsS3Service.saveSongFile(resource, testFile);

        // Verify
        assertTrue(result, "File should be saved successfully");

        // Verify file content
        byte[] retrievedFile = awsS3Service.getFile("123");
        assertArrayEquals(testFile, retrievedFile, "Retrieved file should match the original");
    }

    @Test
    public void testGetFile() {
        // Prepare
        String key = "1";
        byte[] testContent = "test get file content".getBytes();

        // Save a file first
        Resource resource = new Resource();
        resource.setId(Long.parseLong(key));
        awsS3Service.saveSongFile(resource, testContent);

        // Execute
        byte[] retrievedFile = awsS3Service.getFile(key);

        // Verify
        assertNotNull(retrievedFile, "Retrieved file should not be null");
        assertArrayEquals(testContent, retrievedFile, "Retrieved file should match the original");
    }

    @Test
    public void testDownloadFile() throws IOException {
        // Prepare
        String key = "1";
        byte[] testContent = "test download content".getBytes();

        // Save a file first
        Resource resource = new Resource();
        resource.setId(Long.parseLong(key));
        awsS3Service.saveSongFile(resource, testContent);

        // Execute
        byte[] downloadedContent = awsS3Service.download(key).readAllBytes();

        // Verify
        assertNotNull(downloadedContent, "Downloaded content should not be null");
        assertArrayEquals(testContent, downloadedContent, "Downloaded content should match original");
    }
}