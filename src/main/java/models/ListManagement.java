package models;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.util.HashSet;

import common.Constants;

// Manages lists that recipes and restaurants are a part of by storing relevant properties on these items in MySQL DB
public class ListManagement {
    private static final String RESTAURANT = "Restaurant";
    static final String PLACE_ID = "placeID";
    static final String RESTAURANT_ID = "restaurantID";
    static final String RESTAURANT_NAME = "restaurantName";
    static final String LIST_TYPE = "listType";
    static final String ADDRESS = "address";
    static final String DRIVE_TIME = "driveTime";
    static final String WEB_URL = "webURL";
    static final String STARS = "stars";
    static final String PRICE = "price";
    static final String PHONE_NUMBER = "phoneNumber";
    private static final String RECIPE = "Recipe";
    static final String RECIPE_ID = "recipeID";
    static final String RECIPE_NAME = "recipeName";
    static final String PREP_TIME = "prepTime";
    static final String COOK_TIME = "cookTime";
    static final String IMAGE_URL = "imageURL";
    static final String INGREDIENTS = "ingredients";
    static final String STEPS = "steps";
    static final String RECIPE_URL = "recipeURL";
    private static final String USER_ID = "userID";
    private static final String SEARCH_TERMS = "SearchTerms";
    private static final String SEARCHED_ITEM = "searchedItem";
    private static final String LIST_ORDER = "listOrder";

    //insert restaurant into database
    public static void insertRestaurant(int userID, String placeID) {
        if(containsRestaurant(userID, placeID)){//restaurant is in database with particular user already
            return;
        }
        Restaurant newRestaurant = RestaurantFactory.fromPlaceID(placeID);
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlInsertStatement = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                    RESTAURANT, PLACE_ID, RESTAURANT_NAME, LIST_TYPE, ADDRESS, DRIVE_TIME, WEB_URL, STARS, PRICE, PHONE_NUMBER, USER_ID, LIST_ORDER);
            preparedStatement = connection.prepareStatement(sqlInsertStatement);
            preparedStatement.setString(1, newRestaurant.getPlaceID());
            preparedStatement.setString(2, newRestaurant.getName());
            preparedStatement.setString(3, newRestaurant.getListType().name());
            preparedStatement.setString(4, newRestaurant.getAddress());
            preparedStatement.setString(5, newRestaurant.getDriveTime());
            preparedStatement.setString(6, newRestaurant.getWebURL());
            preparedStatement.setDouble(7, newRestaurant.getStars());
            preparedStatement.setInt(8, newRestaurant.getPrice());
            preparedStatement.setString(9, newRestaurant.getPhoneNumber());
            preparedStatement.setInt(10, userID);

            int nextIndex = findMaxListOrder(userID, newRestaurant.getListType());
            preparedStatement.setInt(11, nextIndex);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Restaurant insertion: " + e.getLocalizedMessage());
        }
    }

    //changes a restaurant's listType
    public static void changeRestaurantListType(int userID, String placeID, ListType listType) {
        Connection connection;
        PreparedStatement preparedStatement;
        ListType oldListType = getListTypeFromRestaurant(userID, placeID);
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlUpdateStatement = String.format("UPDATE %s SET %s = ?, %s = ? WHERE %s = ? AND %s = ?", RESTAURANT, LIST_TYPE, LIST_ORDER, PLACE_ID, USER_ID);
            preparedStatement = connection.prepareStatement(sqlUpdateStatement);
            int nextIndex = findMaxListOrder(userID, listType);
            int deletedIndex = getRestaurantListOrder(userID, placeID, oldListType);
            preparedStatement.setString(1, listType.name());
            preparedStatement.setInt(2, nextIndex);
            preparedStatement.setString(3, placeID);
            preparedStatement.setInt(4, userID);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            updateRemovedListOrder(userID, deletedIndex, oldListType);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static int getRestaurantListOrder(int userID, String placeID, ListType listType){
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        int oldIndex = -1;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT %s FROM %s WHERE %s =? AND %s =? AND %s = ?", LIST_ORDER, RESTAURANT, PLACE_ID, USER_ID, LIST_TYPE);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setString(1, placeID);
            preparedStatement.setInt(2, userID);
            preparedStatement.setString(3, listType.name());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                oldIndex = resultSet.getInt(LIST_ORDER);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return oldIndex;
    }

    //returns an arrayList of restaurants with a given listType from the database
    public static ArrayList<Restaurant> getRestaurantSavedLists(int userID, ListType listType) {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = ? AND %s = ?",
                    RESTAURANT_ID, PLACE_ID, RESTAURANT_NAME, ADDRESS, DRIVE_TIME, PHONE_NUMBER, WEB_URL, STARS, PRICE, LIST_ORDER, RESTAURANT, LIST_TYPE, USER_ID);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setString(1, listType.name());
            preparedStatement.setInt(2, userID);
            resultSet = preparedStatement.executeQuery();

            //looping through restaurants with the input listType
            while (resultSet.next()) {
                Restaurant tempRestaurant = new Restaurant(
                    resultSet.getString(RESTAURANT_NAME),
                    resultSet.getString(ADDRESS),
                    resultSet.getString(DRIVE_TIME),
                    0,
                    resultSet.getString(WEB_URL),
                    resultSet.getString(PHONE_NUMBER),
                    resultSet.getString(PLACE_ID),
                    resultSet.getDouble(STARS),
                    resultSet.getInt(PRICE),
                    resultSet.getInt(LIST_ORDER)
                );
                tempRestaurant.setRestaurantID(resultSet.getInt(RESTAURANT_ID));
                restaurants.add(tempRestaurant);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return restaurants;
    }

    //returns a restaurant's ListType from the database
    static ListType getListTypeFromRestaurant(int userID, String placeID) {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        ListType restaurantListType = ListType.NONE;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT %s FROM %s WHERE %s = ? AND %s = ?", LIST_TYPE, RESTAURANT, PLACE_ID, USER_ID);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setString(1, placeID);
            preparedStatement.setInt(2, userID);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                restaurantListType = ListType.valueOf(resultSet.getString(LIST_TYPE));
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return restaurantListType;
    }

    //inserts a recipe into the database
    public static void insertRecipe(int userID, Recipe newRecipe) {
        if(containsRecipe(userID, newRecipe.getRecipeURL()) != -1){
            return;
        }
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlInsertStatement = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                    RECIPE, RECIPE_NAME, LIST_TYPE, PREP_TIME, COOK_TIME, IMAGE_URL, STARS, INGREDIENTS, STEPS, RECIPE_URL, USER_ID, LIST_ORDER);
            preparedStatement = connection.prepareStatement(sqlInsertStatement);

            preparedStatement.setString(1, newRecipe.getName());
            preparedStatement.setString(2, newRecipe.getListType() == null ? ListType.NONE.name() : newRecipe.getListType().name());
            preparedStatement.setString(3, newRecipe.getPrepTime());
            preparedStatement.setString(4, newRecipe.getCookTime());
            preparedStatement.setString(5, newRecipe.getImageURL());
            preparedStatement.setDouble(6, newRecipe.getStars());
            preparedStatement.setString(9, newRecipe.getRecipeURL());
            preparedStatement.setInt(10, userID);
            int nextIndex = findMaxListOrder(userID, ListType.NONE);
            preparedStatement.setInt(11, nextIndex);

            //splitting recipe's array of steps and ingredients into one string
            String ingredientStr = String.join(";", newRecipe.getIngredients());
            preparedStatement.setString(7, ingredientStr);
            String instructionStr = String.join(";", newRecipe.getSteps());
            preparedStatement.setString(8, instructionStr);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    //changes a ListType of a recipe
    public static void changeRecipeListType(int userID, Recipe recipe, ListType listType) {
        Connection connection;
        PreparedStatement preparedStatement;
        ListType oldListType = recipe.getListType();
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlUpdateStatement = String.format("UPDATE %s SET %s = ?, %s = ? WHERE %s = ? AND %s = ?", RECIPE, LIST_TYPE, LIST_ORDER, RECIPE_URL, USER_ID);
            preparedStatement = connection.prepareStatement(sqlUpdateStatement);
            int nextIndex = findMaxListOrder(userID, listType);
            int deletedIndex = getRecipeListOrder(userID, recipe.getRecipeURL(), oldListType);
            preparedStatement.setString(1, listType.name());
            preparedStatement.setInt(2, nextIndex);
            preparedStatement.setString(3, recipe.getRecipeURL());
            preparedStatement.setInt(4, userID);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            updateRemovedListOrder(userID, deletedIndex, oldListType);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static int getRecipeListOrder(int userID, String recipeURL, ListType listType){
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        int oldIndex = -1;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT %s FROM %s WHERE %s =? AND %s =? AND %s = ?", LIST_ORDER, RECIPE, RECIPE_URL, USER_ID, LIST_TYPE);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setString(1, recipeURL);
            preparedStatement.setInt(2, userID);
            preparedStatement.setString(3, listType.name());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                oldIndex = resultSet.getInt(LIST_ORDER);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return oldIndex;
    }

    //returns an arrayList of restaurants with a given listType from the database
    public static ArrayList<Recipe> getRecipeSavedLists(int userID, ListType listType) {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        ArrayList<Recipe> recipes = new ArrayList<>();
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = ? AND %s = ?",
                    RECIPE_ID, RECIPE_NAME, LIST_TYPE, PREP_TIME, COOK_TIME, IMAGE_URL, STARS, INGREDIENTS, STEPS, RECIPE_URL, LIST_ORDER, RECIPE, LIST_TYPE, USER_ID);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setString(1, listType.name());
            preparedStatement.setInt(2, userID);
            resultSet = preparedStatement.executeQuery();

            //looping through restaurants with the input listType
            while (resultSet.next()) {
                String ingredientStr = resultSet.getString(INGREDIENTS);
                String instructionStr = resultSet.getString(STEPS);
                String[] ingredients = ingredientStr.split(";");
                String[] instructions = instructionStr.split(";");

                //constructing recipe
                Recipe newRecipe = new Recipe(
                    resultSet.getString(RECIPE_NAME),
                    resultSet.getString(PREP_TIME),
                    resultSet.getString(COOK_TIME),
                    resultSet.getDouble(STARS),
                    ingredients,
                    instructions,
                    resultSet.getString(RECIPE_URL),
                    resultSet.getString(IMAGE_URL),
                    resultSet.getInt(LIST_ORDER)
                );
                newRecipe.setRecipeID(resultSet.getInt(RECIPE_ID));
                newRecipe.setListType(ListType.valueOf(resultSet.getString(LIST_TYPE)));
                recipes.add(newRecipe);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return recipes;
    }

    //returns a recipe's ListType from the database
    static ListType getListTypeFromRecipe(int userID, String recipeURL) {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        ListType recipeListType = ListType.NONE;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT %s FROM %s WHERE %s =? AND %s =?", LIST_TYPE, RECIPE, RECIPE_URL, USER_ID);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setString(1, recipeURL);
            preparedStatement.setInt(2, userID);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                recipeListType = ListType.valueOf(resultSet.getString(LIST_TYPE));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return recipeListType;
    }

    public static void insertSearchTerm(int userID, String searchedItem){
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlInsertStatement = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?);",
                     SEARCH_TERMS, USER_ID, SEARCHED_ITEM);
            preparedStatement = connection.prepareStatement(sqlInsertStatement);

            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, searchedItem);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static ArrayList<String> getAllSearchedItems(int userID){
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        ArrayList<String> allSearchedItems = new ArrayList<>();
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT %s FROM %s WHERE %s =?", SEARCHED_ITEM, SEARCH_TERMS, USER_ID);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(1, userID);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                allSearchedItems.add(resultSet.getString(SEARCHED_ITEM));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        HashSet<String> set = new HashSet<>(allSearchedItems);

        return new ArrayList<>(set);
    }

    static String[] getIngredients(int recipeID){
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String[] ingredients = null;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT %s FROM %s WHERE %s = ?",
                    INGREDIENTS, RECIPE, RECIPE_ID);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(1, recipeID);
            resultSet = preparedStatement.executeQuery();

            //looping through restaurants with the input listType
            if (resultSet.next()) {
                String ingredientStr = resultSet.getString(INGREDIENTS);
                ingredients = ingredientStr.split(";");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return ingredients;
    }

    /**
     * Checks if a user already has a recipe associated with them. If they do, return
     * the recipeID, otherwise -1
     *
     * @param userID user
     * @param recipeURL allrecipes url
     *
     * @return recipeID >=1 or -1 for not contains
     */
    public static int containsRecipe(int userID, String recipeURL){
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        int recipeId = -1;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", RECIPE, USER_ID, RECIPE_URL);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, recipeURL);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {//recipeURL is already in
                recipeId = resultSet.getInt(1);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return recipeId;
    }

    private static boolean containsRestaurant(int userID, String placeID){//returns false if placeID is already in table
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        boolean containsRestaurant = false;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT %s FROM %s WHERE %s = ? AND %s = ?", PLACE_ID, RESTAURANT ,USER_ID, PLACE_ID);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, placeID);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {//placeID is already in
                containsRestaurant = true;
            }
            else{
                //user does not exist
                containsRestaurant = false;
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return containsRestaurant;
    }

    public static int findMaxListOrder(int userID, ListType listType){
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        int maxListOrderNumber = -1;
        int restaurantMaxOrderNumber = -1;
        int recipeMaxOrderNumber = -1;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);

            String sqlSelectStatement = String.format("SELECT %s FROM %s WHERE %s = ? AND %s =?",
                    LIST_ORDER, RESTAURANT, USER_ID, LIST_TYPE);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, listType.name());
            resultSet = preparedStatement.executeQuery();
            //looping through restaurants with the input listType
            while (resultSet.next()) {
                restaurantMaxOrderNumber = Math.max(restaurantMaxOrderNumber, resultSet.getInt(LIST_ORDER));
            }

            sqlSelectStatement = String.format("SELECT %s FROM %s WHERE %s = ? AND %s =?",
                    LIST_ORDER, RECIPE, USER_ID, LIST_TYPE);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, listType.name());
            resultSet = preparedStatement.executeQuery();
            //looping through restaurants with the input listType
            while (resultSet.next()) {
                recipeMaxOrderNumber = Math.max(recipeMaxOrderNumber, resultSet.getInt(LIST_ORDER));
            }
            maxListOrderNumber = Math.max(recipeMaxOrderNumber, restaurantMaxOrderNumber);
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("findMaxListOrder: "+ e.getLocalizedMessage());
        }

        ++maxListOrderNumber;
        return maxListOrderNumber;
    }

    public static void updateRemovedListOrder(int userID, int removedListOrder, ListType listType){
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("UPDATE %s SET %s = %s - 1 WHERE %s = ? AND %s > ? AND %s = ?",
                    RESTAURANT, LIST_ORDER, LIST_ORDER, USER_ID, LIST_ORDER, LIST_TYPE);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, removedListOrder);
            preparedStatement.setString(3, listType.name());
            preparedStatement.execute();

            sqlSelectStatement = String.format("UPDATE %s SET %s = %s - 1 WHERE %s = ? AND %s > ? AND %s = ?",
                    RECIPE, LIST_ORDER, LIST_ORDER, USER_ID, LIST_ORDER, LIST_TYPE);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, removedListOrder);
            preparedStatement.setString(3, listType.name());
            preparedStatement.execute();

            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static ArrayList<RestaurantOrder> getRestaurantOrderOfListType(int userID, ListType listType){
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        ArrayList<RestaurantOrder> sortedRestaurantOrders = new ArrayList<>();
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT %s, %s FROM %s WHERE %s = ? AND %s = ? ORDER BY %s ASC",
                    LIST_ORDER, RESTAURANT_ID, RESTAURANT, USER_ID, LIST_TYPE, LIST_ORDER);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, listType.name());
            resultSet = preparedStatement.executeQuery();

            //looping through restaurants with the input listType
            while (resultSet.next()) {
                RestaurantOrder tempRestaurantOrder = new RestaurantOrder();
                tempRestaurantOrder.setIndex(resultSet.getInt(LIST_ORDER));
                tempRestaurantOrder.setRestaurantID(resultSet.getInt(RESTAURANT_ID));
                sortedRestaurantOrders.add(tempRestaurantOrder);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return sortedRestaurantOrders;
    }

    public static ArrayList<RecipeOrder> getRecipeOrderOfListType(int userID, ListType listType){
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        ArrayList<RecipeOrder> sortedRecipeOrders = new ArrayList<>();
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT %s, %s FROM %s WHERE %s = ? AND %s = ? ORDER BY %s ASC",
                    LIST_ORDER, RECIPE_ID, RECIPE, USER_ID, LIST_TYPE, LIST_ORDER);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, listType.name());
            resultSet = preparedStatement.executeQuery();

            //looping through restaurants with the input listType
            while (resultSet.next()) {
                RecipeOrder tempRecipeOrder = new RecipeOrder();
                tempRecipeOrder.setIndex(resultSet.getInt(LIST_ORDER));
                tempRecipeOrder.setRecipeID(resultSet.getInt(RECIPE_ID));
                sortedRecipeOrders.add(tempRecipeOrder);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return sortedRecipeOrders;
    }

    public static void updateListOrderForRestaurants(int userID, ListType listType, ArrayList<RestaurantOrder> updatedRestaurantOrder){
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("UPDATE %s SET %s = ? WHERE %s = ? AND %s = ? AND %s = ?",
                    RESTAURANT, LIST_ORDER, USER_ID, RESTAURANT_ID, LIST_TYPE);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(2, userID);
            preparedStatement.setString(4, listType.name());
            for(RestaurantOrder currentRestaurantOrder : updatedRestaurantOrder){
                preparedStatement.setInt(1,currentRestaurantOrder.getIndex());
                preparedStatement.setInt(3, currentRestaurantOrder.getRestaurantID());
                preparedStatement.execute();
            }

            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static void updateListOrderForRecipes(int userID, ListType listType, ArrayList<RecipeOrder> updatedRecipeOrder){
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("UPDATE %s SET %s = ? WHERE %s = ? AND %s = ? AND %s = ?",
                    RECIPE, LIST_ORDER, USER_ID, RECIPE_ID, LIST_TYPE);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(2, userID);
            preparedStatement.setString(4, listType.name());
            for(RecipeOrder currentRecipeOrder : updatedRecipeOrder){
                preparedStatement.setInt(1,currentRecipeOrder.getIndex());
                preparedStatement.setInt(3, currentRecipeOrder.getRecipeID());
                preparedStatement.execute();
            }

            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
