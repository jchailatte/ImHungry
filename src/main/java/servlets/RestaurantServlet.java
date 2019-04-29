package servlets;

import com.google.gson.Gson;
import common.Constants;
import common.ServletHelper;
import models.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// Endpoint for accessing Restaurant data from frontend
@WebServlet("/api/restaurant/")
public class RestaurantServlet extends HttpServlet {
    /**
     * Changes the saved list a particular restaurant is on
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ChangeRestaurantListRequest body = new Gson().fromJson(request.getReader(), ChangeRestaurantListRequest.class);
        ListType listType = ListType.values()[body.listType];
        User user = ServletHelper.readUserFromRequest(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        ListManagement.changeRestaurantListType(user.getUserID(), body.placeID, listType);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Provides restaurant results to the front end
     *
     * Pass in parameter placeID to get a single item
     * Pass in parameters {query, numberOfResults} to get a list of results
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String placeID = request.getParameter("placeID");
        String searchQuery = request.getParameter("query");

        response.setContentType("application/json");

        if (placeID != null && !placeID.isBlank() && !placeID.isEmpty()) {
            getSingle(request, response, placeID);
        } else {
            int numberOfResults;
            int radius;
            try {
                numberOfResults = Integer.parseInt(request.getParameter("numberOfResults"));
                radius = (int)Float.parseFloat(request.getParameter("radius"));
            }
            catch (Exception e) {
                numberOfResults = Constants.DEFAULT_NUMBER_OF_RESULTS;
                radius = 40000;
            }
            getMultiple(response, searchQuery, numberOfResults, radius);
        }
    }

    /**
     * Gets a Restaurant by Google Place ID and returns as a single JSON item
     *
     * @param request to read user from
     * @param response Writes a JSON representation of a restaurant to the response
     * @param placeID Google Place ID to look up
     * @throws IOException in case cannot get print writer
     */
    private void getSingle(
        HttpServletRequest request,
        HttpServletResponse response,
        String placeID
    ) throws IOException {
        Restaurant restaurant = RestaurantFactory.fromPlaceID(placeID);
        User user = ServletHelper.readUserFromRequest(request);
        if (user != null) {
            // If user is logged in, save item for list persistence
            ListManagement.insertRestaurant(user.getUserID(), placeID);
        }
        // Regardless of user state, allow for browsing
        Gson gson = new Gson();
        response.getWriter().print(gson.toJson(restaurant));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Gets restaurant(s) of the specified numberOfResults for the query and returns as a list
     * of recipe items as JSON
     *
     * @param response Writes a JSON representation of all recipe results to the response
     * @param query Search query to look up
     * @param numberOfResults Number of results to limit response to
     * @throws IOException in case cannot get print writer
     */
    private void getMultiple(
        HttpServletResponse response,
        String query,
        int numberOfResults,
        int radius
    ) throws IOException {
        if (numberOfResults < 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            List<Restaurant> searchResults = RestaurantFactory.search(query, numberOfResults, radius);
            Gson gson = new Gson();
            response.getWriter().print(gson.toJson(searchResults));
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
