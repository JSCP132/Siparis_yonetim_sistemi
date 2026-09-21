package com.example.siparis_yonetim_sistemi.repository;

import com.example.siparis_yonetim_sistemi.model.Siparis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiparisRepository extends JpaRepository<Siparis, Long> {

    // Asama 4'un ikinci yarisinda JOIN FETCH'li sorgu buraya gelecek.
}
