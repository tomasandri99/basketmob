package is.hi.basketmob.security;

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

    private static final int LIMIT = 120;
    private static final Duration WINDOW = Duration.ofMinutes(1);
    private final Map<String, Window> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
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

    private static final class Window {
        private final AtomicInteger counter = new AtomicInteger();
        private volatile long windowStart = System.nanoTime();

        synchronized boolean incrementAndCheckLimit() {
            long now = System.nanoTime();
            if (now - windowStart > WINDOW.toNanos()) {
                windowStart = now;
                counter.set(0);
            }
            return counter.incrementAndGet() > LIMIT;
        }
    }
}
