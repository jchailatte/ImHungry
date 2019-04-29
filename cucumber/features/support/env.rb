require "capybara"
require "capybara/cucumber"
require "selenium-webdriver"
require "rspec"

Capybara.register_driver :chrome do |app|
    Capybara::Selenium::Driver.new(app, :browser => :chrome, :driver_path=>"/home/travis/build/PeterYangIO/imhungry/cucumber/driver/chromedriver")

end

Capybara.default_driver = :chrome

Capybara.default_max_wait_time = 30
