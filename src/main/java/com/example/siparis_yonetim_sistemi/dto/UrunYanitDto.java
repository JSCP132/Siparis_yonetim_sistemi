package com.example.siparis_yonetim_sistemi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrunYanitDto {

    private Long id;
    private String ad;
    private BigDecimal fiyat;
    private Integer stok;

}
