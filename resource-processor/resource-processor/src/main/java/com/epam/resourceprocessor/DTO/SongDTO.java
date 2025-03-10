package com.epam.resourceprocessor.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class SongDTO {
    private Long id;
    private Long resourceId;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;

    public SongDTO(Long resourceId,String name, String artist, String album, String length, String year) {
        this.resourceId = resourceId;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.duration = length;
        this.year = year;
    }
}
