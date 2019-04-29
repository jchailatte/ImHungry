Feature: Login system

  Background:
    Given I am logged out
    Given I am on the home page

  Scenario: Register user
    When I click the login button
    And I click the sign up button
    And I enter "logintester" in the username box
    And I enter "letmein123" in the password box
    And I click the sign up confirm button
    Then I should be signed in
    When I logout
    Then I should be logged out

  Scenario: Register existing user
    When I click the login button
    And I click the sign up button
    And I enter "logintester" in the username box
    And I enter "letmein123" in the password box
    And I click the sign up confirm button
    Then I should see account exists error

  Scenario: Register missing fields
    When I click the login button
    And I click the sign up button
    And I click the sign up confirm button
    Then I should see register missing fields error

  Scenario: Register weak password
    When I click the login button
    And I click the sign up button
    And I enter "logintester2" in the username box
    And I enter "hi" in the password box
    And I click the sign up confirm button
    Then I should see password weak error

  Scenario: Login user
    When I click the login button
    And I enter "logintester" in the username box
    And I enter "letmein123" in the password box
    And I click the login confirm button
    Then I should be signed in
    And I logout
    Then I should be logged out

  Scenario: Login wrong password
    When I click the login button
    And I enter "logintester" in the username box
    And I enter "jfalsrhoiesfjd" in the password box
    And I click the login confirm button
    Then I should see log in error message
