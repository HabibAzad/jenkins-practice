Feature: Logout functionality
  As a logged-in user
  I want to log out of the application
  So that my session is terminated

  Background:
    Given I navigate to the login page
    When I enter username "student@student.com" and password "Password123"
    And I click the login button

  @smoke @regression @logout
  Scenario: Successful logout redirects to the login page
    When I click the logout button
    Then I should be redirected to the login page

  @regression @logout
  Scenario: Flash message confirms logout
    When I click the logout button
    Then I should see a logout confirmation message
