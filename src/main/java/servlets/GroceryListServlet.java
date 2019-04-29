package servlets;

import com.google.gson.Gson;
import common.ServletHelper;
import models.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/api/grocery-list/")
public class GroceryListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        User user = ServletHelper.readUserFromRequest(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        ArrayList<GroceryListItem> groceryList = GroceryListManagement.getGroceryListForUser(user.getUserID());
        response.getWriter().print(gson.toJson(groceryList));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GroceryListRequest body = new Gson().fromJson(request.getReader(), GroceryListRequest.class);
        User user = ServletHelper.readUserFromRequest(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        GroceryListManagement.insertToGroceryList(user.getUserID(), body.recipeID);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UpdateGroceryListRequest body = new Gson().fromJson(request.getReader(), UpdateGroceryListRequest.class);
        User user = ServletHelper.readUserFromRequest(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        GroceryListManagement.updateGroceryList(user.getUserID(), body.groceryID, body.checkList);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GroceryListRequest body = new Gson().fromJson(request.getReader(), GroceryListRequest.class);
        User user = ServletHelper.readUserFromRequest(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        GroceryListManagement.removeFromGroceryList(user.getUserID(), body.recipeID);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
