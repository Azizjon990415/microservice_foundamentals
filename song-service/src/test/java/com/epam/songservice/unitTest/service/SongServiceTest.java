package com.epam.songservice.unitTest.service;

import com.epam.songservice.dto.SongDTO;
import com.epam.songservice.dto.SongResourceDTO;
import com.epam.songservice.entity.Song;
import com.epam.songservice.repository.SongRepository;
import com.epam.songservice.service.SongService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SongServiceTest {

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private SongService songService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSong() {
        SongResourceDTO songDTO = new SongResourceDTO(1L, "Song Name", "Artist", "Album", "3:45", 123L, "2023");
        Song song = new Song(songDTO);

        when(songRepository.save(any(Song.class))).thenReturn(song);

        SongDTO createdSong = songService.createSong(songDTO);

        assertNotNull(createdSong);
        assertEquals(songDTO.getName(), createdSong.getName());
        verify(songRepository, times(1)).save(any(Song.class));
    }

    @Test
    void testGetSongByResourceId() {
        Long resourceId = 123L;
        Song song = new Song(1L, "Song Name", "Artist", "Album", "3:45", resourceId, "2023");

        when(songRepository.findByResourceId(resourceId)).thenReturn(Optional.of(song));

        SongDTO foundSong = songService.getSongByResourceId(resourceId);

        assertNotNull(foundSong);
        assertEquals(song.getId(), foundSong.getId());
        verify(songRepository, times(1)).findByResourceId(resourceId);
    }

    @Test
    void testDeleteSongs() {
        String ids = "1,2,3";
        List<Long> idList = List.of(1L, 2L, 3L);

        when(songRepository.existsByResourceId(anyLong())).thenReturn(true);

        songService.deleteSongs(ids);

        verify(songRepository, times(1)).deleteAllByResourceIdIn(idList);
    }
}