package com.example.siparis_yonetim_sistemi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Testlerde JWT_GIZLI_ANAHTAR ortam değişkeni yok; sadece testlere özel, gizli olmayan bir anahtar.
@SpringBootTest(properties = "jwt.gizli-anahtar=c2FkZWNlLXRlc3RsZXItaWNpbi1hbmFodGFyLWdpemxpLWRlZ2lsLTMy")
class SiparisYonetimSistemiApplicationTests {

    @Test
    void contextLoads() {
    }

}
