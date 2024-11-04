package com.epam.resourceservice.service;

import com.epam.resourceservice.DTO.SongDTO;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.repository.ResourceRepository;
import com.groupdocs.metadata.Mp3Format;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    private RestTemplate restTemplate = new RestTemplate();



    public Resource saveResource(byte[] file) {
        Resource resource = new Resource();
        resource.setData(file);
        Resource saved;
        try {
            saved = resourceRepository.save(resource);
            saveSongMetadata(saved, file);

            return saved;
        } catch (Exception e) {
            resourceRepository.delete(resource);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void saveSongMetadata(Resource saved, byte[] file) throws RuntimeException {
        Mp3Format mp3Format = new Mp3Format(new ByteArrayInputStream(file));
        // Process built-in MP3 metadata
        System.out.printf("Album: %",
                mp3Format.getId3v1Properties().getAlbum());
        System.out.printf("Title: %",
                mp3Format.getId3v2Properties().getTitle());

        SongDTO songDTO = new SongDTO(
                saved.getId(),
                mp3Format.getId3v2Properties().getTitle(),
                mp3Format.getId3v2Properties().getArtist(),
                mp3Format.getId3v2Properties().getAlbum(),
                mp3Format.getId3v2Properties().getLengthInMilliseconds(),
                mp3Format.getId3v2Properties().getDate()
        );
        SongDTO songDTO1 = restTemplate.postForObject("http://localhost:91/api/songs", songDTO, SongDTO.class);
    }

    public Resource getResource(Long id) {
        return resourceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    public void deleteResources(List<Long> ids) {
        resourceRepository.deleteAllById(ids);
    }
}
