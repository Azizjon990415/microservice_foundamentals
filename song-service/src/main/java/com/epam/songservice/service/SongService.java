package com.epam.songservice.service;

import com.epam.songservice.dto.SongDTO;
import com.epam.songservice.entity.Song;
import com.epam.songservice.exception.BadRequestException;
import com.epam.songservice.exception.ResourceNotFoundException;
import com.epam.songservice.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SongService {
    @Autowired
    private SongRepository songRepository;

    public SongDTO createSong(SongDTO songDTO) {
        return new SongDTO(songRepository.save(new Song(songDTO)));
    }

    public SongDTO getSongByResourceId(Long id) {
        return new SongDTO(songRepository.findByResourceId(id).orElseThrow(() -> new ResourceNotFoundException("Song not found")));
    }
    @Transactional
    public Map<String,List<Long>> deleteSongs(String ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("IDs parameter cannot be empty");
        }
        if (ids.length() > 200) {
            throw new BadRequestException("The CSV string exceeds the maximum allowed length of 200 characters");
        }
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<Long> notFoundIds = idList.stream()
                .filter(id -> !songRepository.existsByResourceId(id))
                .toList();
        idList.removeAll(notFoundIds);;
        songRepository.deleteAllByResourceIdIn(idList);
        return Map.of("ids", idList, "not_found_ids", notFoundIds);
    }
}
