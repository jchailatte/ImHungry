=begin
  Actions
=end

When("I drag the top down") do
  @initial_first = find("#draggable-0")
  target = find("#draggable-2")
  @initial_first.drag_to target
end

=begin
  Assertions
=end

Then("it should not be at the top of the list") do
  $new_first = find("#draggable-1")
  @initial_first.text.should_not == $new_first.text
end

Then("they should be in the same order") do
  current_first = find("#draggable-1")
  $new_first.text.should == current_first.text
end
