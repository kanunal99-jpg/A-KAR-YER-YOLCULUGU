# A-KAR-YER-YOLCULUGU — KANITLI TEKNİK DENETİM

**Tarih:** 15.10.2026  
**Kapsam:** GitHub dosya ağacı, seçili kaynak kodları, CI logları, Releases. Bu belge gerçek cihaz uçtan uca testi yerine geçmez.  
**Dosya sayısı (denetim anında):** 67  
**CI gözlemi:** 15.10.2026 Android APK job başarılı; erişilen Releases listesi boş.  
**Anayasa:** [PROJE_ANAYASASI_VE_DUZELTILECEKLER.md](https://github.com/kanunal99-jpg/A-KAR-YER-YOLCULUGU/blob/main/PROJE_ANAYASASI_VE_DUZELTILECEKLER.md)

## Kanıtlarla Düzeltilmesi Gerekenler

### 1. Eski CV parser'ında uydurma kariyer geçmişi var — P1 (KODDA DOĞRULANMIŞ VERİ BÜTÜNLÜĞÜ RİSKİ)
- **Dosya/kanıt:** [app/src/main/java/com/example/domain/CareerAgentEngine.kt#L15-L105](https://github.com/kanunal99-jpg/A-KAR-YER-YOLCULUGU/blob/main/app/src/main/java/com/example/domain/CareerAgentEngine.kt#L15-L105); [CI koşusu](https://github.com/kanunal99-jpg/A-KAR-YER-YOLCULUGU/actions/runs/38673141756)
- **Gözlem:** parseCvText() girdi içinde Satış/Yönetici/Sütaş kelimesi görünce Sütaş ve Peyman şirket deneyimlerini, sahte başarı/KPI verileriyle oluşturabiliyor; boşsa da örnek firma deneyimi üretiyor.
- **Somut örnek / etkisi:** 'Satış Yöneticisi' içeren ama Sütaş/Peyman yazmayan CV ile parseCvText çağırıldığında sahte deneyim çıkar.
- **Düzeltme öncesi kabul kriteri:** Mevcut UI akışı CareerViewModel.kt#L81 üzerinde EvidenceBasedCvParser kullanıyor. Eski metot silinmeli/yalnızca işaretlenmiş demo olmalı; negatif testle uydurma veri üretilmediği ispatlanmalı.
- **Durum:** ARAŞTIRILDI — henüz kaynak kod düzeltmesi veya cihaz testi yapılmadı.

### 2. İndirilebilir APK Release görünmüyor — P1 (SÜRÜM TESLİM EKSİĞİ)
- **Dosya/kanıt:** [releases](https://github.com/kanunal99-jpg/A-KAR-YER-YOLCULUGU/releases); [CI koşusu](https://github.com/kanunal99-jpg/A-KAR-YER-YOLCULUGU/actions/runs/38673141756)
- **Gözlem:** Build workflow başarılı olsa da GitHub Releases koleksiyonu boş.
- **Somut örnek / etkisi:** Telefon üzerinden Releases/Yayınlar alanından APK indirilemiyor.
- **Düzeltme öncesi kabul kriteri:** Başarılı sürümde APK + SHA-256 Release asset'i, açık URL ve gerçek cihaz kurulumu doğrulanmalı.
- **Durum:** ARAŞTIRILDI — henüz kaynak kod düzeltmesi veya cihaz testi yapılmadı.

### 3. Eşleştirme puanlarının açıklanabilirliği — P2 (KOD RİSKİ)
- **Dosya/kanıt:** [app/src/main/java/com/example/domain/CareerAgentEngine.kt#L244-L269](https://github.com/kanunal99-jpg/A-KAR-YER-YOLCULUGU/blob/main/app/src/main/java/com/example/domain/CareerAgentEngine.kt#L244-L269); [CI koşusu](https://github.com/kanunal99-jpg/A-KAR-YER-YOLCULUGU/actions/runs/38673141756)
- **Gözlem:** Yetenek/sektör/tecrübe bazlı sabit skorlar kullanılıyor; puan kaynağı işverene ait veri değil.
- **Somut örnek / etkisi:** ATS %95 benzeri rakamlar özgeçmişte doğrulanmış başarı oranı gibi algılanabilir.
- **Düzeltme öncesi kabul kriteri:** 'Tahmini uygunluk' ibaresi, formül şeffaflığı, kapsama göre açıklama ve negatif testler.
- **Durum:** ARAŞTIRILDI — henüz kaynak kod düzeltmesi veya cihaz testi yapılmadı.

### 4. Evidenced parser CV kaynak doğrulaması — P2 (TEST GEREK)
- **Dosya/kanıt:** [app/src/main/java/com/example/domain/EvidenceBasedCvParser.kt#L13-L18](https://github.com/kanunal99-jpg/A-KAR-YER-YOLCULUGU/blob/main/app/src/main/java/com/example/domain/EvidenceBasedCvParser.kt#L13-L18); [CI koşusu](https://github.com/kanunal99-jpg/A-KAR-YER-YOLCULUGU/actions/runs/38673141756)
- **Gözlem:** Ayrı parser yalnızca CV kaynak metnindeki açık gerçekleri taşımayı hedefliyor; aktif UI bunu kullanıyor.
- **Somut örnek / etkisi:** Sahte ad/telefon/firma olmaması ve Türkçe OCR biçim sapmaları test edilmelidir.
- **Düzeltme öncesi kabul kriteri:** Regex kaynak doğruluğu, boş CV, tablo OCR, Kocaeli/İstanbul, CV hiç deneyim içermeyen senaryoları.
- **Durum:** ARAŞTIRILDI — henüz kaynak kod düzeltmesi veya cihaz testi yapılmadı.

## 4 Fazlı Geliştirme ve Doğrulama Kapıları

1. **Faz 1 — Araştır:** Tam modül/dosya ve bağımlılık envanteri, doğrulanmış/farklılaşmış hatalar, etki analizi. **Bu rapor ilk kaynak/CI kanıtıdır; bütün işlevlerin çalıştığını iddia etmez.**
2. **Faz 2 — Düzelt:** Önce P0/P1, en küçük güvenli kod değişimi, gizli anahtar/ücret/erişim izinlerini güvenli yöntemlerle ele al.
3. **Faz 3 — Uygulama ve test:** Unit, integrasyon, UI, offline, yetki reddi, yanlış veri, servis kesintisi, gerçek fallback, CI + Android gerçek cihaz varsa.
4. **Faz 4 — Yayın:** CI yeşil → Android ise SHA-256'lı indirilebilir Release APK → kullanıcı akışları smoke → kanıt linkleri.

## Kapatma Kriteri
Her bulgu için issue veya değişiklik ID'si, önce/sonra kanıtı, ilgili satır, test raporu, CI koşu bağlantısı, release/dağıtım bağlantısı, sonuç tarihi zorunludur. **Yalnızca belgenin eklenmiş olması sorun giderildi anlamına gelmez.**
