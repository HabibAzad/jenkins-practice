Feature: Etsy Search
  As an Etsy user
  I want to search for products on Etsy
  So that I can find items by keyword

  @wip @etsy
  Scenario: Search for wooden spoon and verify product title
    Given I navigate to Etsy homepage
    When I search for "wooden spoon"
    Then I should see a product with title containing "Personalized Wooden Spoon"
