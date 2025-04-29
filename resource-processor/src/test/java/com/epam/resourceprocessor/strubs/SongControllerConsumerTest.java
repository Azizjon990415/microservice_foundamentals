package com.epam.resourceprocessor.srubs;


import com.epam.resourceprocessor.DTO.SongDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@AutoConfigureStubRunner(
        ids = "song-service-0.0.1-SNAPSHOT-stubs:8080",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class SongControllerConsumerTest {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void shouldCreateSong() {
        // Arrange
        SongDTO songDTO = new SongDTO();
        songDTO.setName("Test Song");
        songDTO.setArtist("Test Artist");
        songDTO.setDuration("10:20");
        songDTO.setYear("1975");
        songDTO.setAlbum("album");

        // Act
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "http://localhost:8080/songs", songDTO, Void.class);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}