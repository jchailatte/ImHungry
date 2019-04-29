package models;

public class RecipeOrder {
    int index;
    int recipeID;

    public RecipeOrder(){}

    public RecipeOrder(int index, int recipeID) {
        this.index = index;
        this.recipeID = recipeID;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRecipeID() {
        return recipeID;
    }
    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }
}
