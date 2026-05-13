package com.familyteacher.backend.config;

import com.familyteacher.backend.security.JwtAuthenticationFilter;
import com.familyteacher.backend.security.RestAccessDeniedHandler;
import com.familyteacher.backend.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                          RestAccessDeniedHandler restAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/login", "/api/users/register").permitAll()
                .requestMatchers(HttpMethod.GET,
                    "/api/resources/teacher/job-posts",
                    "/api/resources/teacher/job-posts/search",
                    "/api/resources/tutoring-requests",
                    "/api/resources/tutoring-requests/search"
                ).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/transactions/appointment/**", "/api/transactions/order/**").denyAll()
                .requestMatchers("/api/transactions/appointments/student", "/api/transactions/appointments/teacher").denyAll()
                .requestMatchers(HttpMethod.GET, "/api/recommendations/teachers-for-student").hasRole("STUDENT")
                .requestMatchers(HttpMethod.GET, "/api/recommendations/students-for-teacher").hasRole("TEACHER")
                .requestMatchers("/api/chat/**").hasAnyRole("STUDENT", "TEACHER")
                .requestMatchers("/api/favorites/**").hasAnyRole("STUDENT", "TEACHER")
                .requestMatchers("/api/evaluations/**").hasAnyRole("STUDENT", "TEACHER")
                .requestMatchers(HttpMethod.POST, "/api/resources/teacher/job-post").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/resources/teacher/job-post", "/api/resources/teacher/job-post/*/deactivate").hasRole("TEACHER")
                .requestMatchers(HttpMethod.GET, "/api/resources/teacher/my-job-posts").hasRole("TEACHER")
                .requestMatchers(HttpMethod.POST, "/api/resources/tutoring-requests").hasRole("STUDENT")
                .requestMatchers(HttpMethod.PUT, "/api/resources/tutoring-request", "/api/resources/tutoring-request/*/deactivate").hasRole("STUDENT")
                .requestMatchers(HttpMethod.GET, "/api/resources/my-tutoring-requests").hasRole("STUDENT")
                .requestMatchers(HttpMethod.GET, "/api/users/profile").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/users/student/profile").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/users/student/profile").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/student/update").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users/teacher/profile").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/users/teacher/profile").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/teacher/update").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/update").denyAll()
                .requestMatchers("/api/transactions/appointments/**", "/api/transactions/orders/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有来源 (开发环境)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
