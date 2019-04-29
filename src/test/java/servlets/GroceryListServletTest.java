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
import static org.mockito.Mockito.*;

class GroceryListServletTest {
    private static User user;

    @BeforeAll
    static void setUp() {
        SQLHelper.executeSetUpScript();
        user = User.register("username", "letmein123");
    }

    @Test
    void doGetNoUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);

            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new GroceryListServlet().doGet(request, response);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doGet() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getHeader("authorization")).thenReturn(user
                    .generateJwt(User.STANDARD_EXPIRY_TIME));
            Reader inputString = new StringReader("");
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);
            new GroceryListServlet().doGet(request, response);
            verify(response).setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void doPostNoUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            Reader inputString = new StringReader("");
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new GroceryListServlet().doPost(request, response);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doPost() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getHeader("authorization")).thenReturn(user
                    .generateJwt(User.STANDARD_EXPIRY_TIME));
            Reader inputString = new StringReader("{\"recipeID\": 1}");
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);
            new GroceryListServlet().doPost(request, response);
            verify(response).setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void doPutNoUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            Reader inputString = new StringReader("");
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new GroceryListServlet().doPut(request, response);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doPut() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getHeader("authorization")).thenReturn(user
                    .generateJwt(User.STANDARD_EXPIRY_TIME));
            Reader inputString = new StringReader("");
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);
            new GroceryListServlet().doPut(request, response);
            verify(response).setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void doDeleteNoUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            Reader inputString = new StringReader("");
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new GroceryListServlet().doDelete(request, response);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doDelete() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getHeader("authorization")).thenReturn(user
                    .generateJwt(User.STANDARD_EXPIRY_TIME));
            Reader inputString = new StringReader("{\"recipeID\": 1}");
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);
            new GroceryListServlet().doDelete(request, response);
            verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}