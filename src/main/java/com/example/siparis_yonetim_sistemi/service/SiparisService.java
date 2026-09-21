package com.example.siparis_yonetim_sistemi.service;

import com.example.siparis_yonetim_sistemi.model.Siparis;
import com.example.siparis_yonetim_sistemi.model.SiparisKalemi;
import com.example.siparis_yonetim_sistemi.repository.SiparisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SiparisService {

    private static final Logger log = LoggerFactory.getLogger(SiparisService.class);

    private final SiparisRepository siparisRepository;

    public SiparisService(SiparisRepository siparisRepository) {
        this.siparisRepository = siparisRepository;
    }

    // @Transactional(readOnly = true): metot boyunca tek bir veritabani oturumu acik kalir.
    // Lazy iliskilere dokunabilmek icin gerekli, yoksa LazyInitializationException alirsin.
    @Transactional(readOnly = true)
    public List<Siparis> tumSiparisleriGetir() {
        log.info(">>>>> SORGU SAYIMI BASLIYOR <<<<<");

        List<Siparis> siparisler = siparisRepository.findAll();

        // Gercek bir is kurali: her siparisin toplam tutarini hesapla.
        // Bunu yapmak icin kalemlere DOKUNMAK zorundayiz.
        for (Siparis siparis : siparisler) {
            BigDecimal toplam = BigDecimal.ZERO;
            for (SiparisKalemi kalem : siparis.getKalemler()) {
                toplam = toplam.add(kalem.getBirimFiyat().multiply(BigDecimal.valueOf(kalem.getAdet())));
            }
            log.info("Siparis #{} | musteri: {} | {} kalem | toplam: {} TL",
                    siparis.getId(), siparis.getKullanici().getAd(),
                    siparis.getKalemler().size(), toplam);
        }

        log.info(">>>>> SORGU SAYIMI BITTI | {} siparis islendi <<<<<", siparisler.size());
        return siparisler;
    }
}
