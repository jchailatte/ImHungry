Feature: Searching with radius

  Background:
    Given I am logged in
    Given I am on the home page

  Scenario: search within 5 miles
    When I enter "Tacos" in the search box
    And I enter "5" in the radius box
    Then I should see radius help text
    When I press search
    Then I should see nearby places

  Scenario: search for non-numeric radius
    And I enter "potatoes" in the radius box
    When I press search
    Then I should see radius error message

  Scenario: search for negative radius
    And I enter "-5" in the radius box
    When I press search
    Then I should see radius error message

  Scenario: search for 0 radius
    When I enter "0" in the radius box
    When I press search
    Then I should see radius error message
