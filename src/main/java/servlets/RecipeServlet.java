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

@WebServlet("/api/recipe/")
public class RecipeServlet extends HttpServlet {
    /**
     * Changes the saved list a particular recipe is on
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ChangeRecipeListRequest body = new Gson().fromJson(request.getReader(), ChangeRecipeListRequest.class);
        ListType listType = ListType.values()[body.listType];
        Recipe recipe = Recipe.fromRecipeURL(body.recipeURL);
        User user = ServletHelper.readUserFromRequest(request);
        if (user != null) {
            ListManagement.changeRecipeListType(user.getUserID(), recipe, listType);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    /**
     * Provides recipe results to the front end
     *
     * Pass in parameter recipeURL to get a single item
     * Pass in parameters {query, numberOfResults} to get a list of results
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String recipeURL = request.getParameter("recipeURL");
        String searchQuery = request.getParameter("query");

        response.setContentType("application/json");
        if (recipeURL != null && !recipeURL.isBlank() && !recipeURL.isEmpty()) {
            getSingle(request, response, recipeURL);
        } else {
            int numberOfResults;
            try {
                numberOfResults = Integer.parseInt(request.getParameter("numberOfResults"));
            } catch (Exception e) {
                numberOfResults = Constants.DEFAULT_NUMBER_OF_RESULTS;
            }
            getMultiple(response, searchQuery, numberOfResults);
        }
    }

    /**
     * Gets a Recipe by recipeURL and returns as single JSON item
     *
     * @param request to read user from
     * @param response Writes a JSON representation of a recipe to the response
     * @param recipeURL Recipe URL to look up
     * @throws IOException in case cannot get print writer
     */
    private void getSingle(
        HttpServletRequest request,
        HttpServletResponse response,
        String recipeURL
    ) throws IOException {
        Recipe recipe = Recipe.fromRecipeURL(recipeURL);
        User user = ServletHelper.readUserFromRequest(request);
        if (user != null) {
            // If user is logged in, save item for persistence
            ListManagement.insertRecipe(user.getUserID(), recipe);
            recipe.setRecipeID(
                ListManagement.containsRecipe(user.getUserID(), recipeURL)
            );
        }
        // Regardless of user state, allow for browsing
        Gson gson = new Gson();
        response.getWriter().print(gson.toJson(recipe));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Gets recipe(s) of the specified numberOfResults for the query and returns as a list
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
        int numberOfResults
    ) throws IOException {
        if (numberOfResults < 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            List<Recipe> searchResults = Recipe.search(query, numberOfResults);
            Gson gson = new Gson();
            response.getWriter().print(gson.toJson(searchResults));
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
