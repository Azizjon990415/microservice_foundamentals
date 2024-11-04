package com.epam.songservice.service;

import com.epam.songservice.dto.SongDTO;
import com.epam.songservice.entity.Song;
import com.epam.songservice.exception.ResourceNotFoundException;
import com.epam.songservice.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongService {
    @Autowired
    private SongRepository songRepository;

    public SongDTO createSong(SongDTO songDTO) {
        return new SongDTO(songRepository.save(new Song(songDTO)));
    }

    public SongDTO getSongById(Long id) {
        return new SongDTO(songRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Song not found")));
    }

    public void deleteSongs(List<Long> ids) {
        songRepository.deleteAllById(ids);
    }
}
