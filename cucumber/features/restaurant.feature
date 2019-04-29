Feature: Viewing restaurant

  Background:
    Given I am logged in
    Given I am on a restaurant page

  Scenario: Browse data
    Then I should see address, phone number, and website

  Scenario: Click maps
    When I click the address
    Then I should go to Google maps

  Scenario: search no results in radius
    When I enter "In-n-out" in the search box
    And I enter "1" in the radius box
    And I press search
    Then I should see no restaurants

  Scenario: Click website
    When I click the URL
    Then I should go to the restaurant website

  Scenario: Printable version
    When I click print
    Then the buttons should go away

  Scenario: Back to Results
    When I go back to results
    Then I should be on results page
