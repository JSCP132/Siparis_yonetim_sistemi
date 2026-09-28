package com.example.siparis_yonetim_sistemi.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> urunBulunamadi(UrunBulunamadiException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message",ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> dogrulamaHatasi(MethodArgumentNotValidException ex) {
        Map<String, String> hatalar = new HashMap<>();
        for (FieldError hata : ex.getBindingResult().getFieldErrors()) {
            hatalar.put(hata.getField(), hata.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(hatalar);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> veriButunluguIhlali(DataIntegrityViolationException ex) {
        Map<String, String> ihlal = Map.of("message", "Bu ürün bir siparişte kullanıldığı için silinemez.");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(ihlal);
    }
}
