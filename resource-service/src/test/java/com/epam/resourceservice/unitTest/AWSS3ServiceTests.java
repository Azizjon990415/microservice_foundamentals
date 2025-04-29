package com.epam.resourceservice.unitTest;

import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.service.AWSS3Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.Mockito.*;

class AWSS3ServiceTests {

    @Mock
    private S3Client s3Client;

    private AWSS3Service awss3Service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        awss3Service = new AWSS3Service("test-bucket", "http://localhost:4566", "test-secret", "test-access");
        awss3Service.s3Client = s3Client; // Inject mocked S3Client
    }

    @Test
    void savesSongFileSuccessfully() {
        Resource resource = new Resource();
        resource.setId(1L);
        byte[] file = "test-data".getBytes();

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenReturn(new ResponseInputStream<>(GetObjectResponse.builder().build(), new ByteArrayInputStream(file)));

        boolean result = awss3Service.saveSongFile(resource, file);

        Assertions.assertTrue(result);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
    }

    @Test
    void throwsExceptionWhenSavingFails() {
        Resource resource = new Resource();
        resource.setId(1L);
        byte[] file = "test-data".getBytes();

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(SdkException.class);

        Assertions.assertThrows(RuntimeException.class, () -> awss3Service.saveSongFile(resource, file));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void retrievesFileSuccessfully() throws IOException {
        byte[] file = "test-data".getBytes();
        ResponseInputStream<GetObjectResponse> responseInputStream = new ResponseInputStream<>(
                GetObjectResponse.builder().build(),
                new ByteArrayInputStream(file)
        );

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);

        byte[] result = awss3Service.getFile("test-key");

        Assertions.assertArrayEquals(file, result);
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
    }

    @Test
    void throwsExceptionWhenRetrievingFileFails() {
        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(SdkException.class);

        Assertions.assertThrows(RuntimeException.class, () -> awss3Service.getFile("test-key"));
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
    }

    @Test
    void deletesFileSuccessfully() {
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenThrow(NoSuchKeyException.class);

        boolean result = awss3Service.deleteFile("test-key");

        Assertions.assertTrue(result);
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
        verify(s3Client, times(1)).headObject(any(HeadObjectRequest.class));
    }

    @Test
    void returnsFalseWhenFileStillExistsAfterDelete() {
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(DeleteObjectResponse.builder().build());
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(HeadObjectResponse.builder().build());

        boolean result = awss3Service.deleteFile("test-key");

        Assertions.assertFalse(result);
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
        verify(s3Client, times(1)).headObject(any(HeadObjectRequest.class));
    }
}