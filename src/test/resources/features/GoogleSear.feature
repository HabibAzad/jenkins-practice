Feature: Google Search functionality
  Agile Story: As a user, when I search for a term on Google, I want to see relevant results.

  @wip @google
  Scenario: Search for a term on Google
    Given I am on the Google search page
    When I enter "Cucumber BDD" into the search box
    And I click the search button
    Then I should see results related to "Cucumber BDD"

