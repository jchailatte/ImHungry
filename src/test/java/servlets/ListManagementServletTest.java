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


class ListManagementServletTest extends Mockito {
    private static User user;

    @BeforeAll
    static void setUp() {
        SQLHelper.executeSetUpScript();
        user = User.register("username", "letmein123");
    }

    @Test
    void doGetRestaurantNoUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("restaurant");
            when(request.getParameter("listType")).thenReturn("0");
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new ListManagementServlet().doGet(request, response);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doGetRestaurantWithUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("restaurant");
            when(request.getParameter("listType")).thenReturn("0");
            when(request.getHeader("authorization"))
                    .thenReturn(user.generateJwt(User.STANDARD_EXPIRY_TIME));
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);
            new ListManagementServlet().doGet(request, response);
            verify(response).setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void doGetRecipeNoUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("recipe");
            when(request.getParameter("listType")).thenReturn("0");
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new ListManagementServlet().doGet(request, response);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doGetRecipeWithUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("recipe");
            when(request.getParameter("listType")).thenReturn("0");
            when(request.getHeader("authorization"))
                    .thenReturn(user.generateJwt(User.STANDARD_EXPIRY_TIME));
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new ListManagementServlet().doGet(request, response);
            verify(response).setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doGetBadType() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("restaurant");
            when(request.getParameter("listType")).thenReturn("notInt");
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new ListManagementServlet().doGet(request, response);
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doGetBadQuery() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("invalid");
            when(request.getParameter("listType")).thenReturn("1");
            when(request.getHeader("authorization"))
                    .thenReturn(user.generateJwt(User.STANDARD_EXPIRY_TIME));
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new ListManagementServlet().doGet(request, response);
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doPutBadType() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("restaurant");
            when(request.getParameter("listType")).thenReturn("notInt");
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new ListManagementServlet().doPut(request, response);
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doPutBadQuery() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("invalid");
            when(request.getParameter("listType")).thenReturn("1");
            when(request.getHeader("authorization"))
                .thenReturn(user.generateJwt(User.STANDARD_EXPIRY_TIME));
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new ListManagementServlet().doPut(request, response);
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doPutNoUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("recipe");
            when(request.getParameter("listType")).thenReturn("0");
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new ListManagementServlet().doPut(request, response);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doPutRestaurant() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("restaurant");
            when(request.getParameter("listType")).thenReturn("0");
            when(request.getHeader("authorization"))
                .thenReturn(user.generateJwt(User.STANDARD_EXPIRY_TIME));
            String testJson = "{\"body\": []}";
            Reader inputString = new StringReader(testJson);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            new ListManagementServlet().doPut(request, response);
            verify(response).setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void doPutRecipe() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("recipe");
            when(request.getParameter("listType")).thenReturn("0");
            when(request.getHeader("authorization"))
                .thenReturn(user.generateJwt(User.STANDARD_EXPIRY_TIME));
            String testJson = "{\"body\": []}";
            Reader inputString = new StringReader(testJson);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            new ListManagementServlet().doPut(request, response);
            verify(response).setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            fail();
        }
    }
}