package com.epam.resourceservice.cucumber;

import com.epam.resourceservice.DTO.ResourceDTO;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.service.AWSS3Service;
import com.epam.resourceservice.service.ResourceService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ResourceServiceSteps {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private AWSS3Service awsS3Service;

    private ResourceDTO savedResource;
    private byte[] retrievedFileContent;
    private boolean deleteResult;

    @Given("a resource with ID {long} and file content {string}")
    public void aResourceWithIDAndFileContent(Long id, String content) {
        Resource resource = new Resource();
        resource.setId(id);
        byte[] fileContent = content.getBytes();

        resourceRepository.save(resource);
        awsS3Service.saveSongFile(resource, fileContent);

    }

    @When("the resource is saved")
    public void theResourceIsSaved() {
        byte[] fileContent = "test-data".getBytes();
        savedResource = resourceService.saveResource(fileContent);
    }

    @Then("the resource should be saved successfully")
    public void theResourceShouldBeSavedSuccessfully() {
        assertNotNull(savedResource);
    }

    @Given("a resource with ID {long} exists in the system")
    public void aResourceWithIDExistsInTheSystem(Long id) {
        byte[] fileContent = "test-data".getBytes();
        assertArrayEquals(awsS3Service.getFile(id.toString()), fileContent);
    }

    @When("the resource is retrieved")
    public void theResourceIsRetrieved() {
        retrievedFileContent = resourceService.getResource(1L);
    }

    @Then("the file content should be {string}")
    public void theFileContentShouldBe(String expectedContent) {
        assertArrayEquals(expectedContent.getBytes(), retrievedFileContent);
    }

    @When("the resource is deleted")
    public void theResourceIsDeleted() {
        deleteResult = resourceService.deleteResources("1").get("ids").contains(1L);
    }

    @Then("the resource should no longer exist")
    public void theResourceShouldNoLongerExist() {
        assertTrue(deleteResult);
    }
}