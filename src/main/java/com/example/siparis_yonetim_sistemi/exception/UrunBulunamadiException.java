package com.example.siparis_yonetim_sistemi.exception;

public class UrunBulunamadiException extends RuntimeException{
    public UrunBulunamadiException(Long id) {
        super("id'si " + id + " olan ürün bulunamadı!");

    }
}
