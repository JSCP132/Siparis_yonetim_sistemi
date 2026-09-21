package com.example.siparis_yonetim_sistemi.controller;

import com.example.siparis_yonetim_sistemi.model.Siparis;
import com.example.siparis_yonetim_sistemi.service.SiparisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/siparisler")
public class SiparisController {

    private final SiparisService siparisService;

    public SiparisController(SiparisService siparisService) {
        this.siparisService = siparisService;
    }

    @GetMapping
    public List<Siparis> tumSiparisleriGetir() {
        return siparisService.tumSiparisleriGetir();
    }
}
