Feature: Searching from the homepage

  Background:
    Given I am logged in
    Given I am on the home page

  Scenario: search for tacos
    When I enter "Tacos" in the search box
    And I should see Feed Me button
    When I press search
    Then I should see food results for "Tacos"

  Scenario: search for empty string
    When I enter "" in the search box
    And I press search
    Then I should see query error message
