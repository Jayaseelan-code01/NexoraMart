package com.jayaseelan.nexoramart.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Adds conservative browser security headers without changing application routing. */
public final class SecurityHeadersFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse http = (HttpServletResponse) response;
            http.setHeader("X-Content-Type-Options", "nosniff");
            http.setHeader("X-Frame-Options", "SAMEORIGIN");
            http.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            http.setHeader("Permissions-Policy", "camera=(self), microphone=(self), geolocation=()");
            http.setHeader("Content-Security-Policy", "default-src 'self'; img-src 'self' data: blob:; style-src 'self' 'unsafe-inline'; script-src 'self' 'unsafe-inline'; connect-src 'self'; font-src 'self' data:; frame-ancestors 'self'; base-uri 'self'; form-action 'self'");
        }
        chain.doFilter(request, response);
    }
}
