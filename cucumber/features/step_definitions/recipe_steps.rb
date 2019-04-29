=begin
  Buttons
=end

When("I click print") do
  click_button "printable-version-button"
end

When("I go back to results") do
  click_button "results-page-button"
end

=begin
  Assertions
=end

Then("I should see title, image, prep and cook time, ingredients, instructions, and required buttons") do
  expect(page).to have_title("Recipe")
  expect(page).to have_content("Prep Time")
  expect(page).to have_content("Cook Time")
  expect(page).to have_content("Ingredients")
  expect(page).to have_content("Instructions")
end

Then("the buttons should go away") do
  expect(page).to have_no_content("Print")
  expect(page).to have_no_content("Back to Results")
  expect(page).to have_no_content("Add to List")
end

Then("I should be on results page") do
  expect(page).to have_title("Results")
end
