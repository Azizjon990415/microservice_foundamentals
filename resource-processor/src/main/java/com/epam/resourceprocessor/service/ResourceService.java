package com.epam.resourceprocessor.service;

import com.epam.resourceprocessor.DTO.ResourceDTO;
import com.epam.resourceprocessor.DTO.SongDTO;
import com.epam.resourceprocessor.DTO.SongResponseDTO;
import com.epam.resourceprocessor.exception.BadRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class ResourceService {

    @Value("${song.service.url}")
    private String songServiceUrl;
    @Value("${resource.service.url}")
    private String resourceServiceUrl;
    @Value("${storage.service.url}")
    private String storageServiceUrl;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${kafka.topic.resource-processed}")
    private String resourceProcessedTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "${kafka.topic.resource}", groupId = "${spring.kafka.consumer.group-id}")
    public void processResource(ConsumerRecord<String, String> record) {
        Header traceIdHeader = record.headers().lastHeader("X-Trace-Id");
        String traceId = traceIdHeader != null ? new String(traceIdHeader.value()) : "default-trace-id";
        MDC.put("traceId", traceId);
        try {
            String resourceId = record.value();
            // Make synchronous call to Resource Service to retrieve resource data
            byte[] resourceData = getResourceData(resourceId);

            SongDTO songDTO = getSongMetaData(resourceId, resourceData);
            saveSongMetadata(songDTO);

            notifyResourceService(resourceId);
        } finally {
            MDC.clear();
        }
    }

    @Retryable(value = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    private Map saveSongMetadata(SongDTO songDTO) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonSongDTO = null;
            jsonSongDTO = objectMapper.writeValueAsString(songDTO);
            return restTemplate.postForObject(songServiceUrl, songDTO, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Retryable(value = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    private byte[] getResourceData(String resourceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "audio/mpeg");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(resourceServiceUrl + "/" + resourceId, HttpMethod.GET, entity, byte[].class);
        return response.getBody();
    }


    private SongDTO getSongMetaData(String resourceId, byte[] file) {
        SongDTO songDTO;
        try {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();

            ParseContext pcontext = new ParseContext();

            //Mp3 parser
            Mp3Parser mp3Parser = new Mp3Parser();
            mp3Parser.parse(new ByteArrayInputStream(file), handler, metadata, pcontext);


            // Process built-in MP3 metadata
            if (metadata.get(Metadata.TITLE) == null || metadata.get(Metadata.TITLE).equals("") || metadata.get(XMPDM.ARTIST) == null || metadata.get(XMPDM.ARTIST).equals("") || metadata.get(XMPDM.ALBUM) == null || metadata.get(XMPDM.ALBUM).equals("") || metadata.get(XMPDM.RELEASE_DATE) == null || metadata.get(XMPDM.RELEASE_DATE).equals("")) {
                throw new BadRequestException("Invalid MP3 file");
            }
            songDTO = new SongDTO(
                    Long.valueOf(resourceId),
                    metadata.get(Metadata.TITLE),
                    metadata.get(XMPDM.ARTIST) != null ? metadata.get(XMPDM.ARTIST) : metadata.get("Author"),

                    metadata.get(XMPDM.ALBUM),
                    metadata.get(XMPDM.DURATION) != null ? convertDuration(metadata.get(XMPDM.DURATION)) : "",
                    metadata.get(XMPDM.RELEASE_DATE)
            );
        } catch (TikaException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        return songDTO;
    }

    private String convertDuration(String duration) {

        try {
            double milliseconds = Double.parseDouble(duration);
            int minutes = (int) (milliseconds / 60000);
            int remainingSeconds = (int) ((milliseconds % 60000) / 1000);
            return String.format("%02d:%02d", minutes, remainingSeconds);
        } catch (NumberFormatException e) {
            return "";
        }
    }


    private void notifyResourceService(String resourceId) {
        String traceId = MDC.get("traceId");
        kafkaTemplate.send(resourceProcessedTopic, resourceId).addCallback(
                success -> success.getProducerRecord().headers().add("X-Trace-Id", traceId.getBytes()),
                failure -> {
                }
        );
    }


}


