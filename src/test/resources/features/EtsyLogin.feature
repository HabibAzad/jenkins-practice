Feature: Etsy sign in
  As an Etsy user
  I want to open Etsy sign in and enter my credentials
  So that I can attempt login


    @etsy
  Scenario: Enter Etsy credentials on sign in form with data table
    Given I navigate to Etsy homepage
    When I click Etsy Sign in
    And I enter Etsy username "azadhabibullah2010@gmail.com " and password "shamtal@23"

    Then I click on Sign in button



