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

    @Transactional(readOnly = true)
    public List<Siparis> tumSiparisleriGetir() {
        log.info(">>>>> SAYIM BASLIYOR | YONTEM: findAll() <<<<<");
        List<Siparis> siparisler = siparisRepository.findAll();
        ozetle(siparisler);
        log.info(">>>>> SAYIM BITTI | findAll() | {} siparis <<<<<", siparisler.size());
        return siparisler;
    }

    @Transactional(readOnly = true)
    public List<Siparis> tumSiparisleriHizliGetir() {
        log.info(">>>>> SAYIM BASLIYOR | YONTEM: JOIN FETCH <<<<<");
        List<Siparis> siparisler = siparisRepository.tumunuIliskileriyleGetir();
        ozetle(siparisler);
        log.info(">>>>> SAYIM BITTI | JOIN FETCH | {} siparis <<<<<", siparisler.size());
        return siparisler;
    }

    private void ozetle(List<Siparis> siparisler) {
        for (Siparis siparis : siparisler) {
            BigDecimal toplam = BigDecimal.ZERO;
            for (SiparisKalemi kalem : siparis.getKalemler()) {
                toplam = toplam.add(kalem.getBirimFiyat().multiply(BigDecimal.valueOf(kalem.getAdet())));
            }
            log.info("Siparis #{} | musteri: {} | {} kalem | toplam: {} TL",
                    siparis.getId(), siparis.getKullanici().getAd(),
                    siparis.getKalemler().size(), toplam);
        }
    }
}
