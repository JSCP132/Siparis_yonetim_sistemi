package com.example.siparis_yonetim_sistemi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
// @Entity(name = "urunler") TABLO adini degistirmez! Oradaki name, JPQL sorgularinda
// kullanilan ENTITY adidir ("SELECT u FROM urunler u"). Tablo adi icin @Table kullanilir.
@Table(name = "urunler")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Urun {

    @Id
    // IDENTITY = id'yi veritabani uretir (auto-increment sutun).
    // Bu satir olmadan Hibernate id'yi SENDEN bekler, POST'ta null id ile hata alirdin.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable = false -> sutun NOT NULL, length = 100 -> VARCHAR(100)
    @Column(nullable = false, length = 100)
    private String ad;

    // precision = toplam hane sayisi, scale = virgulden sonraki hane -> DECIMAL(10,2)
    @Column(precision = 10, scale = 2)
    private BigDecimal fiyat;

    private Integer stok;
}
