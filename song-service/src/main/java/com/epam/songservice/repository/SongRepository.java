package com.epam.songservice.repository;

import com.epam.songservice.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    Optional<Song> findByResourceId(Long resourceId);
    void deleteAllByResourceIdIn(List<Long> resourceIds);
    boolean existsByResourceId(Long resourceId);
}