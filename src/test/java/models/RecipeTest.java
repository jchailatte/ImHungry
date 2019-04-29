package models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecipeTest {
    private Recipe testRecipe1;

    @BeforeAll
    void setUp() {
        testRecipe1 = Recipe.fromRecipeURL("http://allrecipes.co.uk/recipe/18744/chipotle-prawn-tacos.aspx");
    }

    @Test
    void getName() {
        assertNotNull(testRecipe1.getName());
        assertEquals("Chipotle prawn tacos", testRecipe1.getName());

    }

    @Test
    void getPrepTime() {
        assertNotNull(testRecipe1.getPrepTime());
        assertEquals("15 min", testRecipe1.getPrepTime());
    }

    @Test
    void getCookTime() {
        assertNotNull(testRecipe1.getCookTime());
        assertEquals("15 min", testRecipe1.getPrepTime());
    }

    @Test
    void getIngredients() {
        assertNotNull(testRecipe1.getIngredients());
        String[] expectedIngredients = {
            "340g streaky bacon, cut into small pieces",
            "1 onion, diced",
            "900g large cooked prawns - peeled, deveined and halved",
            "3 chipotle chillies in adobo sauce, minced",
            "12 corn tortillas",
            "45g chopped fresh coriander",
            "1 lime, juiced",
            "salt to taste"
        };
        for (int i = 0; i < expectedIngredients.length; i++) {
            assertEquals(expectedIngredients[i], testRecipe1.getIngredients()[i]);
        }
    }

    @Test
    void getSteps() {
        assertNotNull(testRecipe1.getSteps());
        String[] expectedSteps = {
            "In a large, deep frying pan, fry the bacon over medium-high heat until evenly brown. Drain excess fat. Add the onions to the pan " +
                "with the bacon and cook 5 minutes or until the onions are translucent. Stir in the prawns and chipotle chillies; cook 4 minutes or until heated through.",
            "Heat tortillas on an ungreased frying pan over medium-high heat for 10 to 15 seconds. Turn and heat for another 5 to 10 seconds.  Fill the heated tortillas " +
                "with prawn mixture.  Sprinkle with coriander, lime juice and salt."
        };
        for (int i = 0; i < expectedSteps.length; i++) {
            assertEquals(expectedSteps[i], testRecipe1.getSteps()[i]);
        }
    }

    @Test
    void getStars() {
        assertTrue(testRecipe1.getStars() >= 0);
        assertTrue(testRecipe1.getStars() <= 5);
        assertEquals(4.5, testRecipe1.getStars());
    }

    @Test
    void getListType() {
        assertNotNull(testRecipe1.getListType());
    }

    @Test
    void getRecipeURL() {
        assertNotNull(testRecipe1.getRecipeURL());
    }

    @Test
    void getImageURL() {
        assertNotNull(testRecipe1.getImageURL());
        assertEquals("//ukcdn.ar-cdn.com/recipes/port512/8263c59d-fdb2-4676-8351-9cdd799f3b72.jpg", testRecipe1.getImageURL());
    }

    @Test
    void setListType() {
        testRecipe1.setListType(ListType.FAVORITE);
        assertEquals(ListType.FAVORITE, testRecipe1.getListType());

        testRecipe1.setListType(ListType.DO_NOT_SHOW);
        assertEquals(ListType.DO_NOT_SHOW, testRecipe1.getListType());
    }

    @Test
    void search() {
        List<Recipe> results = Recipe.search("tacos", 5);
        assertNotNull(results);
        assertEquals(results.size(), 5);
        assertNotEquals(results.size(), 0);
    }

    @Test
    void fromRecipeURL() {
        assertNotNull(testRecipe1);
        assertEquals("Chipotle prawn tacos", testRecipe1.getName());
    }

//    @Test
//    void exceptionHandling() {
//        Recipe.setTestingFailJson(true);
//        testRecipe1 = Recipe.fromRecipeURL("http://allrecipes.co.uk/recipe/18744/chipotle-prawn-tacos.aspx");
//        assertNull(testRecipe1);
//        List<Recipe> badList = Recipe.search("tacos", 5);
//        assertEquals(badList.size(), 0);
//        Recipe.setTestingFailJson(false);
//    }

    @Test
    void noResults() {
        List<Recipe> noResults = Recipe.search("qwertyuiopmnbvcxzasdfghjkl", 5);
        assertEquals(noResults.size(), 0);
    }
}
