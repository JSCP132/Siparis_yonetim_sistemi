package com.example.siparis_yonetim_sistemi.service;

import com.example.siparis_yonetim_sistemi.dto.SiparisKalemiYanitDto;
import com.example.siparis_yonetim_sistemi.dto.SiparisYanitDto;
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
    public List<SiparisYanitDto> tumSiparisleriGetir() {
        log.info(">>>>> SAYIM BASLIYOR | YONTEM: findAll() <<<<<");
        List<Siparis> siparisler = siparisRepository.findAll();
        List<SiparisYanitDto> sonuc=siparisler.stream().map(this::yanitaDonustur).toList();

        log.info(">>>>> SAYIM BITTI | findAll() | {} siparis <<<<<", siparisler.size());
        return sonuc;
    }

    @Transactional(readOnly = true)
    public List<SiparisYanitDto> tumSiparisleriHizliGetir() {
        log.info(">>>>> SAYIM BASLIYOR | YONTEM: JOIN FETCH <<<<<");
        List<Siparis> siparisler = siparisRepository.tumunuIliskileriyleGetir();
        List<SiparisYanitDto> sonuc=siparisler.stream().map(this::yanitaDonustur).toList();

        log.info(">>>>> SAYIM BITTI | JOIN FETCH | {} siparis <<<<<", siparisler.size());
        return sonuc;
    }

    private SiparisYanitDto yanitaDonustur(Siparis siparis) {
        List<SiparisKalemiYanitDto> kalemler = siparis.getKalemler().stream()
                .map(this::kalemYanitaDonustur)
                .toList();

        BigDecimal toplam = kalemler.stream()
                .map(k -> k.getBirimFiyat().multiply(BigDecimal.valueOf(k.getAdet())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SiparisYanitDto dto = new SiparisYanitDto();
        dto.setId(siparis.getId());
        dto.setTarih(siparis.getTarih());
        dto.setKullaniciAdi(siparis.getKullanici().getAd());
        dto.setKalemler(kalemler);
        dto.setToplamTutar(toplam);
        return dto;
    }

    private SiparisKalemiYanitDto kalemYanitaDonustur(SiparisKalemi kalem) {
        SiparisKalemiYanitDto dto = new SiparisKalemiYanitDto();
        dto.setUrunAdi(kalem.getUrun().getAd());
        dto.setAdet(kalem.getAdet());
        dto.setBirimFiyat(kalem.getBirimFiyat());
        return dto;
    }
}
