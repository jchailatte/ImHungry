package models;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

// Contains helper method for reading files from URLs
public class NetworkReader {
    // Adapted from SO: https://stackoverflow.com/questions/7467568/parsing-json-from-url
    // Reads input URL and converts contents into String. Useful for parsing network info w/ GSON
    static String readUrl(String urlString) throws Exception {
        String pathname = "http://localhost:8080/cache/" + Math.abs(urlString.hashCode()) + ".txt";

        String cached;
        try {
            cached = readRawUrl(pathname);
        } catch (Exception e) {
            cached = "";
        }

        if (cached != "") {
            // return good cache data
            return cached;
        }
        return readRawUrl(urlString);
    }

    private static String readRawUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public static ArrayList<IngredientParserResult> spoonacularCaller(String[] ingredients) throws Exception {
        HttpResponse<JsonNode> response = Unirest
            .post("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/parseIngredients")
            .header("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
            .header("X-RapidAPI-Key", "5e45da3226msheb3791794393e3ep11c6f4jsn43ca4f021773")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .field("ingredientList", String.join("\n", ingredients))
            .field("serving", 2)
            .asJson();

        Gson gson = new Gson();
        ArrayList<IngredientParserResult> resultRows = gson.fromJson(
            response.getBody().toString(),
            new TypeToken<ArrayList<IngredientParserResult>>(){}.getType()
        );
        return resultRows;
    }
}
