package com.epam.songservice.integrationTest.controller;

import com.epam.songservice.dto.SongDTO;
import com.epam.songservice.dto.SongResourceDTO;
import com.epam.songservice.service.SongService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SongControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SongService songService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateSong() throws Exception {
        SongResourceDTO songResourceDTO = new SongResourceDTO(1L, "Song Name", "Artist", "Album", "3:45", 123L, "2023");
        SongDTO songDTO = new SongDTO(1L, "Song Name", "Artist", "Album", "3:45",  "2023");

        when(songService.createSong(any(SongResourceDTO.class))).thenReturn(songDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(songResourceDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(songDTO.getId()));

        verify(songService, times(1)).createSong(any(SongResourceDTO.class));
    }

    @Test
    void testGetSongByResourceId() throws Exception {
        Long resourceId = 123L;
        SongDTO songDTO = new SongDTO(1L, "Song Name", "Artist", "Album", "3:45", "2023");

        when(songService.getSongByResourceId(resourceId)).thenReturn(songDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/songs/{id}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Song Name"));

        verify(songService, times(1)).getSongByResourceId(resourceId);
    }

    @Test
    void testDeleteSongs() throws Exception {
        String ids = "1,2,3";
        Map<String, List<Long>> response = Map.of("ids", List.of(1L, 2L, 3L));

        when(songService.deleteSongs(ids)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.delete("/songs")
                        .param("id", ids))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isArray())
                .andExpect(jsonPath("$.ids[0]").value(1L));

        verify(songService, times(1)).deleteSongs(ids);
    }
}