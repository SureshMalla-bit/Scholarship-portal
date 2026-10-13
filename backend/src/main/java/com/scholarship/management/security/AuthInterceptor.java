package com.scholarship.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Map;
import java.util.Optional;

/**
 * Runs before every /api call. Reads "Authorization: Bearer <token>" and stores the user for the controllers.
 * Login, register and reading scholarships work without a token; everything else returns 401 without one.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokens;
    private final ObjectMapper json;

    public AuthInterceptor(TokenService tokens, ObjectMapper json) {
        this.tokens = tokens;
        this.json = json;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) return true; // CORS preflight

        String header = request.getHeader("Authorization");
        Optional<CurrentUser> user = header != null && header.startsWith("Bearer ")
                ? tokens.read(header.substring(7).trim())
                : Optional.empty();
        if (user.isPresent()) {
            request.setAttribute(CurrentUser.ATTRIBUTE, user.get());
            return true;
        }
        if (isPublic(request)) return true;

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        json.writeValue(response.getOutputStream(), Map.of("message", "Please log in again"));
        return false;
    }

    private boolean isPublic(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.startsWith("/api/auth/")
                || ("GET".equals(request.getMethod()) && path.startsWith("/api/scholarships"));
    }
}
