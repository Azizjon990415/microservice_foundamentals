package com.epam.resourceservice.service;

import com.epam.resourceservice.DTO.StorageDTO;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@Service
public class AWSS3Service {

    private final String ACCESS_KEY;

    private final String SECRET_KEY;

    private final String AWS_URL;

    private String BUCKET_NAME;
    Region region = Region.US_EAST_1;
    public S3Client s3Client;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private StorageServiceClient storageServiceClient;

    public AWSS3Service(@Value("${aws.bucket-name}") String bucketName, @Value("${aws.url}") String awsUrl, @Value("${aws.s3.secret-key}") String secretKey, @Value("${aws.s3.access-key}") String accessKey) {
        ACCESS_KEY = accessKey;
        SECRET_KEY = secretKey;
        AWS_URL = awsUrl;
        BUCKET_NAME = bucketName;
        s3Client = S3Client.builder()
                .endpointOverride(URI.create(AWS_URL))
                .forcePathStyle(true)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                .region(region)

                .build();
    }

    public void setBucketName(String bucketName) {
        this.BUCKET_NAME = bucketName;
    }

    @Retryable(
            include = {Exception.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    public Boolean saveSongFile(Resource saved, byte[] file) {
        // Creating the PUT request with all the relevant information.
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(saved.getId().toString())
                .build();

        // Put the file into the S3 bucket
        PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file));

        // Creating the GET request with all the relevant information.
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(saved.getId().toString())
                .build();

        // Retrieving the object from the bucket.
        ResponseInputStream<GetObjectResponse> savedResponse = s3Client.getObject(getObjectRequest);
        return savedResponse != null && savedResponse.response() != null && savedResponse.response().getClass().equals(GetObjectResponse.class);
    }

    /**
     * Retrieves the file from S3 as a stream.
     */
    public ResponseInputStream<GetObjectResponse> download(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build();
            return s3Client.getObject(getObjectRequest);
        } catch (SdkException e) {

            throw new RuntimeException("Failed to retrieve file from S3 for key " + key, e);
        }
    }

    /**
     * Downloads the file from S3 and returns its contents as a byte array.
     */
    public byte[] getFile(String key) {
        try (ResponseInputStream<GetObjectResponse> s3Stream = download(key)) {
            return s3Stream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file from S3 for key " + key, e);
        }
    }

    @Retryable(
            include = {Exception.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    public boolean deleteFile(Long key) {
        Resource resource = resourceRepository.findById(key).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        StorageDTO storage = storageServiceClient.getStorageByType(resource.getState());
        setBucketName(storage.getBucket());
        // Creating the DELETE request with all the relevant information.
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key.toString())
                .build();

        // Deleting the object from the bucket.
        s3Client.deleteObject(deleteObjectRequest);
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key.toString())
                    .build());
            return false; // File still exists
        } catch (NoSuchKeyException e) {
            return true; // File is deleted
        }
    }

    public boolean deleteFiles(List<Long> keys) {
        boolean allDeleted = true;
        for (Long key : keys) {
            if (!deleteFile(key)) {
                allDeleted = false;
            }
        }
        return allDeleted;
    }
}
