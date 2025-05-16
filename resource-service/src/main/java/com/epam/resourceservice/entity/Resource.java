package com.epam.resourceservice.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String state; // e.g., STAGING, PERMANENT
    @Column
    private String path; // File path in the storage
    // Getters and Setters
}