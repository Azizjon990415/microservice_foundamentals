package com.epam.resourceservice.service;

import com.epam.resourceservice.DTO.ResourceDTO;
import com.epam.resourceservice.DTO.SongDTO;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.BadRequestException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.repository.ResourceRepository;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    private RestTemplate restTemplate = new RestTemplate();
    @Value("${song.service.url}")
    private String songServiceUrl;

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
        SongDTO songDTO;
        try {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();

            ParseContext pcontext = new ParseContext();

            //Mp3 parser
            Mp3Parser  mp3Parser = new  Mp3Parser();
            mp3Parser.parse(new ByteArrayInputStream(file), handler, metadata, pcontext);


          // Process built-in MP3 metadata
           if (metadata.get(Metadata.TITLE)==null ||metadata.get(Metadata.TITLE).equals("")||metadata.get(XMPDM.ARTIST)==null||metadata.get(XMPDM.ARTIST).equals("")||metadata.get(XMPDM.ALBUM)==null||metadata.get(XMPDM.ALBUM).equals("")||metadata.get(XMPDM.RELEASE_DATE)==null||metadata.get(XMPDM.RELEASE_DATE).equals("")) {
               throw new BadRequestException("Invalid MP3 file");
           }
           songDTO = new SongDTO(
                  saved.getId(),
                  metadata.get(Metadata.TITLE),
                  metadata.get(XMPDM.ARTIST)!=null?metadata.get(XMPDM.ARTIST):metadata.get("Author"),

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
        try {
            SongDTO songDTO1 = restTemplate.postForObject(songServiceUrl, songDTO, SongDTO.class);
        }catch (Exception e){
            throw new RuntimeException("Song service unavailable", e);
        }
    }

    public byte[] getResource(Long id) {
        return resourceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found")).getData();
    }
    @Transactional
    public Map<String,List<Long>> deleteResources(String ids) {
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
        if (idList.size()>0)
            restTemplate.delete(songServiceUrl+"?id="+idList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")), Map.class);
        return Map.of("ids", idList);
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
}
