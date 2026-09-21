package com.example.siparis_yonetim_sistemi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "siparisler")
@Getter
@Setter
@NoArgsConstructor
public class Siparis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime tarih;

    // ORNEK: iliskinin sahip tarafi. FK sutunu (kullanici_id) bu tabloda duruyor.
    @ManyToOne
    @JoinColumn(name = "kullanici_id")
    private Kullanici kullanici;

    @OneToMany(mappedBy = "siparis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiparisKalemi> kalemler = new ArrayList<>();

    public Siparis(Kullanici kullanici) {
        this.kullanici = kullanici;
        this.tarih = LocalDateTime.now();
    }

    public void kalemEkle(SiparisKalemi kalem) {
        kalemler.add(kalem);
        // this = metodun uzerinde calistigi Siparis nesnesi.
        // siparis.kalemEkle(kalem) dendiginde "this" iste o siparistir.
        kalem.setSiparis(this);
    }
}
