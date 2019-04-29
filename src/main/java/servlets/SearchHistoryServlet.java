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

@WebServlet("/api/search-history/")
public class SearchHistoryServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = ServletHelper.readUserFromRequest(request);
        ArrayList<String> searchHistory;
        if (user != null) {
            searchHistory = ListManagement.getAllSearchedItems(user.getUserID());
        } else {
            searchHistory = new ArrayList<>();
        }

        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        if (searchHistory.size() > 0) {
            response.getWriter().print(gson.toJson(searchHistory, new TypeToken<ArrayList<String>>(){}.getType()));
        }
        else {
            response.getWriter().print("[]");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        User user = ServletHelper.readUserFromRequest(request);
        if (user != null) {
            SearchHistoryRequest body = gson.fromJson(request.getReader(), SearchHistoryRequest.class);
            ListManagement.insertSearchTerm(user.getUserID(), body.query);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
