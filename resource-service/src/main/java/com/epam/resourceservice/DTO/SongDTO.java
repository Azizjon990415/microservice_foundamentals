package com.epam.resourceservice.DTO;

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
    private String length;
    private String year;

    public SongDTO(Long resourceId,String name, String artist, String album, String length, String year) {
        this.resourceId = resourceId;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.length = length;
        this.year = year;
    }
}
