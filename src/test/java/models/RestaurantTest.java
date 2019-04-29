package models;

import com.google.gson.Gson;
import common.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestaurantTest {
    private Restaurant testRestaurant1, testRestaurant2;

    @BeforeAll
    void setUp() {
        testRestaurant1 = RestaurantFactory.fromPlaceID("ChIJJewhXDol6IARKGxyEnT4sIA");
        testRestaurant2 = RestaurantFactory.fromPlaceID("ChIJ43K_hOfHwoARUvQ16fLfiJM");
    }

    @Test
    void getName() {
        assertNotNull(testRestaurant1.getName());
        assertNotNull(testRestaurant2.getName());
        assertNotEquals(testRestaurant1.getName(), testRestaurant2.getName());
        assertEquals("Umami Burger", testRestaurant1.getName());
        assertEquals("Taco Bell", testRestaurant2.getName());
    }

    @Test
    void getAddress() {
        assertNotNull(testRestaurant1.getAddress());
        assertNotNull(testRestaurant2.getAddress());
        assertNotEquals(testRestaurant1.getAddress(), testRestaurant2.getAddress());
        assertEquals("852 S Broadway, Los Angeles, CA 90014, USA", testRestaurant1.getAddress());
        assertEquals("2718 S Figueroa St, Los Angeles, CA 90007, USA", testRestaurant2.getAddress());
    }

    @Test
    void getDriveTime() {
        assertNotNull(testRestaurant1.getDriveTime());
        assertNotNull(testRestaurant2.getDriveTime());
        assertNotEquals(testRestaurant1.getDriveTime(), testRestaurant2.getDriveTime());
        assertEquals("17 mins", testRestaurant1.getDriveTime());
        assertEquals("10 mins", testRestaurant2.getDriveTime());
    }

    @Test
    void getWebURL() {
        assertNotNull(testRestaurant1.getWebURL());
        assertNotNull(testRestaurant2.getWebURL());
        assertNotEquals(testRestaurant1.getWebURL(), testRestaurant2.getWebURL());
        assertEquals("https://www.umamiburger.com/locations/broadway/?utm_source=Google%20My%20Business&" +
            "utm_medium=Website%20Button&utm_campaign=Los%20Angeles", testRestaurant1.getWebURL());
        assertEquals("https://locations.tacobell.com/ca/los-angeles/2718-s-figueroa-st.html?" +
            "utm_source=yext&utm_campaign=googlelistings&utm_medium=referral&utm_term=028707&utm_content=website", testRestaurant2.getWebURL());
    }

    @Test
    void getPhoneNumber() {
        assertNotNull(testRestaurant1.getPhoneNumber());
        assertNotNull(testRestaurant2.getPhoneNumber());
        assertNotEquals(testRestaurant1.getPhoneNumber(), testRestaurant2.getPhoneNumber());
        assertEquals("(213) 413-8626", testRestaurant1.getPhoneNumber());
        assertEquals("(213) 746-6248", testRestaurant2.getPhoneNumber());
    }

    @Test
    void getPlaceID() {
        assertNotNull(testRestaurant1.getPlaceID());
        assertNotNull(testRestaurant2.getPlaceID());
        assertNotEquals(testRestaurant1.getPlaceID(), testRestaurant2.getPlaceID());
        assertEquals("ChIJJewhXDol6IARKGxyEnT4sIA", testRestaurant1.getPlaceID());
        assertEquals("ChIJ43K_hOfHwoARUvQ16fLfiJM", testRestaurant2.getPlaceID());
    }

    @Test
    void getStars() {
        assertTrue(testRestaurant1.getStars() >= 0);
        assertTrue(testRestaurant1.getStars() <= 5);
        assertTrue(testRestaurant2.getStars() >= 0);
        assertTrue(testRestaurant2.getStars() >= 0);
        assertNotEquals(testRestaurant1.getStars(), testRestaurant2.getStars());
        assertEquals(4.2, testRestaurant1.getStars());
        assertEquals(3.9, testRestaurant2.getStars());
    }

    @Test
    void getPrice() {
        assertTrue(testRestaurant1.getPrice() >= 0);
        assertTrue(testRestaurant1.getPrice() <= 4);
        assertTrue(testRestaurant2.getPrice() >= 0);
        assertTrue(testRestaurant2.getPrice() <= 4);
        assertNotEquals(testRestaurant1.getPrice(), testRestaurant2.getPrice());
        assertEquals(2, testRestaurant1.getPrice());
        assertEquals(1, testRestaurant2.getPrice());
    }

    @Test
    void getListType() {
        assertNotNull(testRestaurant1.getListType());
        assertNotNull(testRestaurant2.getListType());
    }

    @Test
    void search() {
        List<Restaurant> results = RestaurantFactory.search("tacos", 5);
        assertNotNull(results);
        assertEquals(results.size(), 5);
        assertNotEquals(results.size(), 0);
    }

    @Test
    void searchRadius() {
        List<String> resultsExclusive = RestaurantFactory.search("burgers", 5, 1600).stream()
                .map(Restaurant::getPlaceID).collect(Collectors.toCollection(ArrayList::new));
        // Ensure limited locations doesn't have place that is far away
        assertFalse(resultsExclusive.contains("ChIJj8k3FDnGwoARYPPFbgkxVqQ"));
    }

    @Test
    void searchRadiusResults() {
        // Ensure limited locations doesn't have place that is far away
        List resultsInclusive = RestaurantFactory.search("food", 5, 40000);
        List resultsExclusive = RestaurantFactory.search("food", 5, 35000);

        assertEquals(resultsExclusive.size(), resultsInclusive.size());
    }

    @Test
    void searchFail() {
        Restaurant.setTestingFailJson(true);
        assertEquals(RestaurantFactory.search("tacos", 5).size(), 0);
        assertNull(RestaurantFactory.fromPlaceID("abc"));
        Restaurant.setTestingFailJson(false);
    }

    @Test
    void fromPlaceID() {
        assertNotNull(testRestaurant1);
        assertEquals(testRestaurant1.getName(), "Umami Burger");

        assertNotNull(testRestaurant2);
        assertEquals(testRestaurant2.getName(), "Taco Bell");
    }

    @Test
    void searchResultRadiusInBounds() {
        int radius = 4000;
        List<Restaurant> resultsExclusive = RestaurantFactory.search("burgers", 5, radius);
        Gson gson = new Gson();
        for (Restaurant r : resultsExclusive) {
            // Fetch drive time from distance matrix API
            String distanceURL = "https://maps.googleapis.com/maps/api/distancematrix/json"
                    + "?destinations=place_id:" + r.getPlaceID()
                    + "&origins=" + Constants.RESTAURANT_SEARCH_LATITUDE+ "," + Constants.RESTAURANT_SEARCH_LONGITUDE
                    + "&key=" + Constants.GOOGLE_PLACES_API_KEY;
            String distanceJSON = null;
            try {
                distanceJSON = NetworkReader.readUrl(distanceURL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ArrayList<GoogleDistanceResultRows> resultRows = gson.fromJson(distanceJSON, GoogleDistanceResult.class).rows;
            if (resultRows != null && resultRows.size() > 0) {
                ArrayList<GoogleDistanceResultElement> resultRow = resultRows.get(0).elements;
                if (resultRow != null && resultRow.size() > 0) {
                    // Ensure valid distance (in bounds)
                    assertTrue(resultRow.get(0).distance.value <= radius);
                }
            }
        }
    }


}
