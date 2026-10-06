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

## Aşama 5 — Validation ve Hata Yönetimi ✅ BİTTİ

- [x] pom'a `spring-boot-starter-validation` ekle
- [x] `dto` paketi → `UrunIstekDto`, `UrunYanitDto` (entity'yi dışarı açma)
- [x] `@NotNull`, `@NotBlank`, `@Positive`, `@PositiveOrZero` ile doğrulama (UrunIstekDto'da)
- [x] Service: `yanitaDonustur`, `urunBul` (private) ayrıldı; `urunGetir` + `tumUrunleriGetir` DTO dönüyor (Postman ✅)
- [x] `urunEkle` → DTO'ya geçir
- [x] `urunGuncelle` → DTO'ya geçir
- [x] Controller'da `@Valid` (POST + PUT) + `MethodArgumentNotValidException` handler → alan bazlı 400 mesajları
- [x] `exception` paketi → `UrunBulunamadiException` (`RuntimeException`, mesaj `super(...)` ile)
- [x] `GlobalExceptionHandler` (`@RestControllerAdvice`) → tek yerden hata yakalama
- [x] GET / PUT / DELETE olmayan id'de **404** (Postman'den test edildi)
- [x] Doğru status kodları: 201 Created (`@ResponseStatus`), 400
- [x] (Ek) Siparişte kullanılan ürün silinince 500 yerine **409 Conflict** (`DataIntegrityViolationException`)
- [x] Ara commit: "Asama 5 (1/2): exception ve GlobalExceptionHandler ile 404"
- [x] Commit: "Asama 5: validation ve hata yonetimi"

**Takıldığın noktalar (tekrar et):**
1. `System.out.println` mesajı exception'a koymaz → `super(mesaj)` ile üst sınıfa ver, yoksa `getMessage()` `null`.
2. Mesajın formatını **tek yer** bilir: exception sınıfı. Handler `ex.getMessage()` ile taşır, sabit metin yazmaz.
3. `orElseThrow` bir `Supplier` ister: `() -> new UrunBulunamadiException(id)`. Lambda = tek metotlu arayüzün (anonim sınıfın) kısa hâli. `<id>` generics sözdizimi, değer değil tip alır.
4. Exception **çağrılmaz, fırlatılır**: `throw new ...(id);` — `return` değil.
5. Service HTTP bilmez (`ResponseEntity`/`HttpStatus` yok); 404'e çevirmek handler'ın işi.
6. Spring Data 3+'ta `deleteById` olmayan id'de sessiz kalır → önce `existsById`.
7. PUT'ta gövde yoksa 400: Spring gövdeyi metodu çağırmadan **önce** okur, 404 kontrolüne sıra gelmez.
8. Mülakat: `open-in-view` varsayılan açık → istek boyunca persistence context açık kalır (aynı `findById` ikinci kez DB'ye gitmez). Neden birçok ekip kapatır?
9. Sayısal alanda `@NotBlank`/`@NotEmpty` **derlenir ama çalışma zamanında patlar** (`UnexpectedTypeException`) — anotasyonun tipe uygunluğunu derleyici kontrol etmez.
10. `@NotNull` ⊂ `@NotEmpty` ⊂ `@NotBlank` (null / "" / "   "). Metin için tek `@NotBlank` yeter. `@Positive` 0'ı reddeder, `@PositiveOrZero` kabul eder.
11. Yanıt DTO'suna doğrulama konmaz — doğrulama `@Valid` ile, sadece **gelen** veride tetiklenir.
12. İki kez aynı hata: `new XDto()` / boş gövdeli constructor → nesne oluşur ama **veri aktarılmaz**, hepsi `null`. Derleyici yakalamaz.
13. IntelliJ quick fix hatayı susturur, doğru düzeltmeyi yapmayabilir (id'siz boş constructor üretti).
14. `save()`'in dönüşünü kullan: yeni entity → `persist` (aynı nesne), id dolu → `merge` (yeni kopya).
15. Kural: **içeri giren → parametre → `UrunIstekDto`**, **dışarı çıkan → dönüş tipi → `UrunYanitDto`**. Entity controller'a hiç çıkmaz; service'in public metotları DTO, private metotları entity konuşur.
16. `@Valid` olmadan anotasyonlar hiçbir şey yapmaz (geçersiz ürün 200 ile kaydedildi). POST → 201, PUT → 200 (yeni kaynak yok), DELETE → ideali 204.
17. Exception zinciri: H2'nin `JdbcSQL...Exception`'ı en altta; Spring'in `DataIntegrityViolationException`'ını yakala → DB'den bağımsız. `ex.getMessage()`'ı istemciye dönme (tablo/SQL sızar). Global handler'da mesaj fazla özel ("Bu ürün...") — Aşama 6'da e-posta çakışmasında yanlış mesaj verir.

**Öğrenilecek:** Bean Validation, global exception handling, DTO deseni

---

## Aşama 6 — Spring Security + JWT

- [ ] `spring-boot-starter-security` geri ekle + `jjwt` bağımlılıkları (security ✅, jjwt bekliyor)
- [x] `Kullanici`'ya `sifre` ve `rol` alanları (`Rol` enum, `EnumType.STRING`)
- [x] `BCryptPasswordEncoder` bean'i, şifreyi **asla düz metin saklama**
- [x] `UserDetailsService` (`KullaniciDetayService`) — kullanıcılar veritabanından, `findByEmail`
- [ ] Sipariş endpoint'leri DTO döndürsün (şifre hash'i sızıntısı, not 22)
- [ ] `JwtUtil` — token üret / doğrula / içinden kullanıcı çıkar
- [ ] `JwtAuthenticationFilter` — her istekte header'daki token'ı kontrol et
- [ ] `SecurityConfig` — filter chain, hangi endpoint açık hangi kapalı (ilk hâli ✅: CSRF kapalı, stateless, GET ürünler + `/error` açık, gerisi kimlik ister, şimdilik HTTP Basic — JWT filter'ı sonra eklenecek)
- [ ] `POST /api/auth/kayit` ve `POST /api/auth/giris`
- [ ] Rol bazlı yetki: USER / ADMIN
- [ ] Test et: tokensiz → **401**, yanlış rolle → **403**
- [ ] Commit: "Asama 6: security ve jwt"

**Kavram notları (tekrar et):**
1. Geçerli token + yetersiz rol → **403**, 401 değil: token geçerli olduğu için kim olduğu biliniyor (authentication geçti), takılan yer authorization.
2. Filter'da fırlatılan exception `GlobalExceptionHandler`'a **ulaşmaz** — filter DispatcherServlet'ten önce, `@RestControllerAdvice` onun içinde çalışır. Security hataları `AuthenticationEntryPoint` / `AccessDeniedHandler` ile ele alınır. (Benzetme: kapıdaki görevli seni durdurursa içerideki şikâyet masasına varamazsın.)
3. Birden fazla sunucuda session sorun çıkarır (kayıt sadece girişin yapıldığı sunucunun hafızasında); JWT çıkarmaz (her sunucu aynı gizli anahtarla imzayı kendisi doğrular). Stateless = sunucu seni hatırlamaz, her istekte kanıt ister. (Benzetme: vestiyer fişi vs pasaport.)
4. JWT = header.payload.imza. Payload **şifreli değil**, Base64 ile kodlanmış → herkes okur (jwt.io). Kimlik (`sub`) ve rol konabilir, şifre (hash'i bile) **asla**. Kullanıcı bilgisi header'da değil payload'da durur.
5. İmza gizliliği değil **değiştirilmediğini** korur. Payload'ı değiştiren saldırgan **gizli anahtarı** bilmediği için yeni imza üretemez → token geçersiz → **401** (403 değil, çünkü kimlik doğrulanamadı). Gizli anahtar public repoya yazılmaz.
6. BCrypt her `encode`'da **yeni rastgele salt** üretir → aynı şifre her seferinde farklı hash → `equals` ile karşılaştırma hep `false`. Doğrusu `matches(girilen, kayitliHash)`: salt'ı hash'in içinden okur, girileni o salt'la hash'ler. SHA-256 hızlı (saldırgana yarar), BCrypt kasıtlı yavaş (cost).
7. **Auto-configuration:** sadece `spring-boot-starter-security` eklemek her endpoint'i kilitledi ve `user` + rastgele şifre üretti. Boot classpath'e bakar: `@ConditionalOnClass` (kütüphane var mı?) + `@ConditionalOnMissingBean` (geliştirici kendisi tanımlamış mı?). Kendi bean'ini tanımlarsan varsayılan geri çekilir. Aşama 3'te DataSource da böyle kurulmuştu. `@SpringBootApplication` = `@Configuration` + `@ComponentScan` + `@EnableAutoConfiguration`.
8. Aynı istek: Postman → **401**, tarayıcı → **login sayfası**. Security `Accept` header'ına bakıp farklı `AuthenticationEntryPoint` seçer (tarayıcı HTML ister → form login; Postman → Basic).
9. Basic Auth header'ı = Base64(`user:şifre`) → şifre **her istekte** gider, herkes çözer. JWT'de şifre sadece girişte bir kez gider.
10. pom değişince IntelliJ'de **Load Maven Changes** (Ctrl+Shift+O) yapılmazsa çalışan uygulama yeni bağımlılığı görmez (`mvnw compile` IntelliJ'i güncellemez). Beklenmedik sonuçta önce deneyi doğrula: çalışan şey gerçekten benim kodum mu?
11. Aynı kimlikle GET → 200, POST → 401. Sebep **CSRF**: `CsrfFilter` kimlik kontrolünden **önce** çalışır, CSRF token'ı olmayan POST'u **403** ile reddeder. 403 `/error`'a yönlenir, `/error` da kilitli ve o turda kullanıcı anonim → istemciye **401** ulaşır. Status kodu asıl sebebi gizleyebilir; gerçeği `logging.level.org.springframework.security=DEBUG` loglarında oku. (SecurityConfig'te `/error` açılacak.)
12. **CSRF** (siteler arası istek sahteciliği): tarayıcı cookie'yi her isteğe **kendiliğinden** ekler → kötü site, giriş yapmış kullanıcının tarayıcısına onun adına istek attırabilir. Koruma: sayfaya gömülü, kötü sitenin okuyamadığı rastgele token. Sadece veri **değiştiren** metotlar (POST/PUT/DELETE/PATCH) kontrol edilir; GET veri değiştirmemeli (REST kuralı).
13. JWT `Authorization` header'ında taşınırsa tarayıcı onu kendiliğinden eklemez → CSRF'in aracı yok → stateless JWT API'de `csrf` **kapatılır**. JWT cookie'de saklanırsa CSRF riski geri gelir.
14. Kendi `SecurityFilterChain` bean'imizi tanımlayınca varsayılan zincir geri çekildi ama `user` + rastgele şifre **kaldı**. Her auto-config parçası kendi koşuluna bakar: varsayılan kullanıcının koşulu `UserDetailsService` yokluğu, `SecurityFilterChain` değil. Kendi `UserDetailsService`'imizi yazınca şifre satırı kaybolacak.
15. Test sonucu (CSRF kapalı + kurallar): GET ürünler No Auth → 200, GET siparişler No Auth → 401, POST No Auth → 401 (artık **gerçek** 401, CSRF maskelemesi değil), POST Basic → **201**. Kurallar yukarıdan aşağı okunur, ilk eşleşen kazanır; `anyRequest()` en sona.
16. `private Rol USER, ADMIN;` tek bir rol alanı değil, `int x, y;` gibi **iki ayrı alan** tanımlar (derlenir, yanlış). Doğrusu `private Rol rol;` — tip (`Rol`) olası değerleri zaten söyler, alan adı küçük harf, değer constructor'da verilir. Enum'u `@Enumerated(EnumType.STRING)` ile sakla; varsayılan `ORDINAL` sıra numarası yazar, enum'a başa değer eklenince herkesin rolü kayar.
17. IntelliJ "Generate Constructor" seçilen her alanı alır, `id` dahil. `id` constructor'a verilmez (IDENTITY → veritabanı verir; dolu id ile `save` → `merge`, bkz. Aşama 5 not 14). Yan yana aynı tipte parametreler (`String ad, String email, String sifre`) yer değiştirse derleyici yakalamaz.
18. `encode("1234")` her kullanıcı için **ayrı** çağrılır → her biri yeni salt → aynı şifre, 4 farklı hash. Bir kez çağırıp paylaşırsan 4 hash aynı olur: veritabanını gören, aynı şifreyi kullananları anlar, birini kıran hepsini kırar.
19. `PasswordEncoder` bean'ini (BCrypt) ekleyince konsoldaki `user` + şifre de **401** vermeye başladı. Boot, encoder bean'i varsa üretilen şifreyi olduğu gibi (düz metin) saklar; giriş sırasında BCrypt düz metni hash'le karşılaştıramaz (log: `Encoded password does not look like BCrypt`). Bir bean eklemek başka bir auto-config parçasının davranışını değiştirebilir.
20. Kendi `UserDetailsService`'imizi yazınca konsoldaki şifre satırı **kayboldu** (`@ConditionalOnMissingBean(UserDetailsService)`). Akış: Spring `loadUserByUsername(email)` çağırır → biz kullanıcıyı bulup email + hash + rol veririz → `matches()`'i **Spring** çağırır. `findByEmail` gövdesiz: Spring Data SQL'i metot adından üretir.
21. Yanlış şifre ve olmayan kullanıcı **aynı 401**'i alır. Farklı cevap verseydi saldırgan hangi email'lerin kayıtlı olduğunu öğrenirdi (**user enumeration**). Spring `UsernameNotFoundException`'ı gizleyip "kimlik bilgisi hatalı"ya çevirir; bizim mesajımız istemciye gitmez.
22. ⚠️ **Sızıntı:** `GET /api/siparisler` entity döndürdüğü için giriş yapan **her** kullanıcı, bütün kullanıcıların email'ini, rolünü ve şifre hash'ini (`$2a$10$...`) görüyordu. Kullanıcı entity'sine alan eklemek API cevabını sessizce değiştirdi. Aşama 5 kuralı (entity dışarı çıkmaz) sadece ürünlerde uygulanmıştı. `@JsonIgnore` = **kara liste** (gizleneceği say; unutulan alan sızar), DTO = **beyaz liste** (gösterileceği say; unutulan alan gizli kalır). Mülakatta "neden DTO?" sorusunun hikâyesi bu.

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
