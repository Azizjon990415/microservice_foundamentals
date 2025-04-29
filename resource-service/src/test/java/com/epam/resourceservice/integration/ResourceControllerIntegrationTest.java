package com.epam.resourceservice.integration;

import com.epam.resourceservice.DTO.ResourceDTO;
import com.epam.resourceservice.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class ResourceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService resourceService;

    @Test
    void retrievesResourceSuccessfully() throws Exception {
        byte[] fileContent = "test-data".getBytes();
        when(resourceService.getResource(1L)).thenReturn(fileContent);

        mockMvc.perform(get("/resources/1"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fileContent));

        verify(resourceService, times(1)).getResource(1L);
    }

    @Test
    void savesResourceSuccessfully() throws Exception {
        ResourceDTO resourceDTO = new ResourceDTO(1L);
        byte[] fileContent = "test-data".getBytes();
        when(resourceService.saveResource(any(byte[].class))).thenReturn(resourceDTO);

        mockMvc.perform(post("/resources")
                .contentType(MediaType.valueOf("audio/mpeg"))
                .content(fileContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(resourceService, times(1)).saveResource(any(byte[].class));
    }

    @Test
    void deletesResourcesSuccessfully() throws Exception {
        when(resourceService.deleteResources("1,2,3")).thenReturn(Map.of("ids", List.of(1L, 2L, 3L)));

        mockMvc.perform(delete("/resources")
                .param("id", "1,2,3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isArray())
                .andExpect(jsonPath("$.ids[0]").value(1L))
                .andExpect(jsonPath("$.ids[1]").value(2L))
                .andExpect(jsonPath("$.ids[2]").value(3L));

        verify(resourceService, times(1)).deleteResources("1,2,3");
    }
}