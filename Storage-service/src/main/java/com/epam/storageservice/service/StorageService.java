package com.epam.storageservice.service;

import com.epam.storageservice.dto.StorageDTO;
import com.epam.storageservice.entity.Storage;
import com.epam.storageservice.repository.StorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageService {

    @Autowired
    private StorageRepository storageRepository;

    public long createStorage(StorageDTO storageDTO) {
        if (!isValidStorageType(storageDTO.getStorageType())) {
            throw new IllegalArgumentException("Invalid storageType. Allowed values: STAGING, PERMANENT.");
        }
        Storage storage = new Storage(storageDTO.getStorageType(), storageDTO.getBucket(), storageDTO.getPath());
        return storageRepository.save(storage).getId();
    }

    public List<StorageDTO> getAllStorages() {
        return storageRepository.findAll().stream()
                .map(storage -> new StorageDTO(storage.getId(), storage.getStorageType(), storage.getBucket(), storage.getPath()))
                .collect(Collectors.toList());
    }

    public List<Long> deleteStorages(String ids) {
        if (ids.length() > 200) {
            throw new IllegalArgumentException("CSV string length exceeds 200 characters.");
        }
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        List<Long> deletedIds = new ArrayList<>();
        idList.forEach(id -> {
            if (storageRepository.existsById(id)) {
                storageRepository.deleteById(id);
                deletedIds.add(id);
            }
        });
        return deletedIds;
    }

    private boolean isValidStorageType(String storageType) {
        return "STAGING".equalsIgnoreCase(storageType) || "PERMANENT".equalsIgnoreCase(storageType);
    }
}