package is.hi.basketmob.config;

import is.hi.basketmob.security.AuthTokenFilter;
import is.hi.basketmob.security.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final AuthTokenFilter authTokenFilter;
    private final RateLimitFilter rateLimitFilter;

    public SecurityConfig(AuthTokenFilter authTokenFilter,
                          RateLimitFilter rateLimitFilter) {
        this.authTokenFilter = authTokenFilter;
        this.rateLimitFilter = rateLimitFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(registry -> registry
                        .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .antMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/users",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/",
                                "/index",
                                "/games",
                                "/standings",
                                "/login",
                                "/register",
                                "/favorites",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()
                        .antMatchers(HttpMethod.GET, "/api/v1/games/**", "/api/v1/standings/**", "/api/v1/leagues/**", "/api/v1/search/**").permitAll()
                        .antMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitFilter, AuthTokenFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
