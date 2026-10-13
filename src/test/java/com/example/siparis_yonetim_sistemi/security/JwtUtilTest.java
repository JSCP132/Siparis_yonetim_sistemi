package com.example.siparis_yonetim_sistemi.security;

import org.junit.jupiter.api.Test;

// Spring'siz deneme: JwtUtil değerlerini constructor'dan aldığı için onu kendimiz new'leyebiliyoruz.
// Uygulama ayağa kalkmaz, JWT_GIZLI_ANAHTAR ortam değişkeni de gerekmez.
class JwtUtilTest {

    // Sadece deneme için, gizli değiller (Base64, 32 byte'tan uzun).
    // İkisi de 42 byte: jjwt algoritmayı anahtar uzunluğuna göre seçer, aynı uzunluk -> ikisi de HS256.
    private static final String ANAHTAR = "c2FkZWNlLXRlc3RsZXItaWNpbi1hbmFodGFyLWdpemxpLWRlZ2lsLTMy";
    private static final String BASKA_ANAHTAR = "c2FsZGlyZ2FuaW4tYW5haHRhcmktYml6aW1raXlsZS1heW5pLWRlZ2ls";

    @Test
    void tokenUretVeOku() {
        JwtUtil jwtUtil=new JwtUtil(ANAHTAR,3_600_000L);
        String token=jwtUtil.tokenUret("atakan@ornek.com");
        System.out.println("Token : " + token);

        String email = jwtUtil.emailCikar(token);
        System.out.println("Email: " + email);
    }

    @Test
    void sahteMuhurluToken() {
        JwtUtil bizimSunucu = new JwtUtil(ANAHTAR, 3_600_000L);
        JwtUtil saldirgan = new JwtUtil(BASKA_ANAHTAR, 3_600_000L);

        String sahteToken = saldirgan.tokenUret("admin@ornek.com");
        try {
            String email = bizimSunucu.emailCikar(sahteToken);
            System.out.println("Kabul edildi: " + email);
        } catch (Exception e) {
            System.out.println("Reddedildi: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    @Test
    void suresiDolmusToken() {
        JwtUtil jwtUtil = new JwtUtil(ANAHTAR, -60_000L);

        String token = jwtUtil.tokenUret("atakan@ornek.com");

        try {
            String email = jwtUtil.emailCikar(token);
            System.out.println("Kabul edildi: " + email);
        } catch (Exception e) {
            System.out.println("Reddedildi: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    }

