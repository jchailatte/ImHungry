package models;

import java.util.List;

/// Used for GSON to parse G Places response into list of Restaurant items when deserializing.
public class GooglePlacesRestaurantsResponses {
    List<GooglePlacesRestaurant> results;
    String next_page_token;
    String status;
}

