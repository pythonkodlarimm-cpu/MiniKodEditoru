package org.fy.kodeditoru.file;

import android.content.Context;

import org.fy.kodeditoru.proje.DosyaModeli;
import org.fy.kodeditoru.proje.ProjeModeli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Merkezi proje dosya yönetim servisi.
 *
 * Bu sınıf:
 * - Android/data uygulama alanında proje klasörü oluşturur.
 * - Java, XML ve Kotlin dosyaları üretir.
 * - proje modeline dosya ekler.
 * - dosya içeriği okur.
 * - dosya içeriği kaydeder.
 * - örnek başlangıç proje dosyaları oluşturur.
 *
 * Kural:
 * - UI üretmez.
 * - önizleme oluşturmaz.
 * - thread başlatmaz.
 * - APK derlemez.
 * - XML render etmez.
 * - Activity yönetmez.
 */
public final class ProjeDosyaServisi {

    private static final String PROJELER_KLASORU_ADI = "projeler";

    private final File projelerKlasoru;

    /**
     * Proje dosya servisi oluşturur.
     */
    public ProjeDosyaServisi(
            Context context
    ) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context null olamaz."
            );
        }

        File anaKlasor =
                context.getExternalFilesDir(null);

        if (anaKlasor == null) {
            throw new IllegalStateException(
                    "Android external files alanı alınamadı."
            );
        }

        projelerKlasoru =
                new File(
                        anaKlasor,
                        PROJELER_KLASORU_ADI
                );

        klasorOlustur(projelerKlasoru);
    }

    /**
     * Yeni proje oluşturur.
     */
    public ProjeModeli projeOlustur(
            String projeAdi
    ) throws IOException {

        String temizAd =
                projeAdiTemizle(projeAdi);

        File projeKlasoru =
                new File(
                        projelerKlasoru,
                        temizAd
                );

        klasorOlustur(projeKlasoru);

        ProjeModeli proje =
                new ProjeModeli(
                        projeKlasoru
                );

        ornekDosyalariOlustur(proje);

        return proje;
    }

    /**
     * Proje klasörünü model olarak açar.
     */
    public ProjeModeli projeAc(
            String projeAdi
    ) {

        String temizAd =
                projeAdiTemizle(projeAdi);

        File projeKlasoru =
                new File(
                        projelerKlasoru,
                        temizAd
                );

        klasorOlustur(projeKlasoru);

        ProjeModeli proje =
                new ProjeModeli(
                        projeKlasoru
                );

        dosyalariModeleYukle(
                proje,
                projeKlasoru
        );

        return proje;
    }

    /**
     * Java dosyası oluşturur.
     */
    public DosyaModeli javaDosyasiOlustur(
            ProjeModeli proje,
            String dosyaAdi
    ) throws IOException {

        return dosyaOlustur(
                proje,
                dosyaAdi,
                DosyaModeli.TUR_JAVA,
                varsayilanJavaKodu()
        );
    }

    /**
     * XML dosyası oluşturur.
     */
    public DosyaModeli xmlDosyasiOlustur(
            ProjeModeli proje,
            String dosyaAdi
    ) throws IOException {

        return dosyaOlustur(
                proje,
                dosyaAdi,
                DosyaModeli.TUR_XML,
                varsayilanXmlKodu()
        );
    }

    /**
     * Kotlin dosyası oluşturur.
     */
    public DosyaModeli kotlinDosyasiOlustur(
            ProjeModeli proje,
            String dosyaAdi
    ) throws IOException {

        return dosyaOlustur(
                proje,
                dosyaAdi,
                DosyaModeli.TUR_KOTLIN,
                varsayilanKotlinKodu()
        );
    }

    /**
     * Dosya içeriğini kaydeder.
     */
    public void dosyaKaydet(
            DosyaModeli dosya,
            String icerik
    ) throws IOException {

        if (dosya == null) {
            throw new IllegalArgumentException(
                    "Dosya modeli null olamaz."
            );
        }

        File hedefDosya =
                dosya.getDosya();

        File ustKlasor =
                hedefDosya.getParentFile();

        if (ustKlasor != null) {
            klasorOlustur(ustKlasor);
        }

        try (
                FileWriter writer =
                        new FileWriter(
                                hedefDosya,
                                false
                        )
        ) {

            writer.write(
                    icerik == null ? "" : icerik
            );

            writer.flush();
        }
    }

    /**
     * Dosya içeriğini okur.
     */
    public String dosyaOku(
            DosyaModeli dosya
    ) throws IOException {

        if (dosya == null) {
            throw new IllegalArgumentException(
                    "Dosya modeli null olamaz."
            );
        }

        File hedefDosya =
                dosya.getDosya();

        if (!hedefDosya.exists()) {
            return "";
        }

        StringBuilder builder =
                new StringBuilder();

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(
                                        hedefDosya
                                )
                        )
        ) {

            String satir;

            while ((satir = reader.readLine()) != null) {
                builder.append(satir);
                builder.append('\n');
            }
        }

        return builder.toString();
    }

    /**
     * Projeler ana klasörünü döndürür.
     */
    public File getProjelerKlasoru() {
        return projelerKlasoru;
    }

    /**
     * Örnek başlangıç dosyalarını oluşturur.
     */
    private void ornekDosyalariOlustur(
            ProjeModeli proje
    ) throws IOException {

        DosyaModeli javaDosya =
                javaDosyasiOlustur(
                        proje,
                        "MainActivity.java"
                );

        dosyaKaydet(
                javaDosya,
                varsayilanJavaKodu()
        );

        DosyaModeli xmlDosya =
                xmlDosyasiOlustur(
                        proje,
                        "activity_main.xml"
                );

        dosyaKaydet(
                xmlDosya,
                varsayilanXmlKodu()
        );
    }

    /**
     * Merkezi dosya oluşturma işlemi.
     */
    private DosyaModeli dosyaOlustur(
            ProjeModeli proje,
            String dosyaAdi,
            String tur,
            String varsayilanIcerik
    ) throws IOException {

        if (proje == null) {
            throw new IllegalArgumentException(
                    "Proje modeli null olamaz."
            );
        }

        String temizDosyaAdi =
                dosyaAdiTemizle(
                        dosyaAdi,
                        tur
                );

        File dosya =
                new File(
                        proje.getProjeKlasoru(),
                        temizDosyaAdi
                );

        DosyaModeli model =
                new DosyaModeli(
                        dosya,
                        tur
                );

        if (!dosya.exists()) {
            dosyaKaydet(
                    model,
                    varsayilanIcerik
            );
        }

        proje.dosyaEkle(model);

        return model;
    }

    /**
     * Projedeki dosyaları modele yükler.
     */
    private void dosyalariModeleYukle(
            ProjeModeli proje,
            File klasor
    ) {

        File[] dosyalar =
                klasor.listFiles();

        if (dosyalar == null) {
            return;
        }

        for (File dosya : dosyalar) {

            if (dosya.isDirectory()) {
                dosyalariModeleYukle(
                        proje,
                        dosya
                );
                continue;
            }

            proje.dosyaEkle(
                    new DosyaModeli(
                            dosya
                    )
            );
        }
    }

    /**
     * Klasör yoksa oluşturur.
     */
    private void klasorOlustur(
            File klasor
    ) {

        if (klasor == null) {
            return;
        }

        if (!klasor.exists()) {
            //noinspection ResultOfMethodCallIgnored
            klasor.mkdirs();
        }
    }

    /**
     * Proje adını güvenli hale getirir.
     */
    private String projeAdiTemizle(
            String projeAdi
    ) {

        if (projeAdi == null || projeAdi.trim().isEmpty()) {
            return "YeniProje";
        }

        return projeAdi
                .trim()
                .replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    /**
     * Dosya adını güvenli hale getirir.
     */
    private String dosyaAdiTemizle(
            String dosyaAdi,
            String tur
    ) {

        String temizAd =
                dosyaAdi == null ? "" : dosyaAdi.trim();

        if (temizAd.isEmpty()) {

            if (DosyaModeli.TUR_XML.equals(tur)) {
                temizAd = "activity_main.xml";
            } else if (DosyaModeli.TUR_KOTLIN.equals(tur)) {
                temizAd = "MainActivity.kt";
            } else {
                temizAd = "MainActivity.java";
            }
        }

        temizAd =
                temizAd.replaceAll("[/\\\\:*?\"<>|]", "_");

        if (DosyaModeli.TUR_XML.equals(tur)
                && !temizAd.toLowerCase().endsWith(".xml")) {

            temizAd = temizAd + ".xml";
        }

        if (DosyaModeli.TUR_JAVA.equals(tur)
                && !temizAd.toLowerCase().endsWith(".java")) {

            temizAd = temizAd + ".java";
        }

        if (DosyaModeli.TUR_KOTLIN.equals(tur)
                && !temizAd.toLowerCase().endsWith(".kt")) {

            temizAd = temizAd + ".kt";
        }

        return temizAd;
    }

    /**
     * Varsayılan Java kodu üretir.
     */
    private String varsayilanJavaKodu() {

        return ""
                + "package org.fy.test;\n\n"
                + "public class MainActivity {\n\n"
                + "    public void baslat() {\n"
                + "    }\n"
                + "}\n";
    }

    /**
     * Varsayılan Kotlin kodu üretir.
     */
    private String varsayilanKotlinKodu() {

        return ""
                + "package org.fy.test\n\n"
                + "class MainActivity {\n\n"
                + "    fun baslat() {\n"
                + "    }\n"
                + "}\n";
    }

    /**
     * Varsayılan XML kodu üretir.
     */
    private String varsayilanXmlKodu() {

        return ""
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\n"
                + "<LinearLayout\n"
                + "    xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"
                + "    android:layout_width=\"match_parent\"\n"
                + "    android:layout_height=\"match_parent\"\n"
                + "    android:orientation=\"vertical\"\n"
                + "    android:padding=\"16dp\"\n"
                + "    android:background=\"#101820\">\n\n"
                + "    <TextView\n"
                + "        android:layout_width=\"wrap_content\"\n"
                + "        android:layout_height=\"wrap_content\"\n"
                + "        android:text=\"Mini Kod Editörü\"\n"
                + "        android:textSize=\"24sp\"\n"
                + "        android:textColor=\"#FFFFFF\" />\n\n"
                + "    <Button\n"
                + "        android:layout_width=\"match_parent\"\n"
                + "        android:layout_height=\"wrap_content\"\n"
                + "        android:text=\"Buton Test\" />\n\n"
                + "</LinearLayout>\n";
    }
            }
