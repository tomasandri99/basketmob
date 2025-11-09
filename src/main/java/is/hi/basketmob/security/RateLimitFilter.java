package is.hi.basketmob.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final boolean enabled;
    private final int limit;
    private final Duration window;

    private final Map<String, Window> buckets = new ConcurrentHashMap<>();

    public RateLimitFilter(
            @Value("${basketmob.rate-limit.enabled:true}") boolean enabled,
            @Value("${basketmob.rate-limit.limit:120}") int limit,
            @Value("${basketmob.rate-limit.window:PT1M}") Duration window) {
        this.enabled = enabled;
        this.limit = Math.max(1, limit);
        this.window = window == null || window.isZero() || window.isNegative()
                ? Duration.ofMinutes(1)
                : window;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String key = request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
        Window window = buckets.computeIfAbsent(key, k -> new Window());
        if (window.incrementAndCheckLimit()) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit exceeded");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private final class Window {
        private final AtomicInteger counter = new AtomicInteger();
        private volatile long windowStart = System.nanoTime();

        synchronized boolean incrementAndCheckLimit() {
            long now = System.nanoTime();
            if (now - windowStart > window.toNanos()) {
                windowStart = now;
                counter.set(0);
            }
            return counter.incrementAndGet() > limit;
        }
    }
}
