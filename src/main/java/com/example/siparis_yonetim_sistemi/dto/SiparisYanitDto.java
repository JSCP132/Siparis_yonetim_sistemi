package com.example.siparis_yonetim_sistemi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiparisYanitDto {

    private Long id;

    private LocalDateTime tarih;

    private String kullaniciAdi;

    private List<SiparisKalemiYanitDto> kalemler = new ArrayList<>();

    private BigDecimal toplamTutar;

}
