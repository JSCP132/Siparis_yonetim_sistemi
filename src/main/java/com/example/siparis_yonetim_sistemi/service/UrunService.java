package com.example.siparis_yonetim_sistemi.service;

import com.example.siparis_yonetim_sistemi.dto.UrunIstekDto;
import com.example.siparis_yonetim_sistemi.dto.UrunYanitDto;
import com.example.siparis_yonetim_sistemi.exception.UrunBulunamadiException;
import com.example.siparis_yonetim_sistemi.model.Urun;
import com.example.siparis_yonetim_sistemi.repository.UrunRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UrunService {

    private final UrunRepository urunRepository;

    public UrunService(UrunRepository urunRepository) {
        this.urunRepository = urunRepository;
    }

    public List<UrunYanitDto> tumUrunleriGetir() {
        return urunRepository.findAll().stream().map(urun -> yanitaDonustur(urun)).toList();
    }

    public UrunYanitDto urunEkle(UrunIstekDto istek) {
        Urun urun=new Urun(null,istek.getAd(),istek.getFiyat(),istek.getStok());
        Urun urun1 =urunRepository.save(urun);
        return yanitaDonustur(urun1);
    }

    public UrunYanitDto urunGetir(Long id) {
        return yanitaDonustur(urunBul(id));
    }

    private Urun urunBul(Long id) {
        return urunRepository.findById(id).orElseThrow(() -> new UrunBulunamadiException(id));
    }

    public UrunYanitDto urunGuncelle(Long id, UrunIstekDto istekDto) {
        Urun mevcut = urunBul(id);
        mevcut.setAd(istekDto.getAd());
        mevcut.setFiyat(istekDto.getFiyat());
        mevcut.setStok(istekDto.getStok());
        Urun urun1 =urunRepository.save(mevcut);
        return yanitaDonustur(urun1);
    }

    public void urunSil(Long id) {
        if(!urunRepository.existsById(id)){
            throw new UrunBulunamadiException(id);
        }
        urunRepository.deleteById(id);
    }

    private UrunYanitDto yanitaDonustur(Urun urun) {
        return new UrunYanitDto(
                urun.getId(),
                urun.getAd(),
                urun.getFiyat(),
                urun.getStok()
        );
    }
}
