package common;

import models.User;

import javax.servlet.http.HttpServletRequest;

public class ServletHelper {
    /**
     * Parses User from JWT in the request's auth header
     *
     * @param request containing header (may be blank)
     * @return User | null
     */
    public static User readUserFromRequest(HttpServletRequest request) {
        String authToken = request.getHeader("authorization");
        return User.fromJwt(authToken);
    }
}
