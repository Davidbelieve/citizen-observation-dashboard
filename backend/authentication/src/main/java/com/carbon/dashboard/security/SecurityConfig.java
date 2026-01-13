package com.carbon.dashboard.security;

import com.carbon.dashboard.config.CorsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for the application.
 * Configures authentication, authorization, JWT filter, and password encryption.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CorsConfig corsConfig;
    
    /**
     * Constructor with @Lazy to break circular dependency.
     */
    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthFilter, CorsConfig corsConfig) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.corsConfig = corsConfig;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf(csrf -> csrf.disable())
            // Configure CORS - must be before authorization
            .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication required
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                // Gateway routes - require authentication
                .requestMatchers("/api/regions/**").authenticated()
                // Protected endpoints - require authentication
                .requestMatchers("/api/carbon/**").authenticated()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            // Stateless session management (JWT-based)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Add JWT filter before the default authentication filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        // Configure HTTP headers
        http.headers(headers -> headers
            .frameOptions(frame -> frame.sameOrigin())
        );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}