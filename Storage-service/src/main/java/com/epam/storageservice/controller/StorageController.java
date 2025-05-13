package com.epam.storageservice.controller;

import com.epam.storageservice.dto.StorageDTO;
import com.epam.storageservice.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storages")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping
    public ResponseEntity<Long> createStorage(@RequestBody StorageDTO storageDTO) {
        try {
            long id = storageService.createStorage(storageDTO);
            return ResponseEntity.ok(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<StorageDTO>> getAllStorages() {
        try {
            return ResponseEntity.ok(storageService.getAllStorages());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<List<Long>> deleteStorages(@RequestParam String id) {
        try {
            return ResponseEntity.ok(storageService.deleteStorages(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}