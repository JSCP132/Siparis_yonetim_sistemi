package com.example.siparis_yonetim_sistemi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "kullanicilar")
@Getter
@Setter
@NoArgsConstructor
public class Kullanici {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String ad;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false,length = 60)
    private String sifre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol; // Tek alan. Tip "Rol" (USER ya da ADMIN olabilir), alanın adı "rol"; değeri constructor'da verilir.

    @OneToMany(mappedBy = "kullanici")
    private List<Siparis> siparisler = new ArrayList<>();

    // id parametre değil: IDENTITY ile veritabanı verir. Eski (ad, email) constructor'ı silindi,
    // artık şifresi ve rolü olmayan kullanıcı oluşturulamaz (JPA için @NoArgsConstructor yeter).
    public Kullanici(String ad, String email, String sifre, Rol rol) {
        this.ad = ad;
        this.email = email;
        this.sifre = sifre;
        this.rol = rol;
    }
}
