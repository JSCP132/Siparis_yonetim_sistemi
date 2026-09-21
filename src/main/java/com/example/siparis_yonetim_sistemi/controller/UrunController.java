package com.example.siparis_yonetim_sistemi.controller;

import com.example.siparis_yonetim_sistemi.model.Urun;
import com.example.siparis_yonetim_sistemi.service.UrunService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/urunler")
public class UrunController {

    private final UrunService urunService;

    public UrunController(UrunService urunService) {
        this.urunService = urunService;
    }

    @GetMapping
    public List<Urun> tumUrunleriGetir() {
        return urunService.tumUrunleriGetir();
    }

    @PostMapping
    public Urun urunEkle(@RequestBody Urun urun) {
        return urunService.urunEkle(urun);
    }

    @GetMapping("/{id}")
    public Urun urunGetir(@PathVariable Long id) {
        return urunService.urunGetir(id);
    }

    // Sinifin ustundeki @RequestMapping("/api/urunler") ile buradaki yol BIRLESTIRILIR.
    // "/api/urunler/{id}" yazsaydin gercek adres /api/urunler/api/urunler/{id} olurdu.
    // Ustteki @GetMapping("/{id}") ile ayni mantik — oradaki dogru yazilmisti.
    @PutMapping("/{id}")
    // @PathVariable: degeri URL'deki {id}'den al. Bu olmadan Spring id'yi query
    // parametresi sanir ve null gelir.
    // @RequestBody: istek govdesindeki JSON'i Urun nesnesine cevir.
    public Urun urunGuncelle(@PathVariable Long id, @RequestBody Urun urun) {
        return urunService.urunGuncelle(id, urun);
    }

    @DeleteMapping("/{id}")
    public void urunSil(@PathVariable Long id) {
        // "UrunService" tek basina bir satir degildi — sinif ADI yazilmis, cagri yapilmamisti.
        // Cagri, enjekte edilen ALAN uzerinden yapilir: urunService.
        urunService.urunSil(id);
    }
}
