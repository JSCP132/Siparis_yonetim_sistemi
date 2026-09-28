package com.example.siparis_yonetim_sistemi.controller;

import com.example.siparis_yonetim_sistemi.dto.UrunIstekDto;
import com.example.siparis_yonetim_sistemi.dto.UrunYanitDto;
import com.example.siparis_yonetim_sistemi.service.UrunService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public List<UrunYanitDto> tumUrunleriGetir() {
        return urunService.tumUrunleriGetir();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UrunYanitDto urunEkle(@RequestBody @Valid UrunIstekDto istek) {
        return urunService.urunEkle(istek);
    }

    @GetMapping("/{id}")
    public UrunYanitDto urunGetir(@PathVariable Long id) {
        return urunService.urunGetir(id);
    }

    @PutMapping("/{id}")
    public UrunYanitDto urunGuncelle(@PathVariable Long id, @RequestBody @Valid UrunIstekDto urunIstekDto) {
        return urunService.urunGuncelle(id,urunIstekDto);
    }

    @DeleteMapping("/{id}")
    public void urunSil(@PathVariable Long id) {
        urunService.urunSil(id);
    }
}
