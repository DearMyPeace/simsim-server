package com.project.simsim_server.config.auth;

import com.project.simsim_server.config.auth.jwt.AuthenticationService;
import com.project.simsim_server.config.auth.jwt.CustomUserDetailsService;
import com.project.simsim_server.filter.JwtAuthenticationFilter;
import com.project.simsim_server.config.auth.jwt.JwtUtils;
import com.project.simsim_server.filter.JwtExceptionFilter;
import com.project.simsim_server.domain.user.Role;
import com.project.simsim_server.config.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RedisService redisService;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationService authenticationService;


    /**
     * Spring Security 제외 (필터를 거치지 않음)
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(String.valueOf(PathRequest.toStaticResources().atCommonLocations()))
                .requestMatchers("/error");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.httpBasic(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.rememberMe(AbstractHttpConfigurer::disable);
        http.anonymous(AbstractHttpConfigurer::disable);
        http.logout(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);

        http
                .headers(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((request) -> request
                        .requestMatchers( "/api/v1/auth/apple", "/api/v1/auth/google", "/api/v1/auth/reissue",
                                "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/admin", "/api/v1/export/**").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST,"/notice").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH,"/notice/{noticeId}").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE,"/notice/{noticeId}").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtils, redisService, customUserDetailsService, authenticationService),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(new JwtExceptionFilter(), jwtAuthenticationFilter.getClass());

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                List.of(
                        "https://accounts.google.com",
                        "https://localhost:8080", "http://localhost",
                        "https://localhost:8081", "https://dear-my-peace.site",
                        "http://localhost:8080", "http://localhost:8081",
                        "http://127.0.0.1:8080", "http://127.0.0.1:8081"
                )
        );
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "refresh", "Content-Type",
                "Origin", "Accept", "Access-Control-Allow-Origin", "x-custom-header",
                "Access-Control-Allow-Headers", "Access-Control-Allow-Methods"));
        configuration.setExposedHeaders(List.of("Authorization", "refresh"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

