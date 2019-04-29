package models;

import common.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class GroceryListManagement {

    private static final String GROCERY_LISTS = "GroceryLists";
    private static final String GROCERY_INGREDIENTS = "groceryIngredients";
    private static final String GROCERY_ID = "groceryID";
    private static final String GROCERY_CHECKED = "checked";
    private static final String USER_ID = "userID";
    private static final String UNIT = "unit";
    private static final String AMOUNT = "amount";

    public static void insertToGroceryList(int userID, int recipeID){
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlInsertStatement = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?);",
                    GROCERY_LISTS, USER_ID, GROCERY_INGREDIENTS, UNIT, AMOUNT);
            preparedStatement = connection.prepareStatement(sqlInsertStatement);
            preparedStatement.setInt(1, userID);
            String[] ingredients = ListManagement.getIngredients(recipeID);
            ArrayList<IngredientParserResult> parsedIngredients = NetworkReader.spoonacularCaller(ingredients);
            //get an integer amount, string unit, string original names
            for(int i = 0; i < parsedIngredients.size(); ++i){
                IngredientParserResult currentIngredient = parsedIngredients.get(i);
                int resultingUniqueness = verifyUniqueness(userID, currentIngredient.originalName, currentIngredient.unitLong);
                if(resultingUniqueness == -1){
                    preparedStatement.setString(2, currentIngredient.originalName);
                    preparedStatement.setString(3, currentIngredient.unitLong);
                    preparedStatement.setFloat(4, currentIngredient.amount);
                    preparedStatement.executeUpdate();
                }
                else{
                    mergeAmount(userID, resultingUniqueness, currentIngredient.amount);
                }
            }
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static void removeFromGroceryList(int userID, int groceryID){
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("DELETE FROM %s WHERE %s = ? AND %s = ?",
                    GROCERY_LISTS, USER_ID, GROCERY_ID);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, groceryID);
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static void updateGroceryList(int userID, int groceryID, boolean updatedState){
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("UPDATE %s SET %s = ? WHERE %s = ? AND %s = ?",
                    GROCERY_LISTS, GROCERY_CHECKED, USER_ID, GROCERY_ID);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);

            preparedStatement.setBoolean(1, updatedState);
            preparedStatement.setInt(2, userID);
            preparedStatement.setInt(3, groceryID);
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static ArrayList<GroceryListItem> getGroceryListForUser(int userID){
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        ArrayList<GroceryListItem> allGroceryListItems = new ArrayList<>();
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT %s, %s, %s, %s, %s FROM %s WHERE %s = ?",
                    GROCERY_INGREDIENTS, GROCERY_ID, GROCERY_CHECKED, AMOUNT, UNIT, GROCERY_LISTS, USER_ID);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(1, userID);
            resultSet = preparedStatement.executeQuery();

            //looping through restaurants with the input listType
            while (resultSet.next()) {
                GroceryListItem temp = new GroceryListItem();
                String ingredient = resultSet.getString(GROCERY_INGREDIENTS);
                temp.setChecked(resultSet.getBoolean(GROCERY_CHECKED));
                temp.setGroceryID(resultSet.getInt(GROCERY_ID));
                temp.setAmount(resultSet.getFloat(AMOUNT));
                temp.setUnit(resultSet.getString(UNIT));
                temp.setIngredients(ingredient);
                allGroceryListItems.add(temp);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return allGroceryListItems;
    }

    //returns -1 if not unique or groceryID of old item in GroceryList
    private static int verifyUniqueness(int userID, String ingredientName, String unit){
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        int isUnique = -1;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("SELECT %s FROM %s WHERE %s = ? AND %s = ? AND %s = ?",
                GROCERY_ID, GROCERY_LISTS, USER_ID, UNIT, GROCERY_INGREDIENTS);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, unit);
            preparedStatement.setString(3, ingredientName);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                isUnique = resultSet.getInt(GROCERY_ID);
            }

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return isUnique;
    }

    public static void mergeAmount(int userID, int groceryID, float amount){
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            String sqlSelectStatement = String.format("UPDATE %s SET %s = %s + ? WHERE %s = ? AND %s = ?",
                    GROCERY_LISTS, AMOUNT, AMOUNT, USER_ID, GROCERY_ID);
            preparedStatement = connection.prepareStatement(sqlSelectStatement);

            preparedStatement.setFloat(1, amount);
            preparedStatement.setInt(2, userID);
            preparedStatement.setInt(3, groceryID);
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
