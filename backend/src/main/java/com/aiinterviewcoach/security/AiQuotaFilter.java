package com.aiinterviewcoach.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/** Fixed-window quota for a single backend instance, shared across a user's JWTs. */
public final class AiQuotaFilter extends OncePerRequestFilter {
    private record Usage(long expires, int requests) {}
    private final Map<String, Usage> usage = new HashMap<>();
    private final int hourlyLimit;
    public AiQuotaFilter(int hourlyLimit) {
        if (hourlyLimit < 1) throw new IllegalArgumentException("AI hourly limit must be positive");
        this.hourlyLimit = hourlyLimit;
    }
    private synchronized long reserve(String user) {
        long now = System.currentTimeMillis();
        usage.entrySet().removeIf(entry -> entry.getValue().expires() <= now);
        var previous = usage.get(user);
        if (previous == null && usage.size() >= 10000) return 60;
        if (previous != null && previous.requests() >= hourlyLimit)
            return Math.max(1, (previous.expires() - now + 999) / 1000);
        usage.put(user, previous == null ? new Usage(now + 3600000, 1)
            : new Usage(previous.expires(), previous.requests() + 1));
        return 0;
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String path = request.getServletPath();
        boolean aiRequest = (request.getMethod().equals("POST") &&
            (path.endsWith("/parse") || path.equals("/api/question-banks/generate") ||
             path.equals("/api/interviews/start") || path.matches("/api/interviews/[^/]+/answer"))) ||
            (request.getMethod().equals("PUT") && path.matches("/api/questions/[^/]+/feedback"));
        if (aiRequest && auth != null && auth.isAuthenticated()) {
            long retry = reserve(auth.getName());
            if (retry > 0) {
                response.setStatus(429);
                response.setHeader("Retry-After", Long.toString(retry));
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"AI request quota reached. Please retry later.\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
