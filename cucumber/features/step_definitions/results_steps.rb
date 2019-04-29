=begin
  Buttons
=end

When("I press back to Search") do
  click_button "back-to-search"
end

When("I select favorites") do
  click_button "manage-list-button"
  click_button "favorite-button"
end

When("I press manage list") do
  click_button "manage-list-button"
end

When("I press a restaurant") do
  first(".restaurant-row").click
end

When("I press a recipe") do
  first(".recipe-row").click
end

=begin
  Assertions
=end

Then("I should see {int} restaurants and recipes") do |int|
  expect(page).to have_css(".recipe-row", :count => int)
end

Then("I should have required UI elements") do
  expect(page).to have_content("Manage List")
  expect(page).to have_content("Search")
  expect(page).to have_selector("h1", text: "Results for Tacos")
  expect(page).to have_selector("h2", text: "Recipes")
  expect(page).to have_selector("h2", text: "Restaurants")
end

Then("I should have {int} images") do |int|
  expect(page).to have_css(".collage-item", :count => int)
end

Then("I should see search page") do
  expect(page).to have_title("Search")
end

Then("I should be on a restaurant page") do
  expect(page).to have_title("Restaurant")
end

Then("I should be on a recipe page") do
  expect(page).to have_title("Recipe")
end

Then("I should be on the favorites list management page") do
  expect(page).to have_title("List Management")
  expect(page).to have_content("Favorites")
end

Then("I should see no restaurants") do
  expect(page).to have_content("No restaurants found")
end
