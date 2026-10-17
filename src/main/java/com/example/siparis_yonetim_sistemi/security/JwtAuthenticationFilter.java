package com.example.siparis_yonetim_sistemi.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Kapıdaki görevli: her istekte Authorization header'ındaki token'a bakar.
// OncePerRequestFilter: aynı istekte iki kez çalışmayacağını garanti eden Spring sınıfı.
// @Component bilerek yok — SecurityConfig'e bağlarken nedenini konuşacağız.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_ONEKI = "Bearer ";

    private  final  JwtUtil jwtUtil;
    // KullaniciDetayService değil, onun uyguladığı arayüz: filter "kullanıcıyı kim, nereden yüklüyor" bilmek zorunda değil.
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith(BEARER_ONEKI)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(BEARER_ONEKI.length());
        String email;
        UserDetails userDetails;
        try {
            email = jwtUtil.emailCikar(token);
             userDetails = userDetailsService.loadUserByUsername(email);

        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }



        UsernamePasswordAuthenticationToken kart = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());


        SecurityContextHolder.getContext().setAuthentication(kart);


        filterChain.doFilter(request, response);
    }
}
