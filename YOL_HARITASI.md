# SipariÅŸ YÃ¶netim Sistemi â€” Yol HaritasÄ±

Temel paket: `com.example.siparis_yonetim_sistemi`
Kural: her aÅŸama bitince **commit at**. AÅŸama atlama.

---

## AÅŸama 0 â€” HazÄ±rlÄ±k âœ… (Claude yaptÄ±)

- [x] pom.xml temizlendi (security/oauth2/webservices Ã§Ä±karÄ±ldÄ± â€” AÅŸama 6'da geri gelecek)
- [x] `mvnw clean test` yeÅŸil, uygulama ayaÄŸa kalkÄ±yor
- [x] `git init` + ilk commit â†’ https://github.com/JSCP132/Siparis_yonetim_sistemi

---

## AÅŸama 1 â€” Ä°skelet ve Ä°lk Endpoint âœ… BÄ°TTÄ°

VeritabanÄ± yok, veri `List` iÃ§inde tutulacak.

- [x] `model` paketi â†’ `Urun` sÄ±nÄ±fÄ±
- [x] `controller` paketi â†’ `UrunController` sÄ±nÄ±fÄ±
- [x] `GET /api/urunler` â€” tÃ¼m Ã¼rÃ¼nler
- [x] `POST /api/urunler` â€” yeni Ã¼rÃ¼n
- [x] `GET /api/urunler/{id}` â€” tek Ã¼rÃ¼n
- [x] ÃœÃ§ endpoint de test edildi, Ã§alÄ±ÅŸÄ±yor
- [x] Commit: ilk commit'e dahil edildi

**Ã–ÄŸrenilecek:** `@RestController`, `@GetMapping`, `@PostMapping`, `@RequestBody`, `@PathVariable`, DispatcherServlet akÄ±ÅŸÄ±

---

## AÅŸama 2 â€” KatmanlÄ± Mimari âœ… BÄ°TTÄ°

- [x] `repository` paketi â†’ `UrunRepository` (`@Repository`, listeyi burasÄ± tutar)
- [x] `service` paketi â†’ `UrunService` (`@Service`, iÅŸ kurallarÄ± burada)
- [x] `UrunController` listeyi bÄ±rakÄ±p `UrunService`'i **constructor injection** ile alsÄ±n
- [x] `UrunService` de `UrunRepository`'yi constructor injection ile alsÄ±n
- [x] Endpoint Ã§Ä±ktÄ±larÄ± AÅŸama 1 ile birebir aynÄ± (refactoring doÄŸrulandÄ±)
- [x] Commit: ilk commit'e dahil edildi

**Kalan bilinÃ§li kusur:** `GET /api/urunler/99` â†’ 200 + boÅŸ gÃ¶vde dÃ¶nÃ¼yor, 404 dÃ¶nmeli.
AÅŸama 5'te `UrunService.urunGetir` iÃ§indeki `.orElse(null)` â†’ `.orElseThrow(...)` olacak.

**Ã–ÄŸrenilecek:** DI, IoC container, katmanlÄ± mimari, tek sorumluluk

---

## AÅŸama 3 â€” VeritabanÄ± ve JPA (H2 kÄ±smÄ± âœ…)

- [x] pom'a `spring-boot-starter-data-jpa` + `h2` ekle
- [x] `application.properties`: H2 ayarlarÄ± + `spring.jpa.show-sql=true` + H2 console
- [x] `Urun`'Ã¼ entity yap: `@Entity`, `@Table`, `@Id`, `@GeneratedValue(IDENTITY)`, `@Column`
- [x] `UrunRepository` â†’ `JpaRepository<Urun, Long>` arayÃ¼zÃ¼ oldu (liste + AtomicLong gitti)
- [x] `findById` â†’ `Optional` ile doÄŸru kullanÄ±m
- [x] CRUD tamamlandÄ± (PUT + DELETE eklendi), 5 endpoint de canlÄ± test edildi
- [ ] Sonra PostgreSQL'e geÃ§ (Docker'da), H2 sadece test profilinde kalsÄ±n
- [x] Commit: ilk commit'e dahil edildi

**TakÄ±ldÄ±ÄŸÄ±n 3 nokta (tekrar et):**
1. `@Entity(name="urunler")` tablo adÄ±nÄ± deÄŸiÅŸtirmez â†’ tablo adÄ± `@Table(name=...)` ile verilir.
2. `@Id` import'u `jakarta.persistence.Id` olmalÄ±; `org.springframework.data.annotation.Id` derlenir ama Hibernate birincil anahtarÄ± gÃ¶remez.
3. `interface UrunRepository<Urun, Long>` tip argÃ¼manÄ± **vermez**, yeni tip deÄŸiÅŸkeni **tanÄ±mlar**. ArgÃ¼manlar `extends JpaRepository<Urun, Long>` ile verilir.

**Ã–ÄŸrenilecek:** JPA/Hibernate temelleri, Spring Data JPA, `Optional`

---

## AÅŸama 4 â€” Ä°liÅŸkiler ve N+1 âš ï¸ KRÄ°TÄ°K

- [ ] `Kullanici` entity'si
- [ ] `Siparis` entity'si
- [ ] `SiparisKalemi` entity'si (sipariÅŸ â†” Ã¼rÃ¼n arasÄ±, adet + o anki fiyat)
- [ ] `Kullanici` 1â€”N `Siparis`, `Siparis` 1â€”N `SiparisKalemi`, `SiparisKalemi` Nâ€”1 `Urun`
- [ ] `show-sql=true` aÃ§Ä±kken sipariÅŸleri listele, **loglardaki sorgu sayÄ±sÄ±nÄ± say**
- [ ] N+1'i gÃ¶r (1 sorgu + N adet ek sorgu)
- [ ] `JOIN FETCH`'li `@Query` yaz, sorgu sayÄ±sÄ±nÄ±n dÃ¼ÅŸtÃ¼ÄŸÃ¼nÃ¼ **aynÄ± loglarda doÄŸrula**
- [ ] `@Transactional`'Ä± service katmanÄ±na uygula
- [ ] Not tut: kaÃ§ sorgudan kaÃ§a dÃ¼ÅŸtÃ¼ â†’ mÃ¼lakatta bu sayÄ±yÄ± sÃ¶yleyeceksin
- [ ] Commit: "Asama 4: iliskiler ve N+1 cozumu"

**Ã–ÄŸrenilecek:** Ä°liÅŸkiler, lazy/eager, N+1, `@Transactional`

---

## AÅŸama 5 â€” Validation ve Hata YÃ¶netimi

- [ ] pom'a `spring-boot-starter-validation` ekle
- [ ] `dto` paketi â†’ `UrunIstekDto`, `UrunYanitDto` (entity'yi dÄ±ÅŸarÄ± aÃ§ma)
- [ ] `@NotNull`, `@NotBlank`, `@Min`, `@Positive` ile doÄŸrulama
- [ ] Controller'da `@Valid`
- [ ] `exception` paketi â†’ `UrunBulunamadiException` gibi kendi exception'larÄ±n
- [ ] `GlobalExceptionHandler` (`@RestControllerAdvice`) â†’ tek yerden hata yakalama
- [ ] DoÄŸru status kodlarÄ±: 201 Created, 404, 400
- [ ] Commit: "Asama 5: validation ve hata yonetimi"

**Ã–ÄŸrenilecek:** Bean Validation, global exception handling, DTO deseni

---

## AÅŸama 6 â€” Spring Security + JWT

- [ ] `spring-boot-starter-security` geri ekle + `jjwt` baÄŸÄ±mlÄ±lÄ±klarÄ±
- [ ] `Kullanici`'ya `sifre` ve `rol` alanlarÄ±
- [ ] `BCryptPasswordEncoder` bean'i, ÅŸifreyi **asla dÃ¼z metin saklama**
- [ ] `JwtUtil` â€” token Ã¼ret / doÄŸrula / iÃ§inden kullanÄ±cÄ± Ã§Ä±kar
- [ ] `JwtAuthenticationFilter` â€” her istekte header'daki token'Ä± kontrol et
- [ ] `SecurityConfig` â€” filter chain, hangi endpoint aÃ§Ä±k hangi kapalÄ±
- [ ] `POST /api/auth/kayit` ve `POST /api/auth/giris`
- [ ] Rol bazlÄ± yetki: USER / ADMIN
- [ ] Test et: tokensiz â†’ **401**, yanlÄ±ÅŸ rolle â†’ **403**
- [ ] Commit: "Asama 6: security ve jwt"

**Ã–ÄŸrenilecek:** Authentication vs authorization, filter chain, JWT â€” mÃ¼lakatÄ±n en yoÄŸun sorulan kÄ±smÄ±

---

## AÅŸama 7 â€” Test

- [ ] `UrunServiceTest` â€” JUnit 5 ile unit test
- [ ] Mockito ile `UrunRepository`'yi mock'la (DI'nÄ±n faydasÄ± burada gÃ¶rÃ¼nÃ¼r)
- [ ] Hata senaryosunu da test et (bulunamayan Ã¼rÃ¼n â†’ exception fÄ±rlÄ±yor mu)
- [ ] `@SpringBootTest` + `MockMvc` ile en az bir uÃ§tan uca entegrasyon testi
- [ ] Commit: "Asama 7: testler"

**Ã–ÄŸrenilecek:** Unit vs integration test, mocking, test edilebilir kod

---

## AÅŸama 8 â€” Docker ve DokÃ¼mantasyon

- [ ] `Dockerfile` (multi-stage: build + runtime)
- [ ] `docker-compose.yml` â€” uygulama + PostgreSQL
- [ ] `springdoc-openapi` ekle â†’ Swagger UI
- [ ] `README.md`: ne yapar, nasÄ±l Ã§alÄ±ÅŸtÄ±rÄ±lÄ±r, endpoint listesi, N+1 notun
- [ ] GitHub'a push
- [ ] Commit: "Asama 8: docker ve dokumantasyon"

**Ã–ÄŸrenilecek:** KonteynerleÅŸtirme, API dokÃ¼mantasyonu, portfolyo sunumu

---

## SÃ¼rekli kurallar

1. `spring.jpa.show-sql=true` **hep aÃ§Ä±k kalsÄ±n** â€” Hibernate'in ne yaptÄ±ÄŸÄ±nÄ± gÃ¶rmek en Ã¶ÄŸretici ÅŸey.
2. TakÄ±ldÄ±ÄŸÄ±n anotasyonun "ne yaptÄ±ÄŸÄ±nÄ±" sor. MÃ¼lakatta tam bu sorulacak.
3. Her aÅŸamada commit at. Git geÃ§miÅŸi nasÄ±l ilerlediÄŸini gÃ¶sterir.

