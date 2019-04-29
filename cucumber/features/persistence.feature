Feature: Data persistence

  Background:
    Given I am logged in
    Given I am on the home page

  Scenario: Favorite restaurant
    When I enter "Tacos" in the search box
    And I enter "5" in the number of results box
    And I press search
    And I press a restaurant
    And I add to favorites
    And I close the browser
    And I open the browser
    Then I should see a restaurant in favorites
