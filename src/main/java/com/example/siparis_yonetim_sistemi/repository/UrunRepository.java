package com.example.siparis_yonetim_sistemi.repository;

import com.example.siparis_yonetim_sistemi.model.Urun;
import org.springframework.data.jpa.repository.JpaRepository;

// DIKKAT: "interface UrunRepository<Urun, Long>" YANLISTI.
// Arayuz adindan hemen sonra gelen <> tip argumani VERMEZ, yeni tip degiskeni TANIMLAR.
// Yani oradaki "Urun" senin model sinifin degil, sadece bir harf gibi bos bir yer tutucudur
// ve gercek Urun sinifini golgeler. Tip argumanlari extends'ten SONRA verilir.
//
// Metotlari da tek tek yazmana gerek yok: hepsi JpaRepository'den miras geliyor.
// (@Repository'yi sildim — Spring Data bu bean'i zaten kendisi kaydediyor, gereksizdi.)
public interface UrunRepository extends JpaRepository<Urun, Long> {
}
