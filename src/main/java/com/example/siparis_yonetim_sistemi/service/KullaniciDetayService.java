package com.example.siparis_yonetim_sistemi.service;

import com.example.siparis_yonetim_sistemi.model.Kullanici;
import com.example.siparis_yonetim_sistemi.repository.KullaniciRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class KullaniciDetayService implements UserDetailsService {

    private final KullaniciRepository kullaniciRepository;

    public KullaniciDetayService(KullaniciRepository kullaniciRepository) {
        this.kullaniciRepository = kullaniciRepository;
    }

    // Spring Security, giriş yapan biri olduğunda bu metodu kendisi çağırır.
    // Arayüzde parametrenin adı "username", bizim sistemde kullanıcı adı email.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Kullanici kullanici = kullaniciRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanici bulunamadi: " + email));

        return User.withUsername(kullanici.getEmail())
                .password(kullanici.getSifre())
                .roles(kullanici.getRol().name())
                .build();
    }
}
