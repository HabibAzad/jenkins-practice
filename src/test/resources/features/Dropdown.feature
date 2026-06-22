Feature: Dropdown selection
  As a user
  I want to interact with a dropdown control
  So that I can select options by different strategies

  Background:
    Given I navigate to the dropdown page

  @smoke @regression @dropdown
  Scenario: Select an option by visible text
    When I select the option by visible text "Option 1"
    Then the selected option text should be "Option 1"

  @regression @dropdown
  Scenario: Select an option by index
    When I select the option by index 2
    Then the selected option text should be "Option 2"

  @regression @dropdown
  Scenario: Select an option by value attribute
    When I select the option by value "1"
    Then the selected option text should be "Option 1"

  @regression @dropdown
  Scenario Outline: Verify all selectable options
    When I select the option by visible text "<optionText>"
    Then the selected option text should be "<optionText>"

    Examples:
      | optionText |
      | Option 1   |
      | Option 2   |
