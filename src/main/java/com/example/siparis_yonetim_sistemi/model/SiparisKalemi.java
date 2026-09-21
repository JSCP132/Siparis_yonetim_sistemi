package com.example.siparis_yonetim_sistemi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "siparis_kalemleri")
@Getter
@Setter
@NoArgsConstructor
public class SiparisKalemi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer adet;

    @Column(precision = 10, scale = 2)
    private BigDecimal birimFiyat;

    @ManyToOne
    @JoinColumn(name = "siparis_id")
    @JsonIgnore
    private Siparis siparis;

    @ManyToOne
    @JoinColumn(name = "urun_id")
    private Urun urun;

    public SiparisKalemi(Urun urun, Integer adet, BigDecimal birimFiyat) {
        this.urun = urun;
        this.adet = adet;
        this.birimFiyat = birimFiyat;
    }
}
