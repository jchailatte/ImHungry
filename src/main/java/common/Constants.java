package common;

public class Constants {
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "";

    public static final String DATABASE_CONNECTION = "jdbc:mysql://localhost:3306/imhungry"
            + "?user=" + DATABASE_USER
            + "&password=" + DATABASE_PASSWORD
            + "&useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";
    public static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    public static final String GOOGLE_PLACES_API_KEY = "get";
    public static final String GOOGLE_SEARCH_API_KEY = "your";
    public static final String GOOGLE_SEARCH_ENGINE_ID = "own";
    public static final int DEFAULT_NUMBER_OF_RESULTS = 5;
    // Tommy Trojan latitude--search results are centered from here and directions are from here
    public static final String RESTAURANT_SEARCH_LATITUDE = "34.020775";
    // Tommy Trojan longitude--search results are centered from here and directions are from here
    public static final  String RESTAURANT_SEARCH_LONGITUDE = "-118.285479";
    // Base Google search API URL
    public static final String RESTAURANT_BASE_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";
    // Base Google place detail API URL
    public static final String RESTAURANT_BASE_DETAIL_URL = "https://maps.googleapis.com/maps/api/place/details/json";
}
