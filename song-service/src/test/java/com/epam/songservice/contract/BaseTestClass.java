package com.epam.songservice.contract;

import com.epam.songservice.controller.SongController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
@AutoConfigureMessageVerifier
public class BaseTestClass {

    @Autowired
    private SongController songController;

    @BeforeEach()
    public void setup() {
        StandaloneMockMvcBuilder standaloneMockMvcBuilder
          = MockMvcBuilders.standaloneSetup(songController);
        RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);
    }
}