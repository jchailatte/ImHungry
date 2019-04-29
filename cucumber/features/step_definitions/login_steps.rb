=begin
  Buttons
=end

When("I click the login button") do
  click_button "login-button"
end

When("I click the sign up button") do
  click_button "sign-up-button"
end

When("I click the sign up confirm button") do
  click_button "sign-up-confirm-button"
end

When("I click the login confirm button") do
  click_button "login-confirm-button"
end

When("I logout") do
  find("#persona").click
  click_button "logout-button"
end

=begin
  Text fields
=end

When("I enter {string} in the username box") do |string|
  fill_in "username", with: string
end

When("I enter {string} in the password box") do |string|
  fill_in "password", with: string
end

=begin
  Assertions
=end

Then("I should be signed in") do
  expect(page).to have_no_content("Login")
end

Then("I should be logged out") do
  expect(page).to have_content("Login")
end

Then("I should see account exists error") do
  expect(page).to have_content("An account with that username already exists")
end

Then("I should see register missing fields error") do
  expect(page).to have_content("may not be blank")
end

Then("I should see password weak error") do
  expect(page).to have_content("Password is too weak")
end

Then("I should see log in error message") do
  expect(page).to have_content("Username or password is invalid")
end
