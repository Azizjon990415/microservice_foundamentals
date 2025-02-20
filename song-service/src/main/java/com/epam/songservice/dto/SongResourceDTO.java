package com.epam.songservice.dto;

import com.epam.songservice.entity.Song;
import com.epam.songservice.exception.DurationFormat;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.MethodArgumentNotValidException;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongResourceDTO {


    private Long id;
    @NotNull
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotNull
    @NotBlank
    @Size(max = 100)
    private String artist;
    @NotNull
    @NotBlank
    @Size(max = 100)
    private String album;
    @NotNull
    @NotBlank
    @Size(max = 10)
    @DurationFormat
    private String duration;
    @Positive
    private Long resourceId;
    @NotNull
    @NotBlank
    @Size(max = 4)
    private String year;

    public SongResourceDTO(Song song) {
        this.id = song.getId();
        this.name = song.getName();
        this.artist = song.getArtist();
        this.album = song.getAlbum();
        this.duration = song.getLength();
        this.resourceId = song.getResourceId();
        this.year = song.getYear();
    }
}