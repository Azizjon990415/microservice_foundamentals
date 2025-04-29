Feature: Resource Service Component Test

  Scenario: Save a resource successfully
    Given a resource with ID 1 and file content "test-data"
    When the resource is saved
    Then the resource should be saved successfully

  Scenario: Retrieve a resource successfully
    Given a resource with ID 1 exists in the system
    When the resource is retrieved
    Then the file content should be "test-data"

  Scenario: Delete a resource successfully
    Given a resource with ID 1 exists in the system
    When the resource is deleted
    Then the resource should no longer exist