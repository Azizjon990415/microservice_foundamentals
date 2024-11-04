package com.epam.resourceservice;

import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ResourceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResourceRepository resourceRepository;

    @BeforeEach
    public void setup() {
        resourceRepository.deleteAll();
    }

    @Test
    public void testUploadResource() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.mp3", "audio/mpeg", "test data".getBytes());
        byte[] bytes = file.getBytes();
        bytes[0] = (byte) 0xFF;
        bytes[1] = (byte) 0xFB;

        mockMvc.perform(post("/resources")
                        .contentType("audio/mpeg")
                        .content(bytes))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void testGetResource() throws Exception {
        Resource resource = new Resource();
        resource.setData("test data".getBytes());
        Resource savedResource = resourceRepository.save(resource);

        mockMvc.perform(get("/resources/{id}", savedResource.getId()))
                .andExpect(status().isOk())
                .andExpect(content().bytes("test data".getBytes()));
    }

    @Test
    public void testDeleteResources() throws Exception {
        Resource resource1 = new Resource();
        resource1.setData("test data 1".getBytes());
        Resource resource2 = new Resource();
        resource2.setData("test data 2".getBytes());
        resourceRepository.save(resource1);
        resourceRepository.save(resource2);

        mockMvc.perform(delete("/resources")
                        .param("id", resource1.getId().toString() + "," + resource2.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isArray())
                .andExpect(jsonPath("$.ids.length()").value(2));

        Optional<Resource> deletedResource1 = resourceRepository.findById(resource1.getId());
        Optional<Resource> deletedResource2 = resourceRepository.findById(resource2.getId());

        assert(deletedResource1.isEmpty());
        assert(deletedResource2.isEmpty());
    }
}