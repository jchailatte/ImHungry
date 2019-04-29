package models;

public class RestaurantOrder {
    int index;
    int restaurantID;

    public RestaurantOrder(){}

    public RestaurantOrder(int index, int restaurantID) {
        this.index = index;
        this.restaurantID = restaurantID;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(int restaurantID) {
        this.restaurantID = restaurantID;
    }
}