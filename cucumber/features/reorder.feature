Feature: Re-ordering list

  Background:
    Given I am logged in

  Scenario: Add to list and reorder
    When I am on a recipe page
    And I add to favorites
    When I am on a different recipe page
    And I add to favorites
    When I am on a restaurant page
    And I add to favorites
    When I am on the favorites page
    When I drag the top down
    Then it should not be at the top of the list

  Scenario: Items are persistent
    When I am on the favorites page
    Then they should be in the same order
