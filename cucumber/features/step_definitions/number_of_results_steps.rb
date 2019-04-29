=begin
  Text fields
=end

When("I enter {string} in the number of results box") do |string|
  fill_in "number-of-results", with: ""
  fill_in "number-of-results", with: string
end

=begin
  Assertions
=end

Then("I should see number of results help text") do
  expect(page).to have_content("Number of items to show in results")
end

Then("I should see {string} results") do |string|
  expect(page).to have_css(".restaurant-row", :count => string)
  expect(page).to have_css(".recipe-row", :count => string)
end

Then("I should see number of results error message") do
  expect(page).to have_content("Must be positive number")
end
