package com.epam.resourceprocessor.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
public class SongDTO {
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

    public SongDTO() {
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @JsonProperty
    public Long getResourceId() {
        return resourceId;
    }
    @JsonProperty
    public String getName() {
        return name;
    }
    @JsonProperty
    public String getArtist() {
        return artist;
    }
    @JsonProperty
    public String getAlbum() {
        return album;
    }
    @JsonProperty
    public String getDuration() {
        return duration;
    }
    @JsonProperty
    public String getYear() {
        return year;
    }
}
