package org.fy.kodeditoru.proje;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aktif proje modelini yöneten sınıf.
 *
 * Bu sınıf:
 * - proje kök klasörünü tutar.
 * - projedeki dosyaları yönetir.
 * - Java/XML/Kotlin dosyalarını filtreler.
 * - aktif dosya bilgisini saklar.
 *
 * Kural:
 * - UI üretmez.
 * - dosya içeriği okumaz.
 * - dosya kaydetmez.
 * - log yazmaz.
 * - thread başlatmaz.
 */
public final class ProjeModeli {

    private final File projeKlasoru;

    private final List<DosyaModeli> dosyalar;

    private DosyaModeli aktifDosya;

    /**
     * Proje modeli oluşturur.
     */
    public ProjeModeli(
            File projeKlasoru
    ) {

        if (projeKlasoru == null) {
            throw new IllegalArgumentException(
                    "Proje klasörü null olamaz."
            );
        }

        this.projeKlasoru = projeKlasoru;
        this.dosyalar = new ArrayList<>();
    }

    /**
     * Proje klasörünü döndürür.
     */
    public File getProjeKlasoru() {
        return projeKlasoru;
    }

    /**
     * Proje adını döndürür.
     */
    public String getProjeAdi() {
        return projeKlasoru.getName();
    }

    /**
     * Dosya listesini döndürür.
     */
    public List<DosyaModeli> getDosyalar() {

        return Collections.unmodifiableList(
                dosyalar
        );
    }

    /**
     * Aktif dosyayı döndürür.
     */
    public DosyaModeli getAktifDosya() {
        return aktifDosya;
    }

    /**
     * Aktif dosyayı ayarlar.
     */
    public void aktifDosyaAyarla(
            DosyaModeli dosya
    ) {

        aktifDosya = dosya;
    }

    /**
     * Projeye dosya ekler.
     */
    public void dosyaEkle(
            DosyaModeli dosya
    ) {

        if (dosya == null) {
            return;
        }

        dosyalar.add(dosya);

        if (aktifDosya == null) {
            aktifDosya = dosya;
        }
    }

    /**
     * Projeyi temizler.
     */
    public void temizle() {

        dosyalar.clear();
        aktifDosya = null;
    }

    /**
     * XML dosyalarını döndürür.
     */
    public List<DosyaModeli> xmlDosyalari() {

        List<DosyaModeli> sonuc =
                new ArrayList<>();

        for (DosyaModeli dosya : dosyalar) {

            if (dosya.isXml()) {
                sonuc.add(dosya);
            }
        }

        return sonuc;
    }

    /**
     * Java dosyalarını döndürür.
     */
    public List<DosyaModeli> javaDosyalari() {

        List<DosyaModeli> sonuc =
                new ArrayList<>();

        for (DosyaModeli dosya : dosyalar) {

            if (dosya.isJava()) {
                sonuc.add(dosya);
            }
        }

        return sonuc;
    }

    /**
     * Kotlin dosyalarını döndürür.
     */
    public List<DosyaModeli> kotlinDosyalari() {

        List<DosyaModeli> sonuc =
                new ArrayList<>();

        for (DosyaModeli dosya : dosyalar) {

            if (dosya.isKotlin()) {
                sonuc.add(dosya);
            }
        }

        return sonuc;
    }
}
