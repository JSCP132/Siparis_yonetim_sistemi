package com.example.siparis_yonetim_sistemi.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class UrunIstekDto {


    @NotBlank(message = "Ürün adı boş olamaz")
    private String ad;

    @NotNull(message = "fiyat alanı boş olamaz")
    @Positive(message = "ürün fiyatı sıfırdan büyük olmalı")
    private BigDecimal fiyat;

    @NotNull(message = "stok alanı boş olamaz")
    @PositiveOrZero(message = "ürün stoğu negatif olamaz")
    private Integer stok;

}
