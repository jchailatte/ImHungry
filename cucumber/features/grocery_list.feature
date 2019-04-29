Feature: Grocery List Management

  Background:
    Given I am logged in

  Scenario: add recipe to grocery list
    Given I am on a recipe page
    When I press the add to grocery list button
    And I go to the grocery list page
    Then I should see "Grocery List"

  Scenario: Check then delete
    When I go to the grocery list page
    And I check off a grocery list item
    Then I should see a checked grocery list item
    When I refresh the page
    Then I should see a checked grocery list item
    When I delete a grocery list item
    Then I should not see any grocery list items
    When I refresh the page
    Then I should not see any grocery list items
