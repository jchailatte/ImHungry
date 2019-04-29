Feature: Viewing results

  Background:
    Given I am logged in
    Given I am on the home page

  Scenario: Browse results
    When I enter "Tacos" in the search box
    And I enter "5" in the number of results box
    When I press search
    Then I should see 5 restaurants and recipes
    And I should have required UI elements
    And I should have 10 images

  Scenario: Go back to search Page
    When I enter "Tacos" in the search box
    And I enter "5" in the number of results box
    And I press search
    And I press back to Search
    Then I should see search page

  Scenario: Go to a Restaurant Page
    When I enter "Tacos" in the search box
    And I enter "5" in the number of results box
    And I press search
    And I press a restaurant
    Then I should be on a restaurant page

  Scenario: Go to a Recipe Page
    When I enter "Tacos" in the search box
    And I enter "5" in the number of results box
    And I press search
    And I press a recipe
    Then I should be on a recipe page

  Scenario: Go to Favorites list
    When I enter "Tacos" in the search box
    And I enter "5" in the number of results box
    And I press search
    And I select favorites
    Then I should be on the favorites list management page
