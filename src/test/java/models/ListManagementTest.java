package models;

import common.SQLHelper;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListManagementTest extends ListManagement {
    private static int TEST_USERID = 1;
    private static final String MOCK_DATABASE_XML = "src/test/java/resources/mockdatabase.xml";

    @BeforeEach
    void executeSetUpScript(){
        SQLHelper.executeSetUpScript();
    }

    @Test
    void singleInsertRestaurantTest() throws Exception {
        String testPlaceID = "ChIJx4xJnoG4woARDB61pcKADlw";
        final IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(MOCK_DATABASE_XML));
        insertRestaurant(TEST_USERID, testPlaceID);
        ArrayList<Restaurant> restaurantList = getRestaurantSavedLists(TEST_USERID,ListType.NONE);

        assertEquals(1, restaurantList.size());

        final List<String> tableNames = Arrays.asList(dataSet.getTableNames());
        assertEquals(6, tableNames.size());
        ITable restaurantTestTable = dataSet.getTable("Restaurant1");
        assertNotNull(dataSet);

        //expected results
        String expectedPlaceID = (String) restaurantTestTable.getValue(0, PLACE_ID);
        String expectedRestaurantName = (String) restaurantTestTable.getValue(0, RESTAURANT_NAME);
        String expectedAddress = (String) restaurantTestTable.getValue(0, ADDRESS);
        String expectedDriveTime = (String) restaurantTestTable.getValue(0, DRIVE_TIME);
        String expectedWebURL = (String) restaurantTestTable.getValue(0, WEB_URL);
        String expectedPhoneNumber = (String) restaurantTestTable.getValue(0, PHONE_NUMBER);
        ListType expectedListType = ListType.valueOf((String) restaurantTestTable.getValue(0, LIST_TYPE));
        double expectedStars = Double.parseDouble((String) restaurantTestTable.getValue(0, STARS));
        double expectedPrice = Double.parseDouble((String) restaurantTestTable.getValue(0, PRICE));

        Restaurant testRestaurant = restaurantList.get(0);
        assertEquals(expectedPlaceID, testRestaurant.getPlaceID());
        assertEquals(expectedRestaurantName, testRestaurant.getName());
        assertEquals(expectedAddress, testRestaurant.getAddress());
        assertEquals(expectedDriveTime, testRestaurant.getDriveTime());
        assertEquals(expectedWebURL, testRestaurant.getWebURL());
        assertEquals(expectedPhoneNumber, testRestaurant.getPhoneNumber());
        assertEquals(expectedListType, testRestaurant.getListType());
        assertEquals(expectedStars, testRestaurant.getStars());
        assertEquals(expectedPrice, testRestaurant.getPrice());

    }

    @Test
    void restaurantChangeListTypeTest() throws Exception {
        String testPlaceID = "ChIJx4xJnoG4woARDB61pcKADlw";
        final IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(MOCK_DATABASE_XML));
        insertRestaurant(TEST_USERID, testPlaceID);

        final List<String> tableNames = Arrays.asList(dataSet.getTableNames());
        assertEquals(6, tableNames.size());
        ITable restaurantTestTable = dataSet.getTable("Restaurant1");
        assertNotNull(dataSet);

        //expected results
        String expectedPlaceID = (String) restaurantTestTable.getValue(0, PLACE_ID);
        String expectedRestaurantName = (String) restaurantTestTable.getValue(0, RESTAURANT_NAME);
        String expectedAddress = (String) restaurantTestTable.getValue(0, ADDRESS);
        String expectedDriveTime = (String) restaurantTestTable.getValue(0, DRIVE_TIME);
        String expectedWebURL = (String) restaurantTestTable.getValue(0, WEB_URL);
        String expectedPhoneNumber = (String) restaurantTestTable.getValue(0, PHONE_NUMBER);
        ListType expectedListType = ListType.valueOf((String) restaurantTestTable.getValue(0, LIST_TYPE));
        double expectedStars = Double.parseDouble((String) restaurantTestTable.getValue(0, STARS));
        double expectedPrice = Double.parseDouble((String) restaurantTestTable.getValue(0, PRICE));

        ArrayList<Restaurant> restaurantList = getRestaurantSavedLists(TEST_USERID, ListType.NONE);
        Restaurant testRestaurant = restaurantList.get(0);
        assertEquals(expectedPlaceID, testRestaurant.getPlaceID());
        assertEquals(expectedRestaurantName, testRestaurant.getName());
        assertEquals(expectedAddress, testRestaurant.getAddress());
        assertEquals(expectedDriveTime, testRestaurant.getDriveTime());
        assertEquals(expectedWebURL, testRestaurant.getWebURL());
        assertEquals(expectedPhoneNumber, testRestaurant.getPhoneNumber());
        assertEquals(expectedListType, testRestaurant.getListType());
        assertEquals(expectedStars, testRestaurant.getStars());
        assertEquals(expectedPrice, testRestaurant.getPrice());

        ListType newListType = ListType.FAVORITE;
        changeRestaurantListType(TEST_USERID, testPlaceID, newListType);
        ArrayList<Restaurant> actualRestaurantNoneList = getRestaurantSavedLists(TEST_USERID, ListType.NONE);
        ArrayList<Restaurant> actualRestaurantFavoriteList = getRestaurantSavedLists(TEST_USERID, newListType);
        assertEquals(0, actualRestaurantNoneList.size());
        assertEquals(1, actualRestaurantFavoriteList.size());

        Restaurant testRestaurant2 = actualRestaurantFavoriteList.get(0);

        assertEquals(expectedPlaceID, testRestaurant2.getPlaceID());
        assertEquals(expectedRestaurantName, testRestaurant2.getName());
        assertEquals(expectedAddress, testRestaurant2.getAddress());
        assertEquals(expectedDriveTime, testRestaurant2.getDriveTime());
        assertEquals(expectedWebURL, testRestaurant2.getWebURL());
        assertEquals(expectedPhoneNumber, testRestaurant2.getPhoneNumber());
        assertEquals(newListType, testRestaurant2.getListType());
        assertEquals(expectedStars, testRestaurant2.getStars());
        assertEquals(expectedPrice, testRestaurant2.getPrice());

        changeRestaurantListType(TEST_USERID, testPlaceID, ListType.NONE);
        actualRestaurantNoneList = getRestaurantSavedLists(TEST_USERID, ListType.NONE);
        testRestaurant = actualRestaurantNoneList.get(0);
        assertEquals(ListType.NONE, testRestaurant.getListType());
    }

    @Test
    void getRestaurantListTypeTest() throws Exception {
        final String testPlaceID = "ChIJx4xJnoG4woARDB61pcKADlw";
        final IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(MOCK_DATABASE_XML));
        insertRestaurant(TEST_USERID, testPlaceID);
        ArrayList<Restaurant> restaurantList = getRestaurantSavedLists(TEST_USERID, ListType.NONE);

        final List<String> tableNames = Arrays.asList(dataSet.getTableNames());
        assertEquals(6, tableNames.size());
        ITable restaurantTestTable = dataSet.getTable("Restaurant1");
        assertNotNull(dataSet);

        ListType expectedListType = ListType.valueOf((String) restaurantTestTable.getValue(0, LIST_TYPE));
        ListType actualListType = getListTypeFromRestaurant(TEST_USERID, testPlaceID);
        assertNotNull(actualListType);
        assertEquals(expectedListType, actualListType);
    }

    @Test
    void multipleInsertRestaurantTest() throws Exception {
        final String testPlaceID = "ChIJx4xJnoG4woARDB61pcKADlw";
        final String testPlaceID2 = "ChIJLyzMquXHwoAR0RpYK9bAM3M";
        final IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(MOCK_DATABASE_XML));
        insertRestaurant(TEST_USERID, testPlaceID);
        insertRestaurant(TEST_USERID, testPlaceID2);
        ArrayList<Restaurant> restaurantList = getRestaurantSavedLists(TEST_USERID, ListType.NONE);

        final List<String> tableNames = Arrays.asList(dataSet.getTableNames());
        assertEquals(6, tableNames.size());
        assertNotNull(dataSet);
        ITable restaurantTestTable = dataSet.getTable("Restaurant1");
        ITable restaurantTestTable2 = dataSet.getTable("Restaurant2");

        //expected results
        String expectedPlaceID = (String) restaurantTestTable.getValue(0, PLACE_ID);
        String expectedRestaurantName = (String) restaurantTestTable.getValue(0, RESTAURANT_NAME);
        String expectedAddress = (String) restaurantTestTable.getValue(0, ADDRESS);
        String expectedDriveTime = (String) restaurantTestTable.getValue(0, DRIVE_TIME);
        String expectedWebURL = (String) restaurantTestTable.getValue(0, WEB_URL);
        String expectedPhoneNumber = (String) restaurantTestTable.getValue(0, PHONE_NUMBER);
        ListType expectedListType = ListType.valueOf((String) restaurantTestTable.getValue(0, LIST_TYPE));
        double expectedStars = Double.parseDouble((String) restaurantTestTable.getValue(0, STARS));
        double expectedPrice = Double.parseDouble((String) restaurantTestTable.getValue(0, PRICE));

        String expectedPlaceID2 = (String) restaurantTestTable2.getValue(0, PLACE_ID);
        String expectedRestaurantName2 = (String) restaurantTestTable2.getValue(0, RESTAURANT_NAME);
        String expectedAddress2 = (String) restaurantTestTable2.getValue(0, ADDRESS);
        String expectedDriveTime2 = (String) restaurantTestTable2.getValue(0, DRIVE_TIME);
        String expectedWebURL2 = (String) restaurantTestTable2.getValue(0, WEB_URL);
        String expectedPhoneNumber2 = (String) restaurantTestTable2.getValue(0, PHONE_NUMBER);
        ListType expectedListType2 = ListType.valueOf((String) restaurantTestTable2.getValue(0, LIST_TYPE));
        double expectedStars2 = Double.parseDouble((String) restaurantTestTable2.getValue(0, STARS));
        double expectedPrice2 = Double.parseDouble((String) restaurantTestTable2.getValue(0, PRICE));

        Restaurant testRestaurant = restaurantList.get(0);
        assertEquals(expectedPlaceID, testRestaurant.getPlaceID());
        assertEquals(expectedRestaurantName, testRestaurant.getName());
        assertEquals(expectedAddress, testRestaurant.getAddress());
        assertEquals(expectedDriveTime, testRestaurant.getDriveTime());
        assertEquals(expectedWebURL, testRestaurant.getWebURL());
        assertEquals(expectedPhoneNumber, testRestaurant.getPhoneNumber());
        assertEquals(expectedListType, testRestaurant.getListType());
        assertEquals(expectedStars, testRestaurant.getStars());
        assertEquals(expectedPrice, testRestaurant.getPrice());

        Restaurant testRestaurant2 = restaurantList.get(1);
        assertEquals(expectedPlaceID2, testRestaurant2.getPlaceID());
        assertEquals(expectedRestaurantName2, testRestaurant2.getName());
        assertEquals(expectedAddress2, testRestaurant2.getAddress());
        assertEquals(expectedDriveTime2, testRestaurant2.getDriveTime());
        assertEquals(expectedWebURL2, testRestaurant2.getWebURL());
        assertEquals(expectedPhoneNumber2, testRestaurant2.getPhoneNumber());
        assertEquals(expectedListType2, testRestaurant2.getListType());
        assertEquals(expectedStars2, testRestaurant2.getStars());
        assertEquals(expectedPrice2, testRestaurant2.getPrice());
    }

    @Test
    void singleInsertRecipeTest() throws Exception {
        String[] ingredients1 = new String[]{"2 pieces of wholemeal bread", "30 grams of margarine or butter"};
        String[] steps1 = new String[]{"Place your bread in your toaster and put it on for 2 mins",
            "When your toast has been toast spread 15 grams of margarine or butter on each piece of toast.",
            "Put it in a plate, eat with jam or marmalade and enjoy!"};

        Recipe recipeTest = new Recipe("Toast", "8min", "2min", 3, ingredients1, steps1,
            "http://allrecipes.co.uk/recipe/7592/toast.aspx", "http://ukcdn.ar-cdn.com/recipes/port500/30da46e0-925c-40e3-b26e-d6c39a7eeb13.jpg");
        final IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(MOCK_DATABASE_XML));
        insertRecipe(TEST_USERID, recipeTest);
        ArrayList<Recipe> recipeList = getRecipeSavedLists(TEST_USERID, ListType.NONE);

        final List<String> tableNames = Arrays.asList(dataSet.getTableNames());
        assertEquals(6, tableNames.size());
        ITable recipeTestTable = dataSet.getTable("Recipe1");
        assertNotNull(dataSet);

        //expected results
        String expectedRecipeName = (String) recipeTestTable.getValue(0, RECIPE_NAME);
        String expectedPrepTime = (String) recipeTestTable.getValue(0, PREP_TIME);
        String expectedCookTime = (String) recipeTestTable.getValue(0, COOK_TIME);
        String expectedIngredients = (String) recipeTestTable.getValue(0, INGREDIENTS);
        String expectedSteps = (String) recipeTestTable.getValue(0, STEPS);
        ListType expectedListType = ListType.valueOf((String) recipeTestTable.getValue(0, LIST_TYPE));
        String expectedImageURL = (String) recipeTestTable.getValue(0, IMAGE_URL);
        String expectedRecipeURL = (String) recipeTestTable.getValue(0, RECIPE_URL);
        double expectedStars = Double.parseDouble((String) recipeTestTable.getValue(0, STARS));

        Recipe testRecipe1 = recipeList.get(0);

        assertEquals(expectedRecipeName, testRecipe1.getName());
        assertEquals(expectedPrepTime, testRecipe1.getPrepTime());
        assertEquals(expectedCookTime, testRecipe1.getCookTime());
        assertEquals(expectedImageURL, testRecipe1.getImageURL());
        assertEquals(expectedRecipeURL, testRecipe1.getRecipeURL());
        assertEquals(expectedListType, testRecipe1.getListType());
        assertEquals(expectedStars, testRecipe1.getStars());
        assertEquals(expectedIngredients, String.join(";", recipeTest.getIngredients()));
        assertEquals(expectedSteps, String.join(";", recipeTest.getSteps()));

    }

    @Test
    void getRecipeTypeTest() throws Exception {
        String[] ingredients1 = new String[]{"2 pieces of wholemeal bread", "30 grams of margarine or butter"};
        String[] steps1 = new String[]{"Place your bread in your toaster and put it on for 2 mins",
            "When your toast has been toast spread 15 grams of margarine or butter on each piece of toast.",
            "Put it in a plate, eat with jam or marmalade and enjoy!"};

        Recipe recipeTest = new Recipe("Toast", "8min", "2min", 3, ingredients1, steps1,
            "http://allrecipes.co.uk/recipe/7592/toast.aspx", "http://ukcdn.ar-cdn.com/recipes/port500/30da46e0-925c-40e3-b26e-d6c39a7eeb13.jpg");
        final IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(MOCK_DATABASE_XML));
        insertRecipe(TEST_USERID, recipeTest);
        ArrayList<Recipe> recipeList = getRecipeSavedLists(TEST_USERID, ListType.NONE);

        final List<String> tableNames = Arrays.asList(dataSet.getTableNames());
        assertEquals(6, tableNames.size());
        ITable recipeTestTable = dataSet.getTable("Recipe1");
        assertNotNull(dataSet);

        ListType expectedListType = ListType.valueOf((String) recipeTestTable.getValue(0, LIST_TYPE));
        ListType actualListType = getListTypeFromRecipe(TEST_USERID, recipeTest.getRecipeURL());
        assertNotNull(actualListType);
        assertEquals(expectedListType, actualListType);
    }

    @Test
    void recipeChangeListTypeTest() throws Exception {
        String[] ingredients1 = new String[]{"2 pieces of wholemeal bread", "30 grams of margarine or butter"};
        String[] steps1 = new String[]{"Place your bread in your toaster and put it on for 2 mins",
            "When your toast has been toast spread 15 grams of margarine or butter on each piece of toast.",
            "Put it in a plate, eat with jam or marmalade and enjoy!"};

        Recipe recipeTest = new Recipe("Toast", "8min", "2min", 3, ingredients1, steps1,
            "http://allrecipes.co.uk/recipe/7592/toast.aspx", "http://ukcdn.ar-cdn.com/recipes/port500/30da46e0-925c-40e3-b26e-d6c39a7eeb13.jpg");
        final IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(MOCK_DATABASE_XML));
        insertRecipe(TEST_USERID, recipeTest);
        ArrayList<Recipe> recipeList = getRecipeSavedLists(TEST_USERID, ListType.NONE);

        final List<String> tableNames = Arrays.asList(dataSet.getTableNames());
        assertEquals(6, tableNames.size());
        ITable recipeTestTable = dataSet.getTable("Recipe1");
        assertNotNull(dataSet);

        //expected results
        String expectedRecipeName = (String) recipeTestTable.getValue(0, RECIPE_NAME);
        String expectedPrepTime = (String) recipeTestTable.getValue(0, PREP_TIME);
        String expectedCookTime = (String) recipeTestTable.getValue(0, COOK_TIME);
        String expectedIngredients = (String) recipeTestTable.getValue(0, INGREDIENTS);
        String expectedSteps = (String) recipeTestTable.getValue(0, STEPS);
        ListType expectedListType = ListType.valueOf((String) recipeTestTable.getValue(0, LIST_TYPE));
        String expectedImageURL = (String) recipeTestTable.getValue(0, IMAGE_URL);
        String expectedRecipeURL = (String) recipeTestTable.getValue(0, RECIPE_URL);
        double expectedStars = Double.parseDouble((String) recipeTestTable.getValue(0, STARS));

        Recipe testRecipe1 = recipeList.get(0);

        assertEquals(expectedRecipeName, testRecipe1.getName());
        assertEquals(expectedPrepTime, testRecipe1.getPrepTime());
        assertEquals(expectedCookTime, testRecipe1.getCookTime());
        assertEquals(expectedImageURL, testRecipe1.getImageURL());
        assertEquals(expectedRecipeURL, testRecipe1.getRecipeURL());
        assertEquals(expectedListType, testRecipe1.getListType());
        assertEquals(expectedStars, testRecipe1.getStars());
        assertEquals(expectedIngredients, String.join(";", recipeTest.getIngredients()));
        assertEquals(expectedSteps, String.join(";", recipeTest.getSteps()));

        ListType newListType = ListType.FAVORITE;
        changeRecipeListType(TEST_USERID, testRecipe1, newListType);
        ArrayList<Recipe> actualRecipeNoneList = getRecipeSavedLists(TEST_USERID, ListType.NONE);
        ArrayList<Recipe> actualRecipeFavoriteList = getRecipeSavedLists(TEST_USERID, newListType);
        assertEquals(0, actualRecipeNoneList.size());
        assertEquals(1, actualRecipeFavoriteList.size());

        Recipe testRecipe2 = actualRecipeFavoriteList.get(0);
        assertEquals(expectedRecipeName, testRecipe2.getName());
        assertEquals(expectedPrepTime, testRecipe2.getPrepTime());
        assertEquals(expectedCookTime, testRecipe2.getCookTime());
        assertEquals(expectedImageURL, testRecipe2.getImageURL());
        assertEquals(expectedRecipeURL, testRecipe2.getRecipeURL());
        assertEquals(newListType, testRecipe2.getListType());
        assertEquals(expectedStars, testRecipe2.getStars());
        assertEquals(expectedIngredients, String.join(";", testRecipe2.getIngredients()));
        assertEquals(expectedSteps, String.join(";", testRecipe2.getSteps()));

        changeRecipeListType(TEST_USERID, testRecipe1, ListType.NONE);
        actualRecipeNoneList = getRecipeSavedLists(TEST_USERID, ListType.NONE);
        testRecipe2 = actualRecipeNoneList.get(0);
        assertEquals(ListType.NONE, testRecipe2.getListType());
    }

    @Test
    void insertSearchTermTest() throws Exception{
        final IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(MOCK_DATABASE_XML));
        assertNotNull(dataSet);
        insertSearchTerm(TEST_USERID, "burger");
        ArrayList<String> termList = getAllSearchedItems(TEST_USERID);

        final List<String> tableNames = Arrays.asList(dataSet.getTableNames());
        assertEquals(6, tableNames.size());
        ITable searchTermTable = dataSet.getTable("SearchTerm");

        assertEquals(1, termList.size());
        assertEquals("burger", termList.get(0));
    }

    @Test
    void RestaurantOrderTest() throws Exception{
        final String testPlaceID = "ChIJx4xJnoG4woARDB61pcKADlw";
        final String testPlaceID2 = "ChIJLyzMquXHwoAR0RpYK9bAM3M";

        insertRestaurant(TEST_USERID, testPlaceID);
        insertRestaurant(TEST_USERID, testPlaceID2);
        ArrayList<RestaurantOrder> restaurantOrders = getRestaurantOrderOfListType(TEST_USERID, ListType.NONE);

        assertEquals(2, restaurantOrders.size());
        assertEquals(0, restaurantOrders.get(0).getIndex());
        assertEquals(1, restaurantOrders.get(0).getRestaurantID());
        assertEquals(1, restaurantOrders.get(1).getIndex());
        assertEquals(2, restaurantOrders.get(1).getRestaurantID());
    }

    @Test
    void RestaurantAndRecipeOrderTest() throws Exception{
        final String testPlaceID = "ChIJx4xJnoG4woARDB61pcKADlw";
        final String testPlaceID2 = "ChIJLyzMquXHwoAR0RpYK9bAM3M";
        String[] ingredients = new String[]{"2 pieces of wholemeal bread", "30 grams of margarine or butter"};
        String[] steps = new String[]{"Place your bread in your toaster and put it on for 2 mins",
                "When your toast has been toast spread 15 grams of margarine or butter on each piece of toast.",
                "Put it in a plate, eat with jam or marmalade and enjoy!"};

        Recipe testRecipe = new Recipe("Toast", "8min", "2min", 3, ingredients, steps,
                "http://allrecipes.co.uk/recipe/7592/toast.aspx", "http://ukcdn.ar-cdn.com/recipes/port500/30da46e0-925c-40e3-b26e-d6c39a7eeb13.jpg");
        insertRestaurant(TEST_USERID, testPlaceID);
        insertRecipe(TEST_USERID, testRecipe);
        insertRestaurant(TEST_USERID, testPlaceID2);
        ArrayList<RestaurantOrder> restaurantOrders = getRestaurantOrderOfListType(TEST_USERID, ListType.NONE);
        ArrayList<RecipeOrder> recipeOrders = getRecipeOrderOfListType(TEST_USERID, ListType.NONE);


        assertEquals(2, restaurantOrders.size());
        assertEquals(0, restaurantOrders.get(0).getIndex());
        assertEquals(1, restaurantOrders.get(0).getRestaurantID());
        assertEquals(2, restaurantOrders.get(1).getIndex());
        assertEquals(2, restaurantOrders.get(1).getRestaurantID());

        assertEquals(1, recipeOrders.size());
        assertEquals(1, recipeOrders.get(0).getIndex());
        assertEquals(1, recipeOrders.get(0).getRecipeID());

    }

    @Test
    void RestaurantRecipeUpdatedRemovalOrderTest() throws Exception{
        final String testPlaceID = "ChIJx4xJnoG4woARDB61pcKADlw";
        final String testPlaceID2 = "ChIJLyzMquXHwoAR0RpYK9bAM3M";
        String[] ingredients = new String[]{"2 pieces of wholemeal bread", "30 grams of margarine or butter"};
        String[] steps = new String[]{"Place your bread in your toaster and put it on for 2 mins",
                "When your toast has been toast spread 15 grams of margarine or butter on each piece of toast.",
                "Put it in a plate, eat with jam or marmalade and enjoy!"};

        Recipe testRecipe = new Recipe("Toast", "8min", "2min", 3, ingredients, steps,
                "http://allrecipes.co.uk/recipe/7592/toast.aspx", "http://ukcdn.ar-cdn.com/recipes/port500/30da46e0-925c-40e3-b26e-d6c39a7eeb13.jpg");
        insertRestaurant(TEST_USERID, testPlaceID);
        insertRecipe(TEST_USERID, testRecipe);
        insertRestaurant(TEST_USERID, testPlaceID2);
        ArrayList<RestaurantOrder> restaurantOrders = getRestaurantOrderOfListType(TEST_USERID, ListType.NONE);
        ArrayList<RecipeOrder> recipeOrders = getRecipeOrderOfListType(TEST_USERID, ListType.NONE);

        assertEquals(2, restaurantOrders.size());
        assertEquals(0, restaurantOrders.get(0).getIndex());
        assertEquals(1, restaurantOrders.get(0).getRestaurantID());
        assertEquals(2, restaurantOrders.get(1).getIndex());
        assertEquals(2, restaurantOrders.get(1).getRestaurantID());

        assertEquals(1, recipeOrders.size());
        assertEquals(1, recipeOrders.get(0).getIndex());
        assertEquals(1, recipeOrders.get(0).getRecipeID());

        changeRecipeListType(TEST_USERID, testRecipe, ListType.FAVORITE);
        recipeOrders = getRecipeOrderOfListType(TEST_USERID, ListType.FAVORITE);
        assertEquals(1, recipeOrders.size());
        assertEquals(0, recipeOrders.get(0).getIndex());

        restaurantOrders = getRestaurantOrderOfListType(TEST_USERID, ListType.NONE);
        assertEquals(2, restaurantOrders.size());
        assertEquals(0, restaurantOrders.get(0).getIndex());
        assertEquals(1, restaurantOrders.get(0).getRestaurantID());
        assertEquals(1, restaurantOrders.get(1).getIndex());
        assertEquals(2, restaurantOrders.get(1).getRestaurantID());
    }

    @Test
    void UpdateRestaurantAndRecipeOrderTest() throws Exception{
        final String testPlaceID = "ChIJx4xJnoG4woARDB61pcKADlw";
        final String testPlaceID2 = "ChIJLyzMquXHwoAR0RpYK9bAM3M";
        String[] ingredients = new String[]{"2 pieces of wholemeal bread", "30 grams of margarine or butter"};
        String[] steps = new String[]{"Place your bread in your toaster and put it on for 2 mins",
                "When your toast has been toast spread 15 grams of margarine or butter on each piece of toast.",
                "Put it in a plate, eat with jam or marmalade and enjoy!"};

        Recipe testRecipe = new Recipe("Toast", "8min", "2min", 3, ingredients, steps,
                "http://allrecipes.co.uk/recipe/7592/toast.aspx", "http://ukcdn.ar-cdn.com/recipes/port500/30da46e0-925c-40e3-b26e-d6c39a7eeb13.jpg");
        insertRestaurant(TEST_USERID, testPlaceID);
        insertRecipe(TEST_USERID, testRecipe);
        insertRestaurant(TEST_USERID, testPlaceID2);
        ArrayList<RestaurantOrder> restaurantOrders = getRestaurantOrderOfListType(TEST_USERID, ListType.NONE);
        ArrayList<RecipeOrder> recipeOrders = getRecipeOrderOfListType(TEST_USERID, ListType.NONE);

        assertEquals(2, restaurantOrders.size());
        assertEquals(0, restaurantOrders.get(0).getIndex());
        assertEquals(1, restaurantOrders.get(0).getRestaurantID());
        assertEquals(2, restaurantOrders.get(1).getIndex());
        assertEquals(2, restaurantOrders.get(1).getRestaurantID());

        assertEquals(1, recipeOrders.size());
        assertEquals(1, recipeOrders.get(0).getIndex());
        assertEquals(1, recipeOrders.get(0).getRecipeID());

        final RestaurantOrder expectedRestaurantOrder1 = new RestaurantOrder(1,1);
        final RestaurantOrder expectedRestaurantOrder2 = new RestaurantOrder(0,2);
        final ArrayList<RestaurantOrder> newRestaurantOrder= new ArrayList<>();
        newRestaurantOrder.add(expectedRestaurantOrder1);
        newRestaurantOrder.add(expectedRestaurantOrder2);
        updateListOrderForRestaurants(TEST_USERID, ListType.NONE, newRestaurantOrder);
        ArrayList<RestaurantOrder> actualRestaurantOrders = getRestaurantOrderOfListType(TEST_USERID, ListType.NONE);

        assertEquals(2, actualRestaurantOrders.size());
        assertEquals(expectedRestaurantOrder1.getRestaurantID(), actualRestaurantOrders.get(1).getRestaurantID());
        assertEquals(expectedRestaurantOrder1.getIndex(), actualRestaurantOrders.get(1).getIndex());
        assertEquals(expectedRestaurantOrder2.getRestaurantID(), actualRestaurantOrders.get(0).getRestaurantID());
        assertEquals(expectedRestaurantOrder2.getIndex(), actualRestaurantOrders.get(0).getIndex());

        final RecipeOrder expectedRecipeOrder = new RecipeOrder(2, 1);
        final ArrayList<RecipeOrder> newRecipeOrder= new ArrayList<>();
        newRecipeOrder.add(expectedRecipeOrder);
        updateListOrderForRecipes(TEST_USERID, ListType.NONE, newRecipeOrder);
        ArrayList<RecipeOrder> actualRecipeOrders = getRecipeOrderOfListType(TEST_USERID, ListType.NONE);
        assertEquals(1, actualRecipeOrders.size());
        assertEquals(expectedRecipeOrder.getIndex(), actualRecipeOrders.get(0).getIndex());
        assertEquals(expectedRecipeOrder.getRecipeID(), actualRecipeOrders.get(0).getRecipeID());

    }
}
