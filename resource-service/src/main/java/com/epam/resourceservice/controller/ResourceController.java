package com.epam.resourceservice.controller;

import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Positive;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/resources")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @PostMapping(consumes = "audio/mpeg")
    public ResponseEntity<?> uploadResource(@RequestBody byte[] audioData) {
            if (!isMp3File(audioData)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only MP3 files are allowed");
            }
            Resource resource = resourceService.saveResource(audioData);
            return ResponseEntity.ok().body(Map.of("id", resource.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResource(@PathVariable @Positive Long id) {
            Resource resource = resourceService.getResource(id);
            return ResponseEntity.ok().body(resource.getData());
    }

    @DeleteMapping
    public ResponseEntity<?> deleteResources(@RequestParam("id") List<@Positive Long> ids) {
        resourceService.deleteResources(ids);
        return ResponseEntity.ok().body(Map.of("ids", ids));
    }
    private boolean isMp3File(byte[] file) {
        // Check if the file starts with the MP3 header bytes
        return file.length > 2 && file[0] == (byte) 0xFF && file[1] == (byte) 0xFB;
    }
}
