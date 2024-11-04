package com.epam.songservice.controller;

import com.epam.songservice.dto.SongDTO;
import com.epam.songservice.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("api/songs")
public class SongController {
    @Autowired
    private SongService songService;

    @PostMapping
    public ResponseEntity<SongDTO> createSong(@Valid @RequestBody SongDTO songDTO) {
        return ResponseEntity.ok(songService.createSong(songDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getSongById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(songService.getSongById(id));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteSongs(@RequestParam List<@Positive Long> ids) {
        songService.deleteSongs(ids);
        return ResponseEntity.noContent().build();
    }
}
