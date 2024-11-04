package com.epam.songservice.dto;

import com.epam.songservice.entity.Song;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongDTO {

    @NotNull
    @Positive
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String artist;

    @NotBlank
    @Size(max = 100)
    private String album;

    @NotBlank
    @Size(max = 10)
    private String length;

    @NotNull
    @Positive
    private Long resourceId;

    @NotBlank
    @Size(max = 4)
    private String year;

    public  SongDTO(Song song) {
        this.id = song.getId();
        this.name = song.getName();
        this.artist = song.getArtist();
        this.album = song.getAlbum();
        this.length = song.getLength();
        this.resourceId = song.getResourceId();
        this.year = song.getYear();
    }
}