package models;

public class GroceryListItem {
    boolean checked;
    int groceryID;
    double amount;
    String unit;
    String ingredients;

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getGroceryID() {
        return groceryID;
    }

    public void setGroceryID(int groceryID) {
        this.groceryID = groceryID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}
