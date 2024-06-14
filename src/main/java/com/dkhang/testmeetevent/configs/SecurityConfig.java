package com.dkhang.testmeetevent.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.dkhang.testmeetevent.filters.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private static final String[] WHITE_LIST_URL = {"/api/v1/users/**",
            "/api/v1/errors/**",
            "/websocket/**",
            "/",
            "/index.html",
            "/main.css", "/js/**", "/css/**", "/img/**", "/favicon.ico/**",
            "/topic/**", "/queue/**", "/user/**", "/app/**",
            "/chat/**","/messages/**",
            "https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.4/sockjs.min.js",
            "https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"

    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(WHITE_LIST_URL).permitAll().anyRequest().authenticated();
//                    auth.requestMatchers(apiPrefix + "/errors/**").permitAll();
//                    auth.requestMatchers(apiPrefix + "/users/login", apiPrefix + "/users/signup",
//                            apiPrefix + "/users/code").permitAll();
//                    auth.requestMatchers("/websocket/**").permitAll();
//                    auth.requestMatchers("/").permitAll();
//                    auth.anyRequest().authenticated();;

                })

                .sessionManagement(s -> {
                    s.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
