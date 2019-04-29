=begin
  Buttons
=end

When("I click the address") do
  find("#address").click
end

When("I click the URL") do
  find("#url").click
end

=begin
  Assertions
=end

Then("I should see address, phone number, and website") do
  expect(page).to have_title("Restaurant")
  expect(page).to have_content("Trejo's Tacos")
  expect(page).to have_content("(213) 536-5513")
  expect(page).to have_content("835 W Jefferson Blvd #1735, Los Angeles, CA 90089, USA")
  expect(page).to have_content("http://www.trejostacos.com/")
end

Then("I should go to Google maps") do
  expect(page).to have_title("Tommy Trojan to Trejo's Tacos - Google Maps")
end

Then("I should go to the restaurant website") do
  expect(page).to have_title("Trejos Tacos")
end
