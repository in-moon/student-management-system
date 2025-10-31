package com.example.studentsystem.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        if (isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Claims claims = jwtUtil.parseToken(token);
                request.setAttribute("jwtClaims", claims);
                filterChain.doFilter(request, response);
                return;
            } catch (Exception e) {
                unauthorized(response, "Invalid token");
                return;
            }
        }

        unauthorized(response, "Missing token");
    }

    private boolean isWhitelisted(String path) {
        return matcher.match("/api/auth/login", path)
                || matcher.match("/v3/api-docs/**", path)
                || matcher.match("/swagger-ui.html", path)
                || matcher.match("/swagger-ui/**", path)
                || matcher.match("/error", path);
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(('{'+"\"message\":\""+message+"\""+'}').getBytes(StandardCharsets.UTF_8));
    }
}


