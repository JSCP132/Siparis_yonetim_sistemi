package com.example.siparis_yonetim_sistemi.service;

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

    public List<Urun> tumUrunleriGetir() {
        return urunRepository.findAll();
    }

    public Urun urunEkle(Urun urun) {
        return urunRepository.save(urun);
    }

    public Urun urunGetir(Long id) {
        // Bilincli kusur: olmayan id'de null donuyor -> 200 + bos govde.
        // Asama 5'te .orElseThrow(...) olacak.
        return urunRepository.findById(id).orElse(null);
    }

    public Urun urunGuncelle(Long id, Urun yeniUrun) {
        // Once veritabanindaki MEVCUT kaydi cekiyoruz.
        Urun mevcut = urunRepository.findById(id).orElse(null);
        if (mevcut == null) {
            return null;
        }

        // Sadece alanlari degistiriyoruz, id'ye DOKUNMUYORUZ.
        // Kritik nokta: save() cagrildiginda nesnenin id'si doluysa Hibernate
        // INSERT degil UPDATE atar. Eger id'yi yeniUrun'dan alsaydik (null gelebilir)
        // Hibernate bunu yeni kayit sanip INSERT atardi.
        mevcut.setAd(yeniUrun.getAd());
        mevcut.setFiyat(yeniUrun.getFiyat());
        mevcut.setStok(yeniUrun.getStok());

        return urunRepository.save(mevcut);
    }

    public void urunSil(Long id) {
        urunRepository.deleteById(id);
    }
}
