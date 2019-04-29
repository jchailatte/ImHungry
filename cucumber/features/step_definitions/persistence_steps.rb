=begin
  Actions
=end

When("I add to favorites") do
  click_button "add-to-list-button"
  click_button "add-to-favorite-button"
end

When("I close the browser") do
  Capybara::Session
end

When("I open the browser") do
  visit "localhost:8080/list-management/1"
end

=begin
  Assertions
=end

Then("I should see a restaurant in favorites") do
  expect(page).to have_css(".restaurant-row")
end

Then("I should see {string}") do |string|
  expect(page).to have_content(string)
end
