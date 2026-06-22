Feature: Dashboard page
  As a logged-in user
  I want the dashboard to load correctly
  So that I can navigate the application

  Background:
    Given I navigate to the login page
    When I enter username "student@student.com" and password "Password123"
    And I click the login button

  @smoke @regression @dashboard
  Scenario: Dashboard page title is correct after login
    Then the page title should be "Secured Area"

  @smoke @regression @dashboard
  Scenario: Dashboard URL contains expected path after login
    Then the current URL should contain "/secure"

  @regression @dashboard
  Scenario: Dashboard displays a welcome message
    Then I should see a welcome message containing "Welcome"

  @regression @dashboard
  Scenario: Dashboard displays the logout button
    Then the logout button should be visible
