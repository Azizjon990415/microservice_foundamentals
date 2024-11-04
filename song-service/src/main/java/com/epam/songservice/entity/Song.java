package com.epam.songservice.entity;

import com.epam.songservice.dto.SongDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;

    @Column(name = "artist")
    private String artist;

    @Column(name = "album")
    private String album;

    @Column(name = "length")
    private String length;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "year")
    private String year;

    public Song(SongDTO songDTO) {
        this.name = songDTO.getName();
        this.artist = songDTO.getArtist();
        this.album = songDTO.getAlbum();
        this.length = songDTO.getLength();
        this.resourceId = songDTO.getResourceId();
        this.year = songDTO.getYear();
    }

    // Getters and Setters
}