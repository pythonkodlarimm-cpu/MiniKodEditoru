# Mini Kod Editörü

Modern Android tabanlı Java • Kotlin • XML mobil kod editörü.

Profesyonel responsive mimari, güvenli ekran alanı sistemi ve canlı XML önizleme altyapısıyla geliştirilmiştir.

---

# Özellikler

- Java kod düzenleme
- Kotlin kod düzenleme
- XML düzenleme
- XML canlı önizleme
- Android gerçek View render sistemi
- Responsive Android arayüz
- Tablet / telefon uyumu
- Akıllı ekran ölçek sistemi
- Safe-area / edge-to-edge desteği
- Gesture navigation uyumu
- Dynamic WindowInsets desteği
- Klavye altında kalmayan editör
- Yatay ve dikey kod kaydırma
- Modern Material tasarım
- Android/data log sistemi
- UTF-8 güvenli dosya yönetimi
- Dinamik proje oluşturma sistemi
- Java/XML/Kotlin dosya üretimi
- Hafif ve hızlı yapı

---

# Ekran Görüntüleri

Yakında eklenecek.

---

# Yeni Mimari

Yeni sürümde profesyonel Android edge-to-edge mimarisi kullanılmaktadır.

## Safe Area Sistemi

```text
bridges/
└── WindowInsetsBridge.java

core/
└── SafeAreaModel.java

ui/
├── SafeAreaYoneticisi.java
├── EdgeToEdgeYoneticisi.java
├── GestureInsetsYoneticisi.java
└── WindowMetricsYoneticisi.java
```

Bu sistem:

- status bar taşmalarını önler
- notch/cutout alanlarını algılar
- gesture navigation alanlarını korur
- klavye altında kalan editörü düzeltir
- tablet ve katlanabilir cihaz uyumu sağlar
- gerçek Android WindowInsets verisi kullanır

---

# Proje Yapısı

```text
app/
└── src/
    └── main/
        ├── java/
        │   └── org/fy/kodeditoru/
        │       ├── bridges/
        │       ├── core/
        │       ├── editor/
        │       ├── file/
        │       ├── log/
        │       ├── preview/
        │       ├── proje/
        │       └── ui/
        │
        ├── res/
        │   ├── drawable/
        │   ├── layout/
        │   ├── values/
        │   └── mipmap/
        │
        └── AndroidManifest.xml
```

---

# Kullanılan Teknolojiler

- Java 17
- Android SDK 35
- XML UI
- Android Studio
- Material Design
- WindowInsets API
- Edge-to-edge Android UI
- UTF-8 dosya sistemi

---

# Minimum Sistem

| Özellik | Değer |
|---|---|
| Android sürümü | Android 7+ |
| Min SDK | 24 |
| Target SDK | 35 |

---

# Kurulum

## 1. Repo klonla

```bash
git clone https://github.com/KULLANICI_ADI/MiniKodEditoru.git
```

---

## 2. Android Studio ile aç

```text
File -> Open
```

---

## 3. Gradle senkronizasyonunu bekle

Android Studio gerekli paketleri otomatik kuracaktır.

---

## 4. Çalıştır

```text
Shift + F10
```

veya:

```text
Run -> Run app
```

---

# Log Sistemi

Uygulama logları:

```text
Android/data/<paket_adi>/files/loglar/debug.log
```

alanına yazılır.

Örnek:

```text
Android/data/org.fy.kodeditoru/files/loglar/debug.log
```

---

# Hedefler

- Sekmeli editör sistemi
- Dosya explorer
- Gerçek proje ağacı
- Syntax highlight
- Kod tamamlama
- XML attribute parser
- RecyclerView dosya sistemi
- APK export sistemi
- Gradle parser
- Git entegrasyonu
- Terminal sistemi
- Java parser
- Kotlin parser
- ConstraintLayout preview
- Compose preview desteği

---

# Lisans

MIT License
