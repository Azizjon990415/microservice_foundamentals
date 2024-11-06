package com.epam.songservice.controller;

import com.epam.songservice.dto.SongDTO;
import com.epam.songservice.service.SongService;
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
@RequestMapping("songs")
public class SongController {
    @Autowired
    private SongService songService;

    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity<Map<String, Long>> createSong(@Valid @RequestBody SongDTO songDTO) {
        return ResponseEntity.ok(Map.of("id", songService.createSong(songDTO).getId()));
    }

    @GetMapping(value="/{id}", produces = "application/json")
    public ResponseEntity<SongDTO> getSongByResourceId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(songService.getSongByResourceId(id));
    }

    @DeleteMapping(value = "", produces = "application/json")
    public ResponseEntity<Map<String, List<Long>>> deleteSongs(@RequestParam String id) {

        return ResponseEntity.ok( songService.deleteSongs(id));
    }
}
