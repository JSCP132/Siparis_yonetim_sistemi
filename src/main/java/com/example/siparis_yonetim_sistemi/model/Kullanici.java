package com.example.siparis_yonetim_sistemi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// Bu dosya hazir. Takilinca ornek olarak buraya bak.

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

    // ORNEK: iliskinin ayna tarafi. "kullanici" = Siparis sinifindaki alanin adi.
    @OneToMany(mappedBy = "kullanici")
    @JsonIgnore
    private List<Siparis> siparisler = new ArrayList<>();

    public Kullanici(String ad, String email) {
        this.ad = ad;
        this.email = email;
    }
}
