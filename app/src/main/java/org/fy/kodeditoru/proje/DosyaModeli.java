package org.fy.kodeditoru.proje;

import org.fy.kodeditoru.core.ProjeSabitleri;

import java.io.File;

/**
 * Proje içindeki tek dosya bilgisini taşıyan model.
 *
 * Bu sınıf:
 * - dosya adını tutar.
 * - dosya yolunu tutar.
 * - dosya uzantısını belirler.
 * - dosya türünü standartlaştırır.
 * - Java, XML ve Kotlin ayrımını sağlar.
 *
 * Kural:
 * - dosya okuma/yazma yapmaz.
 * - UI üretmez.
 * - log yazmaz.
 * - thread başlatmaz.
 */
public final class DosyaModeli {

    public static final String TUR_JAVA =
            ProjeSabitleri.TUR_JAVA;

    public static final String TUR_XML =
            ProjeSabitleri.TUR_XML;

    public static final String TUR_KOTLIN =
            ProjeSabitleri.TUR_KOTLIN;

    public static final String TUR_BILINMEYEN =
            ProjeSabitleri.TUR_BILINMEYEN;

    private final String ad;
    private final File dosya;
    private final String uzanti;
    private final String tur;

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
        this.tur = turBelirle(uzanti);
    }

    /**
     * Dosya modeli oluşturur.
     */
    public DosyaModeli(
            File dosya,
            String tur
    ) {

        if (dosya == null) {
            throw new IllegalArgumentException(
                    "Dosya null olamaz."
            );
        }

        this.dosya = dosya;
        this.ad = dosya.getName();
        this.uzanti = uzantiBul(ad);

        if (tur == null || tur.trim().isEmpty()) {
            this.tur = turBelirle(uzanti);
        } else {
            this.tur = tur.trim();
        }
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
     * Dosya türünü döndürür.
     */
    public String getTur() {
        return tur;
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

        return TUR_XML.equalsIgnoreCase(tur)
                || "xml".equalsIgnoreCase(uzanti);
    }

    /**
     * Dosyanın Java olup olmadığını döndürür.
     */
    public boolean isJava() {

        return TUR_JAVA.equalsIgnoreCase(tur)
                || "java".equalsIgnoreCase(uzanti);
    }

    /**
     * Dosyanın Kotlin olup olmadığını döndürür.
     */
    public boolean isKotlin() {

        return TUR_KOTLIN.equalsIgnoreCase(tur)
                || "kt".equalsIgnoreCase(uzanti);
    }

    /**
     * Dosya türünü belirler.
     */
    private String turBelirle(
            String uzanti
    ) {

        if ("java".equalsIgnoreCase(uzanti)) {
            return TUR_JAVA;
        }

        if ("xml".equalsIgnoreCase(uzanti)) {
            return TUR_XML;
        }

        if ("kt".equalsIgnoreCase(uzanti)) {
            return TUR_KOTLIN;
        }

        return TUR_BILINMEYEN;
    }

    /**
     * Dosya adından uzantı çıkarır.
     */
    private String uzantiBul(
            String dosyaAdi
    ) {

        int noktaIndex =
                dosyaAdi.lastIndexOf('.');

        if (noktaIndex < 0
                || noktaIndex == dosyaAdi.length() - 1) {

            return "";
        }

        return dosyaAdi.substring(
                noktaIndex + 1
        );
    }
    }
