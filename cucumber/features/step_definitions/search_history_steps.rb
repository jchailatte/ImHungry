=begin
  Assertions
=end

Then("I should see {string} in the dropdown") do |string|
  first(".ms-ComboBox-CaretDown-button").click
  expect(page).to have_content(string)
end

Then("I should not see {string} in the dropdown") do |string|
  first(".ms-ComboBox-CaretDown-button").click
  expect(page).to have_no_content(string)
end
