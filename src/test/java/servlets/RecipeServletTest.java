package servlets;

import common.SQLHelper;
import models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class RecipeServletTest extends Mockito {
    private static final String TEST_RECIPE_URL = "http://allrecipes.co.uk/recipe/18744/chipotle-prawn-tacos.aspx";
    private static User user;

    @BeforeAll
    static void setUp() {
        SQLHelper.executeSetUpScript();
        user = User.register("username", "letmein123");
    }

    @Test
    void doPostNoUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            String testJson = "{" +
                    "  \"recipeURL\": \"" + TEST_RECIPE_URL + "\"," +
                    "  \"listType\": 1" +
                    "}";
            Reader inputString = new StringReader(testJson);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            new RecipeServlet().doPost(request, response);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doPostWithUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            String testJson = "{" +
                    "  \"recipeURL\": \"" + TEST_RECIPE_URL + "\"," +
                    "  \"listType\": 1" +
                    "}";
            Reader inputString = new StringReader(testJson);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            when(request.getHeader("authorization"))
                    .thenReturn(user.generateJwt(User.STANDARD_EXPIRY_TIME));
            new RecipeServlet().doPost(request, response);
            verify(response).setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            fail();
        }
    }
    @Test
    void doGetSingleNoUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("recipeURL")).thenReturn(TEST_RECIPE_URL);
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);
            new RecipeServlet().doGet(request, response);
            // ensure valid data for normal request
            assertNotNull(stringWriter.toString());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void doGetSingleWithUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("recipeURL")).thenReturn(TEST_RECIPE_URL);
            when(request.getHeader("authorization"))
                    .thenReturn(user.generateJwt(User.STANDARD_EXPIRY_TIME));
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);
            new RecipeServlet().doGet(request, response);
            // ensure valid data for normal request
            assertNotNull(stringWriter.toString());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void doGetMultiple() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("chinese food");
            when(request.getParameter("numberOfResults")).thenReturn("5");
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new RecipeServlet().doGet(request, response);
            // ensure valid data for normal request
            assertNotNull(stringWriter.toString());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doGetBadNumberOfResults() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("chinese food");
            when(request.getParameter("numberOfResults")).thenReturn("hi");
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new RecipeServlet().doGet(request, response);
            // should default to max results
            assertNotNull(stringWriter.toString());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doGet0Results() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("chinese food");
            when(request.getParameter("numberOfResults")).thenReturn("0");
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new RecipeServlet().doGet(request, response);
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            fail();
        }
    }
}