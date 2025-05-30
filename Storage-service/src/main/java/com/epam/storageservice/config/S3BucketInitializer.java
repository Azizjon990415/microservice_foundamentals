package com.epam.storageservice.config;

import com.epam.storageservice.entity.Storage;
import com.epam.storageservice.repository.StorageRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;

import java.net.URI;


@Configuration
public class S3BucketInitializer {

    private  String ACCESS_KEY;

    private  String SECRET_KEY;

    private  String AWS_URL;

    Region region = Region.US_EAST_1;
    public S3Client s3Client;
    @Autowired
    private StorageRepository storageRepository;

    public S3BucketInitializer(@Value("${aws.url}") String awsUrl, @Value("${aws.s3.secret-key}") String secretKey, @Value("${aws.s3.access-key}") String accessKey) {
        this.ACCESS_KEY = accessKey;
        this.SECRET_KEY = secretKey;
        this.AWS_URL = awsUrl;
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(AWS_URL))
                .forcePathStyle(true)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                .region(region)

                .build();
    }



    @PostConstruct
    public void createBuckets() {
        createBucket("staging-bucket");
        createBucket("permanent-bucket");
    }

    private void createBucket(String bucketName) {
        if (!s3Client.listBuckets().buckets().stream().anyMatch(b -> b.name().equals(bucketName))) {
            CreateBucketResponse bucket = s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            Storage storage = new Storage("S3", bucketName,AWS_URL);
            storageRepository.save(storage);
        }else {
            if (!storageRepository.existsByBucket(bucketName)) {
                Bucket bucket = s3Client.listBuckets().buckets().stream().filter(b -> b.name().equals(bucketName)).findFirst().get();
                Storage storage = new Storage("S3", bucketName, AWS_URL);
                storageRepository.save(storage);
            }
        }
    }
}








































