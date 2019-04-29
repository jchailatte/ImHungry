package servlets;

import common.SQLHelper;
import models.ListManagement;
import models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;


class SearchHistoryServletTest extends Mockito {
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
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);

            new SearchHistoryServlet().doGet(request, response);
            verify(response).setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void doGetWithUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);

            when(request.getHeader("authorization"))
                    .thenReturn(user.generateJwt(User.STANDARD_EXPIRY_TIME));
            new SearchHistoryServlet().doGet(request, response);
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
            String testJson = "{\"query\": \"tacos\"}";
            Reader inputString = new StringReader(testJson);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            new SearchHistoryServlet().doPost(request, response);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void doPostWithUser() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            String testJson = "{\"query\": \"tacos\"}";
            Reader inputString = new StringReader(testJson);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            when(request.getHeader("authorization"))
                    .thenReturn(user.generateJwt(User.STANDARD_EXPIRY_TIME));

            int resultsBefore = ListManagement.getAllSearchedItems(user.getUserID()).size();
            new SearchHistoryServlet().doPost(request, response);
            verify(response).setStatus(HttpServletResponse.SC_OK);
            int resultsAfter = ListManagement.getAllSearchedItems(user.getUserID()).size();

            assertEquals(resultsBefore + 1, resultsAfter);
        }
        catch (Exception e) {
            fail();
        }
    }
}
