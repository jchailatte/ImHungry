package models;

import com.google.gson.annotations.SerializedName;

/// Represents one restaurant with its place ID from first G Place call. This ID us then used to gather details.
public class GooglePlacesRestaurant {
    @SerializedName("place_id")
    public String id;
}
