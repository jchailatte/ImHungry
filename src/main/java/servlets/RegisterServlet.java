package servlets;

import com.google.gson.Gson;
import models.JwtResponse;
import models.LoginRegisterRequest;
import models.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/register/")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        LoginRegisterRequest body = gson.fromJson(request.getReader(), LoginRegisterRequest.class);
        User user = User.register(body.username, body.password);
        response.setContentType("application/json");

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.token = user.generateJwt(User.STANDARD_EXPIRY_TIME);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(gson.toJson(jwtResponse));
        }
    }
}
