Feature: Viewing recipe

  Background:
    Given I am logged in
    Given I am on a recipe page

  Scenario: Browse data
    Then I should see title, image, prep and cook time, ingredients, instructions, and required buttons

  Scenario: Printable version
    When I click print
    Then the buttons should go away

  Scenario: Back to Results
    When I go back to results
    Then I should be on results page
