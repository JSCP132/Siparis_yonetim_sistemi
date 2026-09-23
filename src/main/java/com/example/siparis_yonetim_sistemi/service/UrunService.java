package com.example.siparis_yonetim_sistemi.service;

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

    public List<Urun> tumUrunleriGetir() {
        return urunRepository.findAll();
    }

    public Urun urunEkle(Urun urun) {
        return urunRepository.save(urun);
    }

    public Urun urunGetir(Long id) {
        // orElseThrow bir Supplier ister: parametre almayan, deger ureten fonksiyon.
        // () -> ...  : "parametre almiyorum", sag taraf uretilecek exception.
        // id, metodun parametresi; lambda onu disaridan yakaliyor (capture).
        // Lambda sadece urun YOKSA calisir -> urun varsa exception nesnesi hic olusmaz.
        return urunRepository.findById(id).orElseThrow(() -> new UrunBulunamadiException(id));
    }

    public Urun urunGuncelle(Long id, Urun yeniUrun) {
        // Once veritabanindaki MEVCUT kaydi cekiyoruz; yoksa urunGetir exception firlatir.
        Urun mevcut = urunGetir(id);

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
        // Spring Data 3+: deleteById olmayan id'de sessiz kalir (200 donerdi), o yuzden once kontrol.
        if(!urunRepository.existsById(id)){
            throw new UrunBulunamadiException(id);
        }
        urunRepository.deleteById(id);
    }
}
