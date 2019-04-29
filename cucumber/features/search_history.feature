Feature: Searching from the homepage

  Background:
    Given I am logged in
    Given I am on the home page

  Scenario: search for tacos
    When I enter "Tacos" in the search box
    And I should see Feed Me button
    When I press search
    Then I should see food results for "Tacos"
    When I am on the home page
    Then I should see "Tacos" in the dropdown

  Scenario: invalid search not stored
    When I enter "A taco stand in like China or something" in the search box
    And I enter "-5" in the number of results box
    When I press search
    Then I should see number of results error message
    And I should not see "A taco stand in like China or something" in the dropdown
