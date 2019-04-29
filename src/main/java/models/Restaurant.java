package models;

import com.google.gson.Gson;
import common.Constants;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Represents a Restaurant item including all of its relevant data as well as methods to search or create one from API
public class Restaurant {

    public static String baseSearchURL = Constants.RESTAURANT_BASE_SEARCH_URL;
    public static String baseDetailURL = Constants.RESTAURANT_BASE_DETAIL_URL;

    // When called, triggers invalid URL to test exception handling
    public static void setTestingFailJson(boolean fail) {
        if (fail) {
            Restaurant.baseSearchURL = "";
            Restaurant.baseDetailURL = "";
        } else {

            Restaurant.baseSearchURL = Constants.RESTAURANT_BASE_SEARCH_URL;
            Restaurant.baseDetailURL = Constants.RESTAURANT_BASE_DETAIL_URL;
        }
    }

    //region Instance properties
    private int restaurantID;

    // Restaurant name
    private String name;

    // Address of the restaurant
    private String address;

    // Drive time in minutes from Tommy Trojan to the restaurant
    private String driveTime;

    // Meters away from Tommy Trojan
    private int metersAway;

    // Website URL for the restaurant
    private String webURL;

    // Phone number for the restaurant
    private String phoneNumber;

    // The star rating of the restaurant
    private double stars;

    // The price rating of the restaurant
    private int price;

    // The Google place ID for the restaurant
    private String placeID;

    private int listOrder;

    //endregion

    //region Constructor
    public Restaurant(
        String name,
        String address,
        String driveTime,
        int metersAway,
        String webURL,
        String phoneNumber,
        String placeID,
        double stars,
        int price,
        int listOrder
    ) {
        this.name = name;
        this.address = address;
        this.driveTime = driveTime;
        this.metersAway = metersAway;
        this.webURL = webURL;
        this.phoneNumber = phoneNumber;
        this.placeID = placeID;
        this.stars = stars;
        this.price = price;
        this.listOrder = listOrder;
    }

    public Restaurant(
        String name,
        String address,
        String driveTime,
        int metersAway,
        String webURL,
        String phoneNumber,
        String placeID,
        double stars,
        int price
    ) {
        this.name = name;
        this.address = address;
        this.driveTime = driveTime;
        this.metersAway = metersAway;
        this.webURL = webURL;
        this.phoneNumber = phoneNumber;
        this.placeID = placeID;
        this.stars = stars;
        this.price = price;
    }
    //endregion

    //region Public getters
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDriveTime() {
        return driveTime;
    }

    public int getMetersAway() { return metersAway; }

    public String getWebURL() {
        return webURL;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPlaceID() {
        return placeID;
    }

    public double getStars() {
        return stars;
    }

    public int getPrice() {
        return price;
    }

    public ListType getListType() {
        // TODO Use actual User ID
        return ListManagement.getListTypeFromRestaurant(1, placeID);
    }

    public void setRestaurantID(int restaurantID) {
        this.restaurantID = restaurantID;
    }

    //endregion
}
