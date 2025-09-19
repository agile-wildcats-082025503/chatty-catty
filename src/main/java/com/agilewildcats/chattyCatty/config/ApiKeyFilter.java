package com.agilewildcats.chattyCatty.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class ApiKeyFilter implements Filter {

    private final Environment env;
    // endpoints that require admin/auth header — change as needed
    private static final Set<String> PROTECTED_PREFIXES = Set.of(
            "/docs/upload", "/docs/uploadFile", "/docs/uploadFiles", "/docs/delete",
            "/admin"
    );

    public ApiKeyFilter(Environment env) {
        this.env = env;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI();

        boolean requiresAuth = PROTECTED_PREFIXES.stream().anyMatch(path::startsWith);

        if (!requiresAuth) {
            chain.doFilter(request, response);
            return;
        }

        String header = req.getHeader("X-API-KEY");
        String adminKey = env.getProperty("ADMIN_API_KEY");

        if (adminKey == null || adminKey.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server: ADMIN_API_KEY not configured");
            return;
        }

        if (header == null || !header.equals(adminKey)) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing API key");
            return;
        }

        chain.doFilter(request, response);
    }
}
