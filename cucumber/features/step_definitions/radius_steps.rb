=begin
  Text fields
=end

When("I enter {string} in the radius box") do |string|
  fill_in "radius", with: ""
  fill_in "radius", with: string
end

=begin
  Assertions
=end

Then("I should see radius help text") do
  expect(page).to have_content("Radius from Tommy Trojan to search restaurants for")
end

Then("I should see nearby places") do
  expect(page).to have_no_content("(1h")
end

Then("I should see radius error message") do
  expect(page).to have_content("Must be positive number")
end
