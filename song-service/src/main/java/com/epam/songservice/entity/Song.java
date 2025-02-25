package com.epam.songservice.entity;

import com.epam.songservice.dto.SongDTO;
import com.epam.songservice.dto.SongResourceDTO;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "songs")
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

    public Song(SongResourceDTO songDTO) {
        this.id=songDTO.getId();
        this.name = songDTO.getName();
        this.artist = songDTO.getArtist();
        this.album = songDTO.getAlbum();
        this.length = songDTO.getDuration();
        this.resourceId = songDTO.getResourceId();
        if (!songDTO.getYear().matches("\\d{4}")) {
            throw new IllegalArgumentException("Year must be in the format YYYY");
        }
        this.year = songDTO.getYear();
    }

    // Getters and Setters
}