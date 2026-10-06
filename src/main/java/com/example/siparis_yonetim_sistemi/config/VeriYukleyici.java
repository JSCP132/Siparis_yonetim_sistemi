package com.example.siparis_yonetim_sistemi.config;

import com.example.siparis_yonetim_sistemi.model.Kullanici;
import com.example.siparis_yonetim_sistemi.model.Rol;
import com.example.siparis_yonetim_sistemi.model.Siparis;
import com.example.siparis_yonetim_sistemi.model.SiparisKalemi;
import com.example.siparis_yonetim_sistemi.model.Urun;
import com.example.siparis_yonetim_sistemi.repository.KullaniciRepository;
import com.example.siparis_yonetim_sistemi.repository.SiparisRepository;
import com.example.siparis_yonetim_sistemi.repository.UrunRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class VeriYukleyici implements CommandLineRunner {

    private final UrunRepository urunRepository;
    private final KullaniciRepository kullaniciRepository;
    private final SiparisRepository siparisRepository;
    private final PasswordEncoder passwordEncoder;

    public VeriYukleyici(UrunRepository urunRepository,
                         KullaniciRepository kullaniciRepository,
                         SiparisRepository siparisRepository,
                         PasswordEncoder passwordEncoder) {
        this.urunRepository = urunRepository;
        this.kullaniciRepository = kullaniciRepository;
        this.siparisRepository = siparisRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (siparisRepository.count() > 0) {
            return;
        }

        Urun klavye  = urunRepository.save(new Urun(null, "Mekanik Klavye", new BigDecimal("750.50"), 40));
        Urun mouse   = urunRepository.save(new Urun(null, "Kablosuz Mouse", new BigDecimal("320.00"), 60));
        Urun monitor = urunRepository.save(new Urun(null, "27 inc Monitor", new BigDecimal("4250.00"), 15));
        Urun kulak   = urunRepository.save(new Urun(null, "Kulaklik",       new BigDecimal("899.90"), 25));
        Urun kamera  = urunRepository.save(new Urun(null, "Webcam",         new BigDecimal("1150.00"), 12));

        // encode() her kullanıcı için ayrı çağrılıyor: her çağrı yeni rastgele salt üretir,
        // dördünün şifresi de "1234" olsa veritabanındaki hash'leri farklı olur.
        List<Kullanici> kullanicilar = kullaniciRepository.saveAll(List.of(
                new Kullanici("Ahmet Yilmaz", "ahmet@ornek.com",  passwordEncoder.encode("1234"), Rol.ADMIN),
                new Kullanici("Elif Demir",   "elif@ornek.com",   passwordEncoder.encode("1234"), Rol.USER),
                new Kullanici("Mert Kaya",    "mert@ornek.com",   passwordEncoder.encode("1234"), Rol.USER),
                new Kullanici("Zeynep Sahin", "zeynep@ornek.com", passwordEncoder.encode("1234"), Rol.USER)
        ));

        Urun[] katalog = { klavye, mouse, monitor, kulak, kamera };

        for (Kullanici kullanici : kullanicilar) {
            for (int i = 0; i < 3; i++) {
                Siparis siparis = new Siparis(kullanici);

                Urun birinci = katalog[i % katalog.length];
                Urun ikinci  = katalog[(i + 2) % katalog.length];

                siparis.kalemEkle(new SiparisKalemi(birinci, i + 1, birinci.getFiyat()));
                siparis.kalemEkle(new SiparisKalemi(ikinci, 1, ikinci.getFiyat()));

                siparisRepository.save(siparis);
            }
        }
    }
}
