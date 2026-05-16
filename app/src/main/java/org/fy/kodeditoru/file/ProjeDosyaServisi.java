package org.fy.kodeditoru.file;

import android.content.Context;

import org.fy.kodeditoru.proje.DosyaModeli;
import org.fy.kodeditoru.proje.ProjeModeli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Merkezi proje dosya yönetim servisi.
 *
 * Bu sınıf:
 * - proje klasörü oluşturur.
 * - Java/XML/Kotlin dosyaları üretir.
 * - proje modeline dosya ekler.
 * - örnek başlangıç dosyaları oluşturur.
 * - aktif çalışma klasörünü yönetir.
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

    private final Context context;

    private final File projelerKlasoru;

    /**
     * Servis oluşturur.
     */
    public ProjeDosyaServisi(
            Context context
    ) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context null olamaz."
            );
        }

        this.context = context;

        projelerKlasoru =
                new File(
                        context.getExternalFilesDir(null),
                        "projeler"
                );

        if (!projelerKlasoru.exists()) {
            projelerKlasoru.mkdirs();
        }
    }

    /**
     * Yeni proje oluşturur.
     */
    public ProjeModeli projeOlustur(
            String projeAdi
    ) throws IOException {

        String temizAd =
                projeAdi
                        .trim()
                        .replace(" ", "_");

        File projeKlasoru =
                new File(
                        projelerKlasoru,
                        temizAd
                );

        if (!projeKlasoru.exists()) {
            projeKlasoru.mkdirs();
        }

        ProjeModeli proje =
                new ProjeModeli(
                        projeKlasoru
                );

        ornekDosyalariOlustur(proje);

        return proje;
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
     * Java dosyası oluşturur.
     */
    public DosyaModeli javaDosyasiOlustur(
            ProjeModeli proje,
            String dosyaAdi
    ) {

        return dosyaOlustur(
                proje,
                dosyaAdi,
                DosyaModeli.TUR_JAVA
        );
    }

    /**
     * XML dosyası oluşturur.
     */
    public DosyaModeli xmlDosyasiOlustur(
            ProjeModeli proje,
            String dosyaAdi
    ) {

        return dosyaOlustur(
                proje,
                dosyaAdi,
                DosyaModeli.TUR_XML
        );
    }

    /**
     * Kotlin dosyası oluşturur.
     */
    public DosyaModeli kotlinDosyasiOlustur(
            ProjeModeli proje,
            String dosyaAdi
    ) {

        return dosyaOlustur(
                proje,
                dosyaAdi,
                DosyaModeli.TUR_KOTLIN
        );
    }

    /**
     * Merkezi dosya oluşturma işlemi.
     */
    private DosyaModeli dosyaOlustur(
            ProjeModeli proje,
            String dosyaAdi,
            String tur
    ) {

        File dosya =
                new File(
                        proje.getProjeKlasoru(),
                        dosyaAdi
                );

        DosyaModeli model =
                new DosyaModeli(
                        dosya,
                        tur
                );

        proje.dosyaEkle(model);

        return model;
    }

    /**
     * Dosya içeriğini kaydeder.
     */
    public void dosyaKaydet(
            DosyaModeli dosya,
            String icerik
    ) throws IOException {

        FileWriter writer =
                new FileWriter(
                        dosya.getDosya()
                );

        writer.write(icerik);
        writer.flush();
        writer.close();
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
     * Varsayılan XML kodu üretir.
     */
    private String varsayilanXmlKodu() {

        return ""
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<LinearLayout\n"
                + "    xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"
                + "    android:layout_width=\"match_parent\"\n"
                + "    android:layout_height=\"match_parent\"\n"
                + "    android:orientation=\"vertical\">\n\n"
                + "    <TextView\n"
                + "        android:layout_width=\"wrap_content\"\n"
                + "        android:layout_height=\"wrap_content\"\n"
                + "        android:text=\"Mini Kod Editörü\"/>\n\n"
                + "</LinearLayout>\n";
    }
      }
