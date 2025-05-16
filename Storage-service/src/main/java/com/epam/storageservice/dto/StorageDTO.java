package com.epam.storageservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageDTO {

    @NotNull
    private Long id;
    @NotNull
    private String storageType;

    @NotNull
    private String bucket;

    @NotNull
    private String path;

    // Constructors, Getters, and Setters
}