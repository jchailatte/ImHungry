Given("I am on the home page") do
  visit "localhost:8080"
end

Given("I am on a recipe page") do
  visit "localhost:8080/recipe/?recipeURL=http://allrecipes.co.uk/recipe/34971/classic-beef-burger.aspx"
end

Given("I am on a different recipe page") do
  visit "localhost:8080/recipe/?recipeURL=http://allrecipes.co.uk/recipe/32242/tacos-al-pastor.aspx"
end

Given("I am on a restaurant page") do
  visit "localhost:8080/restaurant/ChIJcWkWVOTHwoAR8OyGlWsyNuI"
end

Given("I am on the favorites page") do
  visit "localhost:8080/list-management/1"
end

Given("I am logged in") do
  visit "localhost:8080"

  if page.has_content? "Login"
    click_button "login-button"
    fill_in "username", with: "cucumberuser"
    fill_in "password", with: "letmein123"
    click_button "login-confirm-button"
    if page.has_content? "Username or password is invalid"
      click_button "sign-up-button"
      click_button "sign-up-confirm-button"
      sleep(1)
    end
  end
end

Given("I am logged out") do
  visit "localhost:8080"

  if page.has_no_content? "Login"
    find("#persona").click
    click_button "logout-button"
  end

  visit "localhost:8080"
end
