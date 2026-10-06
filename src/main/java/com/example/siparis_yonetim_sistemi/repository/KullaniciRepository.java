package com.example.siparis_yonetim_sistemi.repository;

import com.example.siparis_yonetim_sistemi.model.Kullanici;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KullaniciRepository extends JpaRepository<Kullanici, Long> {

    Optional<Kullanici> findByEmail(String email);
}
