package com.epam.resourceservice.unitTest;

import com.epam.resourceservice.DTO.ResourceDTO;
import com.epam.resourceservice.DTO.SongDTO;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.BadRequestException;
import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.service.AWSS3Service;
import com.epam.resourceservice.service.KafkaProducerService;
import com.epam.resourceservice.service.ResourceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class ResourceServiceTests {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private AWSS3Service awss3Service;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ResourceService resourceService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void savesValidResourceSuccessfully() {
        byte[] file = new byte[]{'I', 'D', '3', 0x00, 0x01, 0x02};
        Long categoryId = 1L;
        Resource resource = new Resource();
        resource.setId(2L);

   when(restTemplate.getForObject(anyString(), eq(SongDTO.class))).thenReturn(new SongDTO(1L, "Test Song", "Test Artist", "Test Album", "3:45", "2023"));
        when(awss3Service.saveSongFile(any(Resource.class), eq(file))).thenReturn(true);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        ResourceDTO result = resourceService.saveResource(file);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2L, result.id());
        verify(awss3Service, times(1)).saveSongFile(any(Resource.class), eq(file));
        verify(resourceRepository, times(2)).save(any(Resource.class));
    }
    @Test
    void throwsExceptionWhenSavingInvalidFile() {
        byte[] invalidFile = new byte[]{0x00, 0x01, 0x02};
        Long categoryId = 1L;

        Assertions.assertThrows(RuntimeException.class, () -> resourceService.saveResource(invalidFile));
    }
    @Test
    void deletesResourcesSuccessfully() {
        String ids = "1,2,3";
        List<Long> idList = List.of(1L, 2L, 3L);

        when(resourceRepository.existsById(anyLong())).thenReturn(true);
        when(awss3Service.deleteFiles(anyList())).thenReturn(true);
        doNothing().when(resourceRepository).deleteAllById(idList);

        Map<String, List<Long>> result = resourceService.deleteResources(ids);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(idList, result.get("ids"));
        verify(awss3Service, times(1)).deleteFiles(idList);
    }
    @Test
    void throwsExceptionForInvalidCsvLength() {
        String ids = "1,".repeat(201);

        Assertions.assertThrows(BadRequestException.class, () -> resourceService.deleteResources(ids));
    }
    @Test
    void throwsExceptionForNonExistentIds() {
        String ids = "1,2,3";
        when(resourceRepository.existsById(1L)).thenReturn(false);
        when(resourceRepository.existsById(2L)).thenReturn(false);
        when(resourceRepository.existsById(3L)).thenReturn(false);

        Map<String, List<Long>> result = resourceService.deleteResources(ids);

        Assertions.assertTrue(result.get("ids").isEmpty());
        verify(resourceRepository, never()).deleteAllById(anyList());
    }
}