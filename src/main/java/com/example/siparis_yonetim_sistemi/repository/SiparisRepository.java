package com.example.siparis_yonetim_sistemi.repository;

import com.example.siparis_yonetim_sistemi.model.Siparis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SiparisRepository extends JpaRepository<Siparis, Long> {
    @Query("SELECT DISTINCT s FROM Siparis s " +
            "LEFT JOIN FETCH s.kullanici " +
            "LEFT JOIN FETCH s.kalemler k " +
            " LEFT JOIN FETCH k.urun")
    List<Siparis> tumunuIliskileriyleGetir();
}
