package org.fy.kodeditoru.core;

/**
 * Kod editörü uygulaması merkezi proje sabitleri.
 *
 * Bu sınıf:
 * - uygulama sabitlerini tek merkezde tutar
 * - varsayılan proje klasörlerini tanımlar
 * - desteklenen dosya uzantılarını tanımlar
 * - editör varsayılanlarını standartlaştırır
 *
 * Kural:
 * - state tutmaz
 * - Context taşımaz
 * - UI üretmez
 * - dosya işlemi yapmaz
 * - iş mantığı çalıştırmaz
 */
public final class ProjeSabitleri {

    /**
     * Uygulama adı.
     */
    public static final String UYGULAMA_ADI =
            "Mini Kod Editoru";

    /**
     * Varsayılan proje klasörü.
     */
    public static final String ANA_PROJE_KLASORU =
            "Projelerim";

    /**
     * Varsayılan Android proje adı.
     */
    public static final String VARSAYILAN_PROJE_ADI =
            "OrnekAndroidProje";

    /**
     * Java kaynak klasörü.
     */
    public static final String JAVA_KLASORU =
            "app/src/main/java";

    /**
     * XML layout klasörü.
     */
    public static final String LAYOUT_KLASORU =
            "app/src/main/res/layout";

    /**
     * Drawable klasörü.
     */
    public static final String DRAWABLE_KLASORU =
            "app/src/main/res/drawable";

    /**
     * Values klasörü.
     */
    public static final String VALUES_KLASORU =
            "app/src/main/res/values";

    /**
     * Android manifest dosya adı.
     */
    public static final String MANIFEST_DOSYASI =
            "AndroidManifest.xml";

    /**
     * Gradle dosya adı.
     */
    public static final String BUILD_GRADLE =
            "build.gradle";

    /**
     * Desteklenen Java uzantısı.
     */
    public static final String JAVA_UZANTISI =
            ".java";

    /**
     * Desteklenen Kotlin uzantısı.
     */
    public static final String KOTLIN_UZANTISI =
            ".kt";

    /**
     * Desteklenen XML uzantısı.
     */
    public static final String XML_UZANTISI =
            ".xml";

    /**
     * Desteklenen JSON uzantısı.
     */
    public static final String JSON_UZANTISI =
            ".json";

    /**
     * Varsayılan editör yazı boyutu.
     */
    public static final int EDITOR_YAZI_BOYUTU =
            14;

    /**
     * Maksimum dosya boyutu.
     */
    public static final long MAX_DOSYA_BOYUTU =
            2 * 1024 * 1024;

    /**
     * XML önizleme yenileme gecikmesi.
     */
    public static final long XML_ONIZLEME_GECIKME_MS =
            300;

    /**
     * Constructor engeli.
     */
    private ProjeSabitleri() {
        throw new IllegalStateException(
                "Utility class olusturulamaz."
        );
    }
}
