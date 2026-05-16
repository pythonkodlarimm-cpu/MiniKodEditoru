package org.fy.kodeditoru.runtime;

/**
 * Runtime geçici veri deposu.
 *
 * Bu sınıf:
 * - editörden gelen XML içeriğini geçici olarak tutar.
 * - editörden gelen Java içeriğini geçici olarak tutar.
 * - runtime ekranı açıldığında veriyi sağlar.
 * - runtime ekranı kapanınca temizlenebilir.
 *
 * Kural:
 * - UI üretmez.
 * - dosya okuma/yazma yapmaz.
 * - APK derlemez.
 * - Java kodu gerçek JVM gibi çalıştırmaz.
 * - sadece geçici runtime verisi taşır.
 */
public final class RuntimeVeriDeposu {

    private static String xmlIcerik = "";
    private static String javaIcerik = "";
    private static String aktifDosyaAdi = "";

    /**
     * Constructor engeli.
     */
    private RuntimeVeriDeposu() {

        throw new IllegalStateException(
                "Utility class oluşturulamaz."
        );
    }

    /**
     * Runtime verisini kaydeder.
     */
    public static void veriAyarla(
            String xml,
            String java,
            String dosyaAdi
    ) {

        xmlIcerik =
                xml == null
                        ? ""
                        : xml;

        javaIcerik =
                java == null
                        ? ""
                        : java;

        aktifDosyaAdi =
                dosyaAdi == null
                        ? ""
                        : dosyaAdi;
    }

    /**
     * XML içeriğini döndürür.
     */
    public static String xmlIcerikGetir() {
        return xmlIcerik;
    }

    /**
     * Java içeriğini döndürür.
     */
    public static String javaIcerikGetir() {
        return javaIcerik;
    }

    /**
     * Aktif dosya adını döndürür.
     */
    public static String aktifDosyaAdiGetir() {
        return aktifDosyaAdi;
    }

    /**
     * XML içeriği var mı döndürür.
     */
    public static boolean xmlVarMi() {

        return xmlIcerik != null
                && !xmlIcerik.trim().isEmpty();
    }

    /**
     * Java içeriği var mı döndürür.
     */
    public static boolean javaVarMi() {

        return javaIcerik != null
                && !javaIcerik.trim().isEmpty();
    }

    /**
     * Runtime verisini temizler.
     */
    public static void temizle() {

        xmlIcerik = "";
        javaIcerik = "";
        aktifDosyaAdi = "";
    }
}
