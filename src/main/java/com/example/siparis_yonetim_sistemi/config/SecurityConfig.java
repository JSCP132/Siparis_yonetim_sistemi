package com.example.siparis_yonetim_sistemi.config;

import com.example.siparis_yonetim_sistemi.security.JwtAuthenticationFilter;
import com.example.siparis_yonetim_sistemi.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, // Spring tarafından otomatik verilir
                                                   JwtUtil jwtUtil, // Spring, JwtUtil bean'ini buraya enjekte eder
                                                   UserDetailsService userDetailsService) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/urunler/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        // Basic şimdilik kalıyor: giriş endpoint'i yazılıp JWT uçtan uca çalışınca kaldıracağız.

        JwtAuthenticationFilter jwtFiltre = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        http.addFilterBefore(jwtFiltre, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
