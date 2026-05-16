package org.fy.kodeditoru.core;

/**
 * Kod editörü uygulaması merkezi proje sabitleri.
 *
 * Bu sınıf:
 * - uygulama sabitlerini tek merkezde tutar.
 * - Android/data altındaki proje ve log klasör adlarını tanımlar.
 * - desteklenen dosya türlerini standartlaştırır.
 * - desteklenen dosya uzantılarını tanımlar.
 * - Android proje klasör yollarını tek merkezden yönetir.
 * - editör ve önizleme varsayılanlarını standartlaştırır.
 *
 * Kural:
 * - state tutmaz.
 * - Context taşımaz.
 * - UI üretmez.
 * - dosya işlemi yapmaz.
 * - iş mantığı çalıştırmaz.
 */
public final class ProjeSabitleri {

    /**
     * Uygulama adı.
     */
    public static final String UYGULAMA_ADI =
            "Mini Kod Editörü";

    /**
     * Android/data altında tutulacak ana proje klasörü.
     */
    public static final String PROJELER_KLASORU =
            "projeler";

    /**
     * Android/data altında tutulacak log klasörü.
     */
    public static final String LOG_KLASORU =
            "loglar";

    /**
     * Ana log dosya adı.
     */
    public static final String DEBUG_LOG_DOSYASI =
            "debug.log";

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
     * Android manifest göreli dosya yolu.
     */
    public static final String MANIFEST_DOSYASI =
            "app/src/main/AndroidManifest.xml";

    /**
     * Root Gradle dosya adı.
     */
    public static final String ROOT_BUILD_GRADLE =
            "build.gradle";

    /**
     * App Gradle göreli dosya yolu.
     */
    public static final String APP_BUILD_GRADLE =
            "app/build.gradle";

    /**
     * Settings Gradle dosya adı.
     */
    public static final String SETTINGS_GRADLE =
            "settings.gradle";

    /**
     * Gradle properties dosya adı.
     */
    public static final String GRADLE_PROPERTIES =
            "gradle.properties";

    /**
     * ProGuard rules göreli dosya yolu.
     */
    public static final String PROGUARD_RULES =
            "app/proguard-rules.pro";

    /**
     * Java dosya türü.
     */
    public static final String TUR_JAVA =
            "java";

    /**
     * Kotlin dosya türü.
     */
    public static final String TUR_KOTLIN =
            "kotlin";

    /**
     * XML dosya türü.
     */
    public static final String TUR_XML =
            "xml";

    /**
     * JSON dosya türü.
     */
    public static final String TUR_JSON =
            "json";

    /**
     * Bilinmeyen dosya türü.
     */
    public static final String TUR_BILINMEYEN =
            "bilinmeyen";

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
     * Varsayılan Java dosyası.
     */
    public static final String VARSAYILAN_JAVA_DOSYASI =
            "MainActivity.java";

    /**
     * Varsayılan Kotlin dosyası.
     */
    public static final String VARSAYILAN_KOTLIN_DOSYASI =
            "MainActivity.kt";

    /**
     * Varsayılan XML layout dosyası.
     */
    public static final String VARSAYILAN_XML_DOSYASI =
            "activity_main.xml";

    /**
     * Varsayılan editör yazı boyutu.
     */
    public static final int EDITOR_YAZI_BOYUTU =
            14;

    /**
     * Maksimum dosya boyutu.
     */
    public static final long MAX_DOSYA_BOYUTU =
            2L * 1024L * 1024L;

    /**
     * XML önizleme yenileme gecikmesi.
     */
    public static final long XML_ONIZLEME_GECIKME_MS =
            300L;

    /**
     * Constructor engeli.
     */
    private ProjeSabitleri() {

        throw new IllegalStateException(
                "Utility class oluşturulamaz."
        );
    }
}
