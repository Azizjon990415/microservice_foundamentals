package com.epam.resourceservice.controller;

import com.epam.resourceservice.DTO.ResourceDTO;
import com.epam.resourceservice.exception.BadRequestException;
import com.epam.resourceservice.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;
@Validated
@RestController
@RequestMapping("/resources")
public class ResourceController {

    private ResourceService resourceService;
    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(value = "", consumes = "audio/mpeg", produces = "application/json")
    public ResponseEntity<ResourceDTO> uploadResource(@RequestBody byte[] audioData) {
        resourceService.isMp3File(audioData);
        return ResponseEntity.ok().body(resourceService.saveResource(audioData));
    }

    @GetMapping(value = "/{id}", produces = "audio/mpeg")
    public ResponseEntity<byte[]> getResource(@PathVariable  @Positive Long id) {
        return ResponseEntity.ok().body(resourceService.getResource(id));
    }

    @DeleteMapping(value = "", produces = "application/json")
    public ResponseEntity<Map<String, List<Long>>> deleteResources(@RequestParam String id) {
        return ResponseEntity.ok().body(resourceService.deleteResources(id));
    }


}
