package com.epam.resourceservice.service;

import com.epam.resourceservice.DTO.ResourceDTO;
import com.epam.resourceservice.DTO.SongDTO;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.BadRequestException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.repository.ResourceRepository;
import com.groupdocs.metadata.Mp3Format;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    private RestTemplate restTemplate = new RestTemplate();

    public void isMp3File(byte[] file) {
        if (file.length < 3) {
            throw new BadRequestException("Invalid MP3 file");
        }

        // Check for the ID3 tag (MP3 files often start with this)
        if (file[0] == 'I' && file[1] == 'D' && file[2] == '3') {
            return ;
        }

        // Check for the MPEG-1 header (alternative method)
        if (file[0] == (byte) 0xFF && (file[1] & 0xF0) == 0xF0) {
            return ;
        }
        throw new BadRequestException("Invalid MP3 file");
    }

    public ResourceDTO saveResource(byte[] file) {
        Resource resource = new Resource();
        resource.setData(file);
        Resource saved;
        saved = resourceRepository.save(resource);
        saveSongMetadata(saved, file);
        return new ResourceDTO(saved.getId());

    }

    private void saveSongMetadata(Resource saved, byte[] file) {
        Mp3Format mp3Format = new Mp3Format(new ByteArrayInputStream(file));
        // Process built-in MP3 metadata

        SongDTO songDTO = new SongDTO(
                saved.getId(),
                mp3Format.getId3v2Properties().getTitle(),
                mp3Format.getId3v2Properties().getArtist(),
                mp3Format.getId3v2Properties().getAlbum(),
                mp3Format.getId3v2Properties().getLengthInMilliseconds(),
                mp3Format.getId3v2Properties().getDate()
        );
        try {
            SongDTO songDTO1 = restTemplate.postForObject("http://localhost:91/songs", songDTO, SongDTO.class);
        }catch (Exception e){
            throw new RuntimeException("Song service unavailable", e);
        }
    }

    public byte[] getResource(Long id) {
        return resourceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found")).getData();
    }
    @Transactional
    public Map<String,List<Long>> deleteResources(String ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("IDs parameter cannot be empty");
        }
        if (ids.length() > 200) {
            throw new BadRequestException("The CSV string exceeds the maximum allowed length of 200 characters");
        }
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<Long> notFoundIds = idList.stream()
                .filter(id -> !resourceRepository.existsById(id))
                .toList();
        idList.removeAll(notFoundIds);
        resourceRepository.deleteAllById(idList);
        restTemplate.delete("http://localhost:91/songs?id="+idList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")), Map.class);
        return Map.of("ids", idList, "not_found_ids", notFoundIds);
    }
}
