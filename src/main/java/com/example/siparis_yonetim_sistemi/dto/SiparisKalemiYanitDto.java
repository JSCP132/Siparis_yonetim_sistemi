package com.example.siparis_yonetim_sistemi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiparisKalemiYanitDto {

    private Integer adet;

    private BigDecimal birimFiyat;

    private String urunAdi;

}
