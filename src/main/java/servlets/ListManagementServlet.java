package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import common.ServletHelper;
import models.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

// Endpoint for list management results on frontend
@WebServlet("/api/list-management/")
public class ListManagementServlet extends HttpServlet {
    // Provides frontend list types for queries of Restaurant or Recipe type objects
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String query = request.getParameter("query");
        String listTypeString = request.getParameter("listType");
        int listTypeInt;
        try {
            listTypeInt = Integer.parseInt(listTypeString);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        ListType listType = ListType.values()[listTypeInt];
        Gson gson = new Gson();
        response.setContentType("application/json");

        User user = ServletHelper.readUserFromRequest(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (query.equals("restaurant")) {
            ArrayList<Restaurant> restaurants = ListManagement.getRestaurantSavedLists(user.getUserID(), listType);
            response.getWriter().print(gson.toJson(restaurants));
            response.setStatus(HttpServletResponse.SC_OK);
        } else if (query.equals("recipe")) {
            ArrayList<Recipe> recipes = ListManagement.getRecipeSavedLists(user.getUserID(), listType);
            response.getWriter().print(gson.toJson(recipes));
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String query = request.getParameter("query");
        String listTypeString = request.getParameter("listType");
        int listTypeInt;
        try {
            listTypeInt = Integer.parseInt(listTypeString);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        ListType listType = ListType.values()[listTypeInt];

        User user = ServletHelper.readUserFromRequest(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Gson gson = new Gson();
        if (query.equals("restaurant")) {
            ReorderRequest<RestaurantOrder> body = gson.fromJson(
                request.getReader(),
                new TypeToken<ReorderRequest<RestaurantOrder>>(){}.getType()
            );
            ListManagement.updateListOrderForRestaurants(
                user.getUserID(), listType, body.body
            );
            response.setStatus(HttpServletResponse.SC_OK);
        } else if (query.equals("recipe")) {
            ReorderRequest<RecipeOrder> body = gson.fromJson(
                request.getReader(),
                new TypeToken<ReorderRequest<RecipeOrder>>(){}.getType()
            );
            ListManagement.updateListOrderForRecipes(
                user.getUserID(), listType, body.body
            );
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
