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
                .requestMatchers("/usuarios/**", "/fiscal/**").hasRole("ADMIN")
                .requestMatchers("/proveedores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/planilla/**").hasRole("ADMIN")
                .requestMatchers("/planilla/**").hasAnyRole("ADMIN", "CONTADOR")
                .requestMatchers("/reportes/**").hasAnyRole("ADMIN", "CONTADOR")
                .requestMatchers("/clientes/**", "/cotizaciones/**").hasAnyRole("ADMIN", "CAJERO")
                .requestMatchers(HttpMethod.POST, "/ventas/**").hasAnyRole("ADMIN", "CAJERO")
                .requestMatchers("/ventas/**").hasAnyRole("ADMIN", "CAJERO", "CONTADOR")
                .requestMatchers("/inventario/**").hasAnyRole("ADMIN", "CAJERO", "OPERADOR")
                .requestMatchers("/api/campo/**").authenticated()
                .requestMatchers("/herramientas/**", "/logistica/**")
                    .hasAnyRole("ADMIN", "OPERADOR")
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
