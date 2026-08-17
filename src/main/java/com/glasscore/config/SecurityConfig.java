package com.glasscore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/auth"))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/login",
                        "/auth",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/icons/**",
                        "/favicon.ico",
                        "/manifest.json",
                        "/sw.js",
                        "/offline.html"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                .requestMatchers("/planilla/**", "/reportes/planilla/**", "/usuarios/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/auth")
                .failureUrl("/login?error")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex.accessDeniedHandler((request, response, denied) ->
                    response.sendRedirect(request.getContextPath() + "/403")));
        return http.build();
    }
}
