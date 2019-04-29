package models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import common.Constants;

public class ImageCollage {
    // Number of images to return
    static String numImages = "10";
    // CSE Search type
    static String searchType = "image";

    //Returns a list of 10 image URLs given a search query
    public static ArrayList<String> search(String query) {
        try {
            ArrayList<String> collage = new ArrayList<>();

            String baseSearchURL = "https://www.googleapis.com/customsearch/v1";
            String resultsURLString = baseSearchURL
                    + "?q=" + URLEncoder.encode(query + " food", StandardCharsets.UTF_8)
                    + "&cx=" + Constants.GOOGLE_SEARCH_ENGINE_ID
                    + "&num=" + ImageCollage.numImages
                    + "&searchType=" + ImageCollage.searchType
                    + "&safe=active"
                    + "&key=" + Constants.GOOGLE_SEARCH_API_KEY;

            URL resultsURL = new URL(resultsURLString);
            HttpURLConnection connection = (HttpURLConnection) resultsURL.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            //Get image links
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String output;

            // Loop through collecting image URLs until end of file
            // Regex representing URL format
            String link = "\"thumbnailLink\": \"";
            while ((output = bufferedReader.readLine()) != null) {
                if (output.contains(link)) {
                    String[] splitLine = output.split("\"");
                    String imageURL = splitLine[3];
                    collage.add(imageURL);
                }
            }
            bufferedReader.close();

            return collage;
        }
        catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
