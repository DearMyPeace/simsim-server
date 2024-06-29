package com.project.simsim_server.config.auth;

import com.project.simsim_server.config.auth.jwt.JwtAuthorizationFilter;
import com.project.simsim_server.config.auth.jwt.JwtUtils;
import com.project.simsim_server.domain.user.Role;
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

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private JwtUtils jwtUtils;

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
        http.cors(AbstractHttpConfigurer::disable);
        http.rememberMe(AbstractHttpConfigurer::disable);

        http
                .headers(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/", "/home", "/signup", "/api/v1/auth/google", "/swagger-ui/**",
                                "/index.html", "/favicon", "/v3/api-docs/**", "/api/v1/user/**", "/api/v1/persona/**",
                                "/api/v1/**","/form", "index.html").permitAll()
                        .requestMatchers("/admin").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST,"/notice").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH,"/notice/{noticeId}").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE,"/notice/{noticeId}").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

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
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("Authorization", "refresh", "Content-type",
                "Origin", "Accept", "Access-Control-Allow-Origin",
                "Access-Control-Allow-Headers", "Access-Control-Allow-Methods"));
        configuration.setExposedHeaders(List.of("Authorization", "refresh"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

