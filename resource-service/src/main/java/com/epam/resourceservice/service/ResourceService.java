package com.epam.resourceservice.service;

import com.epam.resourceservice.DTO.ResourceDTO;
import com.epam.resourceservice.DTO.SongDTO;
import com.epam.resourceservice.DTO.StorageDTO;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.BadRequestException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private AWSS3Service awss3Service;
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private StorageServiceClient storageServiceClient;
    private RestTemplate restTemplate = new RestTemplate();


    public void isMp3File(byte[] file) {
        if (file.length < 3) {
            throw new BadRequestException("Invalid MP3 file");
        }

        // Check for the ID3 tag (MP3 files often start with this)
        if (file[0] == 'I' && file[1] == 'D' && file[2] == '3') {
            return;
        }

        // Check for the MPEG-1 header (alternative method)
        if (file[0] == (byte) 0xFF && (file[1] & 0xF0) == 0xF0) {
            return;
        }
        throw new BadRequestException("Invalid MP3 file");
    }

    public ResourceDTO saveResource(byte[] file) {
        StorageDTO stagingStorage = storageServiceClient.getStorageByType("staging-bucket");

        Resource saved = resourceRepository.save(new Resource());
        saved.setState("staging-bucket");
        saved.setPath(stagingStorage.getPath());
        awss3Service.setBucketName(stagingStorage.getBucket()); // Dynamically set bucket
        if (awss3Service.saveSongFile(saved, file)) {
            saved = resourceRepository.save(saved);
        } else {
            throw new RuntimeException("Error saving file");
        }
        kafkaProducerService.sendResourceUploadedMessage(saved.getId());
        return new ResourceDTO(saved.getId());

    }

    public byte[] getResource(Long id) {
        Resource resource = resourceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        StorageDTO storage = storageServiceClient.getStorageByType(resource.getState());
        awss3Service.setBucketName(storage.getBucket()); // Dynamically set bucket
        return awss3Service.getFile(id.toString());
    }

    @Transactional
    public Map<String, List<Long>> deleteResources(String ids) {
        if (ids.length() > 200) {
            throw new BadRequestException("The CSV string exceeds the maximum allowed length of 200 characters");
        }


        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<Long> notFoundIds = idList.stream()
                .filter(id -> !resourceRepository.existsById(id))
                .toList();
        idList.removeAll(notFoundIds);
        if (!awss3Service.deleteFiles(idList) && notFoundIds.isEmpty()) {
            throw new RuntimeException("Error deleting files");
        }
        return Map.of("ids", idList);
    }
    public void handleFileProcessed(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        StorageDTO permanentStorage = storageServiceClient.getStorageByType("permanent-bucket");
        awss3Service.setBucketName(permanentStorage.getBucket());
        byte[] file = awss3Service.getFile(resource.getId().toString());


        if (!awss3Service.saveSongFile(resource, file)) {
            throw new RuntimeException("Error moving file to PERMANENT storage");
        }
        deleteFileFromStagingBucket(resourceId);
        resource.setState("permanent-bucket");
        resource.setPath(permanentStorage.getPath());
        resourceRepository.save(resource);
    }

    public void deleteFileFromStagingBucket(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        StorageDTO stagingStorage = storageServiceClient.getStorageByType("staging-bucket");
        awss3Service.setBucketName(stagingStorage.getBucket());

        if (!awss3Service.deleteFile(resource.getId())) {
            throw new RuntimeException("Error deleting file from staging bucket");
        }
    }

}


