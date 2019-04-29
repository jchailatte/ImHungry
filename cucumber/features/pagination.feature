Feature: Pagination

  Background:
    Given I am logged in
    Given I am on the home page

  Scenario: Next Page
    When I enter "Tacos" in the search box
    And I enter "10" in the number of results box
    When I press search
    Then I should see 5 restaurants and recipes
    When I press the next button
    Then I should see 5 restaurants and recipes

  Scenario: Prev Page
    When I enter "Tacos" in the search box
    And I enter "10" in the number of results box
    When I press search
    Then I should see 5 restaurants and recipes
    When I press the next button
    Then I should see 5 restaurants and recipes
    When I press the back button
    Then I should see 5 restaurants and recipes

  Scenario: Prev Page on First Page
    When I enter "Tacos" in the search box
    And I enter "10" in the number of results box
    When I press search
    Then I should see 5 restaurants and recipes
    When I press the back button
    Then I should see 5 restaurants and recipes

  Scenario: Go to last page
    When I enter "Tacos" in the search box
    And I enter "8" in the number of results box
    When I press search
    Then I should see 5 restaurants and recipes
    When I press the next button
    Then I should see 3 restaurants and recipes

  Scenario: Go past number of pages
    When I enter "Tacos" in the search box
    And I enter "8" in the number of results box
    When I press search
    Then I should see 5 restaurants and recipes
    When I press the next button
    Then I should see 3 restaurants and recipes
    When I press the next button
    Then I should see 3 restaurants and recipes
    
