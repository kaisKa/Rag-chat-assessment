package com.kais.rag_chat.config;

import tools.jackson.databind.ObjectMapper;
import com.kais.rag_chat.dto.response.ErrorResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LogManager.getLogger(RateLimitFilter.class);
    private static final String API_KEY_HEADER = "X-API-Key";

    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = resolveKey(request);
        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            sendRateLimitResponse(response, request);
        }
    }

    private String resolveKey(HttpServletRequest request) {
        String apiKey = request.getHeader(API_KEY_HEADER);
        return (apiKey != null && !apiKey.isBlank()) ? apiKey : request.getRemoteAddr();
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(properties.getCapacity())
                .refillGreedy(properties.getRefillTokens(), Duration.ofMinutes(properties.getRefillMinutes()))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private void sendRateLimitResponse(HttpServletResponse response, HttpServletRequest request) throws IOException {
        log.warn("Rate limit exceeded for key={} on {} {}", resolveKey(request), request.getMethod(), request.getRequestURI());
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error("Too Many Requests")
                .message("Rate limit exceeded. Please slow down your requests.")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        objectMapper.writeValue(response.getWriter(), body);
    }
}