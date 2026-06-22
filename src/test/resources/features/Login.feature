Feature: Login functionality
  As a registered user
  I want to log in to the application
  So that I can access my account

  Background:
    Given I navigate to the login page

  @wip @login
  Scenario: Successful login with valid credentials
    When I enter username "student@student.com" and password "Password123"
    And I click the login button
    Then I should be redirected away from the login page

  @wip @login
  Scenario: Failed login with invalid password
    When I enter username "student@student.com" and password "wrongpassword"
    And I click the login button
    Then I should see a login error message

  @wip @login
  Scenario: Failed login with invalid username
    When I enter username "invalid@invalid.com" and password "Password123"
    And I click the login button
    Then I should see a login error message

  @wip @login
  Scenario Outline: Failed login with missing credentials
    When I enter username "<username>" and password "<password>"
    And I click the login button
    Then I should see a login error message

    Examples:
      | username              | password    |
      |                       | Password123 |
      | student@student.com   |             |
      |                       |             |
