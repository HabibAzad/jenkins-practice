Feature: Etsy Search
  As an Etsy user
  I want to see Categories and search for items
  So that I can find items by keyword

  @smokeTest
  Scenario:
    Given I navigate to Etsy homepage
    When I click on the Categories button
    Then I should see a list of categories displayed
    Then I should see the "Accessories" category
