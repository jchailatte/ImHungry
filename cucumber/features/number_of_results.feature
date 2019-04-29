Feature: Searching with number of results

  Background:
    Given I am logged in
    Given I am on the home page

  Scenario: search for 3 results
    When I enter "Tacos" in the search box
    And I enter "3" in the number of results box
    Then I should see number of results help text
    When I press search
    Then I should see "3" results

  Scenario: search for non-numeric number of results
    When I enter "potatoes" in the number of results box
    When I press search
    Then I should see number of results error message

  Scenario: search for negative number of results
    When I enter "-5" in the number of results box
    When I press search
    Then I should see number of results error message

  Scenario: search for 0 results
    When I enter "0" in the number of results box
    When I press search
    Then I should see number of results error message
