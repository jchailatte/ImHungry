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

class RegisterServletTest extends Mockito {
    private static final String EXISTING_USERNAME = "existingUsername";
    private static final String USER_PASSWORD = "letmein123";
    private static final String NEW_USERNAME = "newUsername";
    private static final String NEW_USERNAME_2 = "newUsername2";

    @BeforeAll
    static void setUp() {
        SQLHelper.executeSetUpScript();
        User.register(EXISTING_USERNAME, USER_PASSWORD);
    }

    @Test
    void registrationSuccessful() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            String testJson = "{" +
                "\"username\": \"" + NEW_USERNAME + "\"," +
                "\"password\": \"" + USER_PASSWORD + "\"" +
                "}";
            Reader inputString = new StringReader(testJson);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);
            new RegisterServlet().doPost(request, response);
            verify(response).setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void registerMissingFields() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            String testJson = "{}";
            Reader inputString = new StringReader(testJson);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            new RegisterServlet().doPost(request, response);
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void registerExistingUsername() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            String testJson = "{" +
                "\"username\": \"" + EXISTING_USERNAME +"\"," +
                "\"password\": \"" + USER_PASSWORD + "\"" +
                "}";
            Reader inputString = new StringReader(testJson);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            new RegisterServlet().doPost(request, response);
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void registerWeakPassword() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            String testJson = "{" +
                "\"username\": \"" + NEW_USERNAME_2 +"\"," +
                "\"password\": \"" + "hi" + "\"" +
                "}";
            Reader inputString = new StringReader(testJson);
            BufferedReader bufferedReader = new BufferedReader(inputString);
            when(request.getReader()).thenReturn(bufferedReader);
            new RegisterServlet().doPost(request, response);
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (Exception e) {
            fail();
        }
    }
}
