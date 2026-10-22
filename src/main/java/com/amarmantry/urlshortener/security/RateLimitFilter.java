package com.amarmantry.urlshortener.security;

import com.amarmantry.urlshortener.dto.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int AUTH_LIMIT = 10;
    private static final int GENERAL_LIMIT = 100;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        boolean isAuth = request.getRequestURI().startsWith("/api/auth/");
        int limit = isAuth ? AUTH_LIMIT : GENERAL_LIMIT;
        String key = "rl:" + (isAuth ? "auth:" : "general:") + request.getRemoteAddr();

        boolean limited = false;
        long retryAfterSeconds = WINDOW.toSeconds();

        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1) {
                redisTemplate.expire(key, WINDOW);
            }
            if (count != null && count > limit) {
                limited = true;
                Long ttl = redisTemplate.getExpire(key);
                if (ttl != null && ttl > 0) {
                    retryAfterSeconds = ttl;
                }
            }
        } catch (DataAccessException e) {
            // Redis is down: fail open, so a cache outage doesn't take the API down with it
        }

        if (limited) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(
                    ErrorResponse.builder()
                            .status(429)
                            .message("Too many requests. Try again in " + retryAfterSeconds + " seconds.")
                            .timeStamp(LocalDateTime.now())
                            .build()));
            return;
        }

        filterChain.doFilter(request, response);
    }
}