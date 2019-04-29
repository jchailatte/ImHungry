=begin
  Buttons
=end

When("I press the add to grocery list button") do
  click_button "add-to-list-button"
  click_button "add-to-grocery-list-button"
end

When("I check off a grocery list item") do
  first(".ms-Checkbox-checkbox").click
end

When("I delete a grocery list item") do
first(".ms-Button-icon").click
end

=begin
  Actions
=end

When("I go to the grocery list page") do
  visit "localhost:8080/list-management/4"
end

When("I refresh the page") do
  visit "localhost:8080/list-management/4"
end

=begin
  Assertions
=end

Then("I should see the ingredients from that recipe") do
  expect(page).to have_content("1kg beef mince")
  expect(page).to have_content("4 slices cheese (optional)")
  expect(page).to have_content("salt and pepper, to taste")
  expect(page).to have_content("4 burger buns, sliced")
  expect(page).to have_content("1/2 red onion, sliced")
end

Then("I should see a checked grocery list item") do
  expect(page).to have_css("is-checked")
end

Then("I should not see any grocery list items") do
  expect(page).to have_no_css("ms-Checkbox")
end
