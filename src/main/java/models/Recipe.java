package models;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// Represents a Recipe object including all data on a Recipe as well as methods to search for recipes or crate one from API
public class Recipe {
    // URL to All Recipes API

    private static String baseURL= "https://us-central1-im-hungry-236221.cloudfunctions.net/function-1";
    private int recipeID;
    // Recipe name
    private String name;
    // Prep time
    @SerializedName("prep_time")
    private String prepTime;
    // Prep time
    @SerializedName("cook_time")
    private String cookTime;
    // The star rating of the recipe
    @SerializedName("rating")
    private double stars;
    // The ingredients of the recipe
    private String[] ingredients;
    // Steps of the recipe
    private String[] steps;
    // Unique recipe ID
    @SerializedName("recipe_url")
    private String recipeURL;
    // Image URL
    @SerializedName("image")
    private String imageURL;
    // ListType indicating what list recipe is on
    private ListType listType;
    private int listOrder;

    //region Constructor
    public Recipe(
        String name,
        String prepTime,
        String cookTime,
        double stars,
        String[] ingredients,
        String[] steps,
        String recipeURL,
        String imageURL
    ) {
        this.name = name;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.stars = stars;
        this.ingredients = ingredients;
        this.steps = steps;
        this.listType = ListType.NONE;
        this.recipeURL = recipeURL;
        this.imageURL = imageURL;
    }

    public Recipe(
        String name,
        String prepTime,
        String cookTime,
        double stars,
        String[] ingredients,
        String[] steps,
        String recipeURL,
        String imageURL,
        int listOrder
    ) {
        this.name = name;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.stars = stars;
        this.ingredients = ingredients;
        this.steps = steps;
        this.listType = ListType.NONE;
        this.recipeURL = recipeURL;
        this.imageURL = imageURL;
        this.listOrder = listOrder;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public String getName() {
        return name;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public String getCookTime() {
        return cookTime;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public String[] getSteps() {
        return steps;
    }

    public double getStars() {
        return stars;
    }

    public ListType getListType() {
        return listType;
    }

    public String getRecipeURL() {
        return recipeURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setRecipeID(int id) {
        this.recipeID = id;
    }

    public void setListType(ListType listType){
        this.listType = listType;
    }

    // Returns a list of recipes matching the search query and limited to the numberOfResults specified
    public static List<Recipe> search(String query, int numberOfResults) {
        // Bound number of results to: 1...maxInt (inclusive)
        int safeNumberOfResults = Integer.max(1, Integer.min(numberOfResults, Integer.MAX_VALUE));
        int page = 0;
        ArrayList<Recipe> allRecipes = new ArrayList<>();
        ArrayList<Recipe> favoriteSortedRecipes;

        try {
            do {
                // Fetch place IDs for candidate restaurants
                String resultsURL = Recipe.baseURL + "/?method=searchv2"
                    + "&searchTerm=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                    + "&pageNumber=" + page;
                String resultsJSON = NetworkReader.readUrl(resultsURL);
                Gson gson = new Gson();
                ArrayList<Recipe> response = gson.fromJson(resultsJSON, new TypeToken<ArrayList<Recipe>>(){}.getType());
                if (response.size()== 0) {
                    break;
                } else {
                    response = Recipe.recipeSearchToDetail(response)
                        .stream()
                        .limit(numberOfResults)
                        .filter(r -> r.getListType() != ListType.DO_NOT_SHOW)
                        .collect(Collectors.toCollection(ArrayList::new));
                    allRecipes.addAll(response);
                    page++;
                }
            } while (allRecipes.size() < safeNumberOfResults);

            // Get favorite recipes
            favoriteSortedRecipes = allRecipes.stream().filter(r -> r.getListType() == ListType.FAVORITE)
                    .collect(Collectors.toCollection(ArrayList::new));
            // Append all non-favorite recipes after the favorites for proper ranking
            favoriteSortedRecipes.addAll(allRecipes.stream().filter(r -> r.getListType() != ListType.FAVORITE)
                    .collect(Collectors.toCollection(ArrayList::new)));
            return favoriteSortedRecipes.subList(0, Integer.min(safeNumberOfResults, favoriteSortedRecipes.size()));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Constructs a recipe based on the All Recipe recipe ID provided
    public static Recipe fromRecipeURL(String recipeURL) {
        String resultsURL = Recipe.baseURL + "/?method=retrieve"
                + "&recipeURL=" + recipeURL;
        String resultsJSON = null;
        try {
            resultsJSON = NetworkReader.readUrl(resultsURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        return gson.fromJson(resultsJSON, Recipe.class);
    }

    private static ArrayList<Recipe> recipeSearchToDetail(ArrayList<Recipe> recipes) {
        return recipes
            .parallelStream()
            .map(x -> fromRecipeURL(x.recipeURL))
            .collect(Collectors.toCollection(ArrayList::new));
    }
}
