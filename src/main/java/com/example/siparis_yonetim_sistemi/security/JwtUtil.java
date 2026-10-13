package com.example.siparis_yonetim_sistemi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey anahtar;
    private final long gecerlilikSuresiMs;

    // @Value: application.properties'teki değeri constructor parametresine verir.
    // Base64 metin byte'lara çevrilip HMAC anahtarı yapılıyor; 32 byte'tan kısaysa jjwt uygulamayı açtırmaz.
    public JwtUtil(@Value("${jwt.gizli-anahtar}") String gizliAnahtar,
                   @Value("${jwt.gecerlilik-suresi-ms}") long gecerlilikSuresiMs) {
        this.anahtar = Keys.hmacShaKeyFor(Decoders.BASE64.decode(gizliAnahtar));
        this.gecerlilikSuresiMs = gecerlilikSuresiMs;
    }

    public String tokenUret(String email) {
        Date simdi = new Date();
        Date bitis = new Date(simdi.getTime() + gecerlilikSuresiMs);

        return Jwts.builder().subject(email).issuedAt(simdi).expiration(bitis).signWith(anahtar).compact();
    }

    public String emailCikar(String token) {
        return Jwts.parser().verifyWith(anahtar).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
