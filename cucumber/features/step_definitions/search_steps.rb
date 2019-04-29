=begin
  Buttons
=end

When("I press search") do
  click_button "submit-button"
end

=begin
  Text fields
=end

When("I enter {string} in the search box") do |string|
  fill_in "query-input", :with => string
end

=begin
  Assertions
=end

Then("I should see food results for {string}") do |string|
  expect(page).to have_content(string)
end

Then("I should see Feed Me button") do
  expect(page).to have_content("Feed Me!")
end

Then("I should see query error message") do
  expect(page).to have_content("Field may not be blank")
end
