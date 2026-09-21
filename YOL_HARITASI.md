# Sipariş Yönetim Sistemi — Yol Haritası

Temel paket: `com.example.siparis_yonetim_sistemi`
Kural: her aşama bitince **commit at**. Aşama atlama.

---

## Aşama 0 — Hazırlık ✅ (Claude yaptı)

- [x] pom.xml temizlendi (security/oauth2/webservices çıkarıldı — Aşama 6'da geri gelecek)
- [x] `mvnw clean test` yeşil, uygulama ayağa kalkıyor
- [x] `git init` + ilk commit → https://github.com/JSCP132/Siparis_yonetim_sistemi

---

## Aşama 1 — İskelet ve İlk Endpoint ✅ BİTTİ

Veritabanı yok, veri `List` içinde tutulacak.

- [x] `model` paketi → `Urun` sınıfı
- [x] `controller` paketi → `UrunController` sınıfı
- [x] `GET /api/urunler` — tüm ürünler
- [x] `POST /api/urunler` — yeni ürün
- [x] `GET /api/urunler/{id}` — tek ürün
- [x] Üç endpoint de test edildi, çalışıyor
- [x] Commit: ilk commit'e dahil edildi (14a915c)

**Öğrenilecek:** `@RestController`, `@GetMapping`, `@PostMapping`, `@RequestBody`, `@PathVariable`, DispatcherServlet akışı

---

## Aşama 2 — Katmanlı Mimari ✅ BİTTİ

- [x] `repository` paketi → `UrunRepository` (`@Repository`, listeyi burası tutar)
- [x] `service` paketi → `UrunService` (`@Service`, iş kuralları burada)
- [x] `UrunController` listeyi bırakıp `UrunService`'i **constructor injection** ile alsın
- [x] `UrunService` de `UrunRepository`'yi constructor injection ile alsın
- [x] Endpoint çıktıları Aşama 1 ile birebir aynı (refactoring doğrulandı)
- [x] Commit: ilk commit'e dahil edildi (14a915c)

**Kalan bilinçli kusur:** `GET /api/urunler/99` → 200 + boş gövde dönüyor, 404 dönmeli.
Aşama 5'te `UrunService.urunGetir` içindeki `.orElse(null)` → `.orElseThrow(...)` olacak.

**Öğrenilecek:** DI, IoC container, katmanlı mimari, tek sorumluluk

---

## Aşama 3 — Veritabanı ve JPA (H2 kısmı ✅)

- [x] pom'a `spring-boot-starter-data-jpa` + `h2` ekle
- [x] `application.properties`: H2 ayarları + `spring.jpa.show-sql=true` + H2 console
- [x] `Urun`'ü entity yap: `@Entity`, `@Table`, `@Id`, `@GeneratedValue(IDENTITY)`, `@Column`
- [x] `UrunRepository` → `JpaRepository<Urun, Long>` arayüzü oldu (liste + AtomicLong gitti)
- [x] `findById` → `Optional` ile doğru kullanım
- [x] CRUD tamamlandı (PUT + DELETE eklendi), 5 endpoint de canlı test edildi
- [ ] Sonra PostgreSQL'e geç (Docker'da), H2 sadece test profilinde kalsın
- [x] Commit: ilk commit'e dahil edildi (14a915c)

**Takıldığın 3 nokta (tekrar et):**
1. `@Entity(name="urunler")` tablo adını değiştirmez → tablo adı `@Table(name=...)` ile verilir.
2. `@Id` import'u `jakarta.persistence.Id` olmalı; `org.springframework.data.annotation.Id` derlenir ama Hibernate birincil anahtarı göremez.
3. `interface UrunRepository<Urun, Long>` tip argümanı **vermez**, yeni tip değişkeni **tanımlar**. Argümanlar `extends JpaRepository<Urun, Long>` ile verilir.

**Öğrenilecek:** JPA/Hibernate temelleri, Spring Data JPA, `Optional`

---

## Aşama 4 — İlişkiler ve N+1 ✅ BİTTİ ⚠️ KRİTİK

- [x] `Kullanici` entity'si
- [x] `Siparis` entity'si
- [x] `SiparisKalemi` entity'si (sipariş ↔ ürün arası, adet + o anki fiyat)
- [x] `Kullanici` 1—N `Siparis`, `Siparis` 1—N `SiparisKalemi`, `SiparisKalemi` N—1 `Urun`
- [x] `show-sql=true` açıkken siparişleri listele, **loglardaki sorgu sayısını say**
- [x] N+1'i gör (1 sorgu + N adet ek sorgu)
- [x] `JOIN FETCH`'li `@Query` yaz, sorgu sayısının düştüğünü **aynı loglarda doğrula**
- [x] `@Transactional(readOnly = true)` service katmanına uygulandı
- [x] Not tut: kaç sorgudan kaça düştü
- [x] Commit: "Asama 4: iliskiler ve N+1 cozumu"

### 📌 MÜLAKATTA SÖYLEYECEĞİN SAYI

**12 sipariş listelemek için 17 sorgu → 1 sorgu.**

| | Sorgu | Dağılım |
|---|---|---|
| `findAll()` | **17** | 1 sipariş + 4 kullanıcı + 12 kalem |
| `JOIN FETCH` (ilk hâli) | **6** | 1 birleşik + 5 ürün |
| `JOIN FETCH` (tam hâli) | **1** | hepsi tek sorguda |

İki endpoint de kodda duruyor, yan yana karşılaştırılabilir:
`GET /api/siparisler` (yavaş) ve `GET /api/siparisler/hizli` (JOIN FETCH).

**Anlatacağın hikâye:**
1. `@ManyToOne` varsayılanı **EAGER**, `@OneToMany` varsayılanı **LAZY** — bu asimetri N+1'i doğuruyor.
2. Kullanıcı sorgusu 12 değil 4 çıktı, çünkü **persistence context** (1. seviye önbellek) aynı id'yi tekrar sormuyor.
3. İlk `JOIN FETCH`'ten sonra 6 sorgu kaldı: kalemlere inmiştik ama `k.urun`'a inmemiştik. **Bir N+1'i çözmek bir alttakini görünür kılar** — logları tekrar okumak şart.
4. `LEFT JOIN FETCH s.kalemler k LEFT JOIN FETCH k.urun` ile 1'e indi.
5. `DISTINCT` gerekli: 12 sipariş × 2 kalem = 24 satır döner, sipariş tekrarlanır.

**Öğrenilecek:** İlişkiler, lazy/eager, N+1, `@Transactional`

---

## Aşama 5 — Validation ve Hata Yönetimi

- [ ] pom'a `spring-boot-starter-validation` ekle
- [ ] `dto` paketi → `UrunIstekDto`, `UrunYanitDto` (entity'yi dışarı açma)
- [ ] `@NotNull`, `@NotBlank`, `@Min`, `@Positive` ile doğrulama
- [ ] Controller'da `@Valid`
- [ ] `exception` paketi → `UrunBulunamadiException` gibi kendi exception'ların
- [ ] `GlobalExceptionHandler` (`@RestControllerAdvice`) → tek yerden hata yakalama
- [ ] Doğru status kodları: 201 Created, 404, 400
- [ ] Commit: "Asama 5: validation ve hata yonetimi"

**Öğrenilecek:** Bean Validation, global exception handling, DTO deseni

---

## Aşama 6 — Spring Security + JWT

- [ ] `spring-boot-starter-security` geri ekle + `jjwt` bağımlılıkları
- [ ] `Kullanici`'ya `sifre` ve `rol` alanları
- [ ] `BCryptPasswordEncoder` bean'i, şifreyi **asla düz metin saklama**
- [ ] `JwtUtil` — token üret / doğrula / içinden kullanıcı çıkar
- [ ] `JwtAuthenticationFilter` — her istekte header'daki token'ı kontrol et
- [ ] `SecurityConfig` — filter chain, hangi endpoint açık hangi kapalı
- [ ] `POST /api/auth/kayit` ve `POST /api/auth/giris`
- [ ] Rol bazlı yetki: USER / ADMIN
- [ ] Test et: tokensiz → **401**, yanlış rolle → **403**
- [ ] Commit: "Asama 6: security ve jwt"

**Öğrenilecek:** Authentication vs authorization, filter chain, JWT — mülakatın en yoğun sorulan kısmı

---

## Aşama 7 — Test

- [ ] `UrunServiceTest` — JUnit 5 ile unit test
- [ ] Mockito ile `UrunRepository`'yi mock'la (DI'nın faydası burada görünür)
- [ ] Hata senaryosunu da test et (bulunamayan ürün → exception fırlıyor mu)
- [ ] `@SpringBootTest` + `MockMvc` ile en az bir uçtan uca entegrasyon testi
- [ ] Commit: "Asama 7: testler"

**Öğrenilecek:** Unit vs integration test, mocking, test edilebilir kod

---

## Aşama 8 — Docker ve Dokümantasyon

- [ ] `Dockerfile` (multi-stage: build + runtime)
- [ ] `docker-compose.yml` — uygulama + PostgreSQL
- [ ] `springdoc-openapi` ekle → Swagger UI
- [ ] `README.md`: ne yapar, nasıl çalıştırılır, endpoint listesi, N+1 notun
- [ ] GitHub'a push
- [ ] Commit: "Asama 8: docker ve dokumantasyon"

**Öğrenilecek:** Konteynerleştirme, API dokümantasyonu, portfolyo sunumu

---

## Sürekli kurallar

1. `spring.jpa.show-sql=true` **hep açık kalsın** — Hibernate'in ne yaptığını görmek en öğretici şey.
2. Takıldığın anotasyonun "ne yaptığını" sor. Mülakatta tam bu sorulacak.
3. Her aşamada commit at. Git geçmişi nasıl ilerlediğini gösterir.
