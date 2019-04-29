package models;

import com.google.gson.Gson;
import common.Constants;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/// Class to generate Restaurant objects from IDs and search queries
public class RestaurantFactory {

    // Returns a Restaurant object with all data necessary given a Google place ID. Makes 2 API calls.
    public static Restaurant fromPlaceID(String id) {
        try {
            // Fetch detail data from G places
            String detailsURL = Restaurant.baseDetailURL
                    + "?placeid=" + id
                    + "&fields=name,formatted_address,website,formatted_phone_number,rating,price_level,place_id"
                    + "&key="+ Constants.GOOGLE_PLACES_API_KEY;
            String restaurantJSON = NetworkReader.readUrl(detailsURL);
            Gson gson = new Gson();
            GooglePlacesRestaurantDetail detailData = gson.fromJson(restaurantJSON, GooglePlacesRestaurantResult.class)
                    .result;
            // Fetch drive time from distance matrix API
            String distanceURL = "https://maps.googleapis.com/maps/api/distancematrix/json"
                    + "?destinations=place_id:" + detailData.place_id
                    + "&origins=" + Constants.RESTAURANT_SEARCH_LATITUDE + "," + Constants.RESTAURANT_SEARCH_LONGITUDE
                    + "&key=" + Constants.GOOGLE_PLACES_API_KEY;
            String distanceJSON = NetworkReader.readUrl(distanceURL);
            ArrayList<GoogleDistanceResultRows> resultRows = gson.fromJson(distanceJSON, GoogleDistanceResult.class).rows;
            // Don't crash if no way to drive here!
            String driveTime = "-";
            int metersAway = 0;
            if (resultRows != null && resultRows.size() > 0) {
                ArrayList<GoogleDistanceResultElement> resultRow = resultRows.get(0).elements;
                if (resultRow != null && resultRow.size() > 0) {
                    // Only here do we have a valid drive time
                    driveTime = resultRow.get(0).duration.text;
                    metersAway = resultRow.get(0).distance.value;
                }
            }

            // Combine both sets of data into Restaurant object
            return new Restaurant(detailData.name,
                    detailData.formatted_address,
                    driveTime,
                    metersAway,
                    detailData.website,
                    detailData.formatted_phone_number,
                    detailData.place_id,
                    RestaurantFactory.parseDoubleWithFallback(detailData.rating),
                    (int)(RestaurantFactory.parseDoubleWithFallback(detailData.price_level)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Restaurant> search(String query, int numberOfResults) {
        return RestaurantFactory.search(query, numberOfResults, 40000);
    }

    // Returns a list of Restaurants given a search query, a given number of results, and a radius in meters
    public static List<Restaurant> search(String query, int numberOfResults, int radius) {
        try {
            // Fetch place IDs for candidate restaurants
            String baseSearchURL = Restaurant.baseSearchURL;
            String resultsURL = baseSearchURL
                    + "?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                    + "&types=restaurant"
                    + "&location=" + Constants.RESTAURANT_SEARCH_LATITUDE + "," + Constants.RESTAURANT_SEARCH_LONGITUDE
                    + "&radius=" + radius
                    + "&key=" + Constants.GOOGLE_PLACES_API_KEY;
            String resultsJSON = NetworkReader.readUrl(resultsURL);
            Gson gson = new Gson();
            GooglePlacesRestaurantsResponses response = gson.fromJson(resultsJSON, GooglePlacesRestaurantsResponses.class);

            // Get details for up to first 20 results
            ArrayList<Restaurant> allResults = RestaurantFactory.googleResponseToRestaurants(response)
                .stream()
                .limit(numberOfResults)
                .filter(r -> r.getListType() != ListType.DO_NOT_SHOW)
                .filter(r -> r.getMetersAway() <= radius)
                .collect(Collectors.toCollection(ArrayList::new));
            ArrayList<Restaurant> favoriteSortedRecipes;

            // Attempt to fetch more restaurants in increments of 20 until we either:
            //      a) reach or exceed the number of results, or,
            //      b) Google has no more results (already got 60 results, or they don't have 60 results)
            int retries = 0;
            final int MAX_RETRIES = 10;
            while (allResults.size() < numberOfResults && response.next_page_token != null && retries < MAX_RETRIES) {
                // There is an indeterminate delay between when the next_page_token is active - retry until it becomes active
                String nextPageToken = response.next_page_token;
                do {
                    Thread.sleep(500);
                    resultsURL = baseSearchURL
                            + "?pagetoken=" + nextPageToken
                            + "&key=" + Constants.GOOGLE_PLACES_API_KEY;
                    resultsJSON = NetworkReader.readUrl(resultsURL);
                    response = gson.fromJson(resultsJSON, GooglePlacesRestaurantsResponses.class);
                    retries++;
                } while (response.status.equals("INVALID_REQUEST"));

                // Parse valid results and append to allResults. Filter out do not shows.
                ArrayList<Restaurant> next20Results = RestaurantFactory.googleResponseToRestaurants(response)
                    .stream()
                    .limit(numberOfResults - allResults.size())
                    .filter(r -> r.getListType() != ListType.DO_NOT_SHOW)
                    .filter(r -> r.getMetersAway() <= radius)
                    .collect(Collectors.toCollection(ArrayList::new));
                allResults.addAll(next20Results);
            }

            // Get favorite restaurants
            favoriteSortedRecipes = allResults.stream().filter(r -> r.getListType() == ListType.FAVORITE)
                    .collect(Collectors.toCollection(ArrayList::new));
            // Append all non-favorite restaurants after the favorites for proper ranking
            favoriteSortedRecipes.addAll(allResults.stream().filter(r -> r.getListType() != ListType.FAVORITE)
                    .sorted(Comparator.comparing(o -> Integer.valueOf(o.getDriveTime().replaceAll("[^\\d.]", ""))))
                    .collect(Collectors.toCollection(ArrayList::new)));
            // Limit number of results
            return favoriteSortedRecipes.subList(0, Integer.min(numberOfResults, favoriteSortedRecipes.size()));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Returns an ArrayList of Restaurant data given a GooglePlacesRestaurantsResponses containing G place IDs
    private static ArrayList<Restaurant> googleResponseToRestaurants(GooglePlacesRestaurantsResponses response) {
        return response
            .results
            .parallelStream()
            .map(s -> RestaurantFactory.fromPlaceID(s.id))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    // Returns the double value of the value string if possible, otherwise returns fallback double value
    private static double parseDoubleWithFallback(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return (double) 0;
        }
    }
}
