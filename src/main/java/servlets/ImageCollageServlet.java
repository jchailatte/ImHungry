package servlets;

import com.google.gson.Gson;
import models.ImageCollage;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

// Endpoint for accessing image collage data for frontend
@WebServlet("/api/image-collage/")
public class ImageCollageServlet extends HttpServlet {
    // Provides front end array of image collage URLs given a search query
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String searchQuery = request.getParameter("query");
        ArrayList<String> collage = ImageCollage.search(searchQuery);
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().print(gson.toJson(collage));
    }
}
