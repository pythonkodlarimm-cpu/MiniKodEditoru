package org.fy.kodeditoru.proje;

import java.io.File;

/**
 * Proje içindeki tek dosya bilgisini taşıyan model.
 *
 * Bu sınıf:
 * - dosya adını tutar.
 * - dosya yolunu tutar.
 * - dosya türünü belirler.
 *
 * Kural:
 * - dosya okuma/yazma yapmaz.
 * - UI üretmez.
 * - log yazmaz.
 */
public final class DosyaModeli {

    private final String ad;
    private final File dosya;
    private final String uzanti;

    /**
     * Dosya modeli oluşturur.
     */
    public DosyaModeli(
            File dosya
    ) {

        if (dosya == null) {
            throw new IllegalArgumentException(
                    "Dosya null olamaz."
            );
        }

        this.dosya = dosya;
        this.ad = dosya.getName();
        this.uzanti = uzantiBul(ad);
    }

    /**
     * Dosya adını döndürür.
     */
    public String getAd() {
        return ad;
    }

    /**
     * Dosya nesnesini döndürür.
     */
    public File getDosya() {
        return dosya;
    }

    /**
     * Dosya uzantısını döndürür.
     */
    public String getUzanti() {
        return uzanti;
    }

    /**
     * Dosya tam yolunu döndürür.
     */
    public String getTamYol() {
        return dosya.getAbsolutePath();
    }

    /**
     * Dosyanın XML olup olmadığını döndürür.
     */
    public boolean isXml() {
        return "xml".equalsIgnoreCase(uzanti);
    }

    /**
     * Dosyanın Java olup olmadığını döndürür.
     */
    public boolean isJava() {
        return "java".equalsIgnoreCase(uzanti);
    }

    /**
     * Dosyanın Kotlin olup olmadığını döndürür.
     */
    public boolean isKotlin() {
        return "kt".equalsIgnoreCase(uzanti);
    }

    /**
     * Dosya adından uzantı çıkarır.
     */
    private String uzantiBul(
            String dosyaAdi
    ) {

        int noktaIndex =
                dosyaAdi.lastIndexOf('.');

        if (noktaIndex < 0 || noktaIndex == dosyaAdi.length() - 1) {
            return "";
        }

        return dosyaAdi.substring(noktaIndex + 1);
    }
}
