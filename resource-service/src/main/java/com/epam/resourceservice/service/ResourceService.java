package com.epam.resourceservice.service;

import com.epam.resourceservice.DTO.ResourceDTO;
import com.epam.resourceservice.DTO.SongDTO;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.BadRequestException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private AWSS3Service awss3Service;
    private RestTemplate restTemplate = new RestTemplate();
    @Value("${song.service.url}")
    private String songServiceUrl;

    public void isMp3File(byte[] file) {
        if (file.length < 3) {
            throw new BadRequestException("Invalid MP3 file");
        }

        // Check for the ID3 tag (MP3 files often start with this)
        if (file[0] == 'I' && file[1] == 'D' && file[2] == '3') {
            return;
        }

        // Check for the MPEG-1 header (alternative method)
        if (file[0] == (byte) 0xFF && (file[1] & 0xF0) == 0xF0) {
            return;
        }
        throw new BadRequestException("Invalid MP3 file");
    }

    public ResourceDTO saveResource(byte[] file) {
        Resource resource = new Resource();
        Resource saved;
        try {
            SongDTO songDTO1 = restTemplate.getForObject(songServiceUrl + "/get-last-song", SongDTO.class);
            resource.setId(songDTO1.getId() + 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (awss3Service.saveSongFile(resource, file)) {
            saved = resourceRepository.save(resource);
        } else {
            throw new RuntimeException("Error saving file");
        }
//        saveSongMetadata(saved, file);
        return new ResourceDTO(saved.getId());

    }

    public byte[] getResource(Long id) {
        return awss3Service.getFile(id.toString());
    }

    private void saveSongMetadata(Resource saved, byte[] file) {
        SongDTO songDTO;
        songDTO = getSongMetaData(saved, file);
        try {
            SongDTO songDTO1 = restTemplate.postForObject(songServiceUrl, songDTO, SongDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Song service unavailable", e);
        }
    }

    private SongDTO getSongMetaData(Resource saved, byte[] file) {
        //call resource Processor
        return new SongDTO();
    }


    @Transactional
    public Map<String, List<Long>> deleteResources(String ids) {
//        if (ids == null || ids.isEmpty()) {
//            throw new BadRequestException("IDs parameter cannot be empty");
//        }
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
        if (idList.size() > 0)
            restTemplate.delete(songServiceUrl + "?id=" + idList.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")), Map.class);
        return Map.of("ids", idList);
    }


}


