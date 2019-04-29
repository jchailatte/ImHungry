package models;

import common.SQLHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class GroceryListTest extends GroceryListManagement{
    private static int TEST_USERID = 1;
    private static int TEST_RECIPEID = 1;
    private static int TEST_RECIPEID2 = 2;
    private static int TEST_GROCERYID = 1;

    @BeforeEach
    void executeSetUpScript() {
        SQLHelper.executeSetUpScript();}

    @Test
    void singleInsertToGroceryListTest() throws Exception {
        String[] ingredients1 = new String[]{"2 pieces of wholemeal bread", "30 grams of margarine or butter"};
        String[] steps1 = new String[]{"Place your bread in your toaster and put it on for 2 mins",
                "When your toast has been toast spread 15 grams of margarine or butter on each piece of toast.",
                "Put it in a plate, eat with jam or marmalade and enjoy!"};

        Recipe recipeTest = new Recipe("Toast", "8min", "2min", 3, ingredients1, steps1,
                "http://allrecipes.co.uk/recipe/7592/toast.aspx", "http://ukcdn.ar-cdn.com/recipes/port500/30da46e0-925c-40e3-b26e-d6c39a7eeb13.jpg");
        // Insert Recipe and GroceryList
        ListManagement.insertRecipe(TEST_USERID, recipeTest);
        insertToGroceryList(TEST_USERID, TEST_RECIPEID);

        //Retrieve groceryListItems, should be 1
        ArrayList<GroceryListItem> groceryList = getGroceryListForUser(TEST_USERID);
        assertEquals(2, groceryList.size());

        //expected results
        assertNotNull( groceryList.get(0));
        assertNotNull( groceryList.get(1));
        assertEquals(false, groceryList.get(0).getChecked());
        assertEquals(false, groceryList.get(1).getChecked());
        assertEquals("wholemeal bread", groceryList.get(0).getIngredients());
        assertEquals("margarine or butter", groceryList.get(1).getIngredients());
        assertEquals("", groceryList.get(0).getUnit());
        assertEquals("grams", groceryList.get(1).getUnit());
        assertEquals(2, groceryList.get(0).getAmount());
        assertEquals(30, groceryList.get(1).getAmount());

    }

    @Test
    void singleRemoveFromGroceryListTest() throws Exception {
        String[] ingredients1 = new String[]{"2 pieces of wholemeal bread", "30 grams of margarine or butter"};
        String[] steps1 = new String[]{"Place your bread in your toaster and put it on for 2 mins",
                "When your toast has been toast spread 15 grams of margarine or butter on each piece of toast.",
                "Put it in a plate, eat with jam or marmalade and enjoy!"};

        Recipe recipeTest = new Recipe( "Toast", "8min", "2min", 3, ingredients1, steps1,
                "http://allrecipes.co.uk/recipe/7592/toast.aspx", "http://ukcdn.ar-cdn.com/recipes/port500/30da46e0-925c-40e3-b26e-d6c39a7eeb13.jpg");
        // Insert Recipe and GroceryList
        ListManagement.insertRecipe(TEST_USERID, recipeTest);
        insertToGroceryList(TEST_USERID, TEST_RECIPEID);

        //Remove from grocerylist
        removeFromGroceryList(TEST_USERID, TEST_GROCERYID);

        //Retrieve groceryListItems, should be 1
        ArrayList<GroceryListItem> groceryList = getGroceryListForUser(TEST_USERID);

        assertEquals(1, groceryList.size());
        assertEquals("margarine or butter", groceryList.get(0).getIngredients());
    }

    @Test
    void updateChecklistTest() throws Exception {
        String[] ingredients1 = new String[]{"2 pieces of wholemeal bread", "30 grams of margarine or butter"};
        String[] steps1 = new String[]{"Place your bread in your toaster and put it on for 2 mins",
                "When your toast has been toast spread 15 grams of margarine or butter on each piece of toast.",
                "Put it in a plate, eat with jam or marmalade and enjoy!"};

        Recipe recipeTest = new Recipe( "Toast", "8min", "2min", 3, ingredients1, steps1,
                "http://allrecipes.co.uk/recipe/7592/toast.aspx", "http://ukcdn.ar-cdn.com/recipes/port500/30da46e0-925c-40e3-b26e-d6c39a7eeb13.jpg");
        // Insert Recipe and GroceryList
        ListManagement.insertRecipe(TEST_USERID, recipeTest);
        insertToGroceryList(TEST_USERID, TEST_RECIPEID);

        //Update Checklist
        updateGroceryList(TEST_USERID, TEST_GROCERYID, true);

        //Retrieve groceryListItems, should be 1
        ArrayList<GroceryListItem> groceryList = getGroceryListForUser(TEST_USERID);

        assertEquals(2, groceryList.size());
        assertNotNull( groceryList.get(0));
        assertNotNull( groceryList.get(1));
        assertEquals(1, groceryList.get(0).getGroceryID());
        assertEquals(2, groceryList.get(1).getGroceryID());
        assertEquals(true, groceryList.get(0).getChecked());
        assertEquals(false, groceryList.get(1).getChecked());
        assertEquals("wholemeal bread", groceryList.get(0).getIngredients());
        assertEquals("margarine or butter", groceryList.get(1).getIngredients());
    }

    @Test
    void mergeIngredientsTest() throws Exception{
        String[] ingredients1 = new String[]{"2 pieces of wholemeal bread", "30 grams of margarine or butter"};
        String[] steps1 = new String[]{"Place your bread in your toaster and put it on for 2 mins",
                "When your toast has been toast spread 15 grams of margarine or butter on each piece of toast.",
                "Put it in a plate, eat with jam or marmalade and enjoy!"};

        Recipe recipeTest = new Recipe("Toast", "8min", "2min", 3, ingredients1, steps1,
                "http://allrecipes.co.uk/recipe/7592/toast.aspx", "http://ukcdn.ar-cdn.com/recipes/port500/30da46e0-925c-40e3-b26e-d6c39a7eeb13.jpg");

        Recipe recipeTest2 =  new Recipe("Toast", "8min", "2min", 3, ingredients1, steps1,
                "DIFFERENT_LINK", "http://ukcdn.ar-cdn.com/recipes/port500/30da46e0-925c-40e3-b26e-d6c39a7eeb13.jpg");
        ListManagement.insertRecipe(TEST_USERID, recipeTest);
        ListManagement.insertRecipe(TEST_USERID, recipeTest2);
        insertToGroceryList(TEST_USERID, TEST_RECIPEID);
        insertToGroceryList(TEST_USERID, TEST_RECIPEID2);

        ArrayList<GroceryListItem> groceryList = getGroceryListForUser(TEST_USERID);
        assertEquals(2, groceryList.size());
        assertEquals(false, groceryList.get(0).getChecked());
        assertEquals(false, groceryList.get(1).getChecked());
        assertEquals("wholemeal bread", groceryList.get(0).getIngredients());
        assertEquals("margarine or butter", groceryList.get(1).getIngredients());
        assertEquals(4, groceryList.get(0).getAmount());
        assertEquals(60, groceryList.get(1).getAmount());
    }
}
