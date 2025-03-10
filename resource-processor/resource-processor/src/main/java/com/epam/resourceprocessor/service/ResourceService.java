package com.epam.resourceprocessor.service;

import com.epam.resourceprocessor.DTO.ResourceDTO;
import com.epam.resourceprocessor.DTO.SongDTO;
import com.epam.resourceprocessor.exception.BadRequestException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class ResourceService {

    @Value("${song.service.url}")
    private String songServiceUrl;






    private SongDTO getSongMetaData(ResourceDTO saved, byte[] file) {
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
                   saved.id(),
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
}


