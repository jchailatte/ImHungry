package servlets;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;


class ImageCollageServletTest extends Mockito {
    @Test
    void doGet() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(request.getParameter("query")).thenReturn("chinese food");
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            new ImageCollageServlet().doGet(request, response);
            assertNotNull(stringWriter.toString());
        } catch (Exception e) {
            fail();
        }
    }
}