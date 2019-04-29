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

class LoginServletTest extends Mockito {
    private static final String EXISTING_USERNAME = "existingUsername";
    private static final String USER_PASSWORD = "letmein123";

    @BeforeAll
    static void setUp() {
        SQLHelper.executeSetUpScript();
        User.register(EXISTING_USERNAME, USER_PASSWORD);
    }

    @Test
    void loginSuccessful() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            String testJson = "{" +
                "\"username\": \"" + EXISTING_USERNAME + "\"," +
                "\"password\": \"" + USER_PASSWORD + "\"" +
                "}";
            Reader inputString = new StringReader(testJson);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);
            new LoginServlet().doPost(request, response);
            verify(response).setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void loginMissingFields() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            String testJson = "{}";
            Reader inputString = new StringReader(testJson);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            new LoginServlet().doPost(request, response);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void loginNoMatchingAccount() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            String testJson = "{" +
                "\"username\": \"invalidusername\"," +
                "\"password\": \"" + USER_PASSWORD + "\"" +
                "}";
            Reader inputString = new StringReader(testJson);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            new LoginServlet().doPost(request, response);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        catch (Exception e) {
            fail();
        }
    }
}
