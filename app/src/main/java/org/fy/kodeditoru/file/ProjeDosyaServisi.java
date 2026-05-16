package org.fy.kodeditoru.file;

import android.content.Context;

import org.fy.kodeditoru.core.ProjeSabitleri;
import org.fy.kodeditoru.proje.DosyaModeli;
import org.fy.kodeditoru.proje.ProjeModeli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Merkezi proje dosya yönetim servisi.
 *
 * Bu sınıf:
 * - Android/data uygulama alanında proje klasörü oluşturur.
 * - proje içinde klasör oluşturur.
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
                        ProjeSabitleri.PROJELER_KLASORU
                );

        klasorOlusturZorunlu(projelerKlasoru);
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

        klasorOlusturZorunlu(projeKlasoru);

        ProjeModeli proje =
                new ProjeModeli(
                        projeKlasoru
                );

        if (proje.getDosyalar().isEmpty()) {
            ornekDosyalariOlustur(proje);
        }

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

        klasorOlusturZorunlu(projeKlasoru);

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
     * Proje içinde klasör oluşturur.
     */
    public File klasorOlustur(
            ProjeModeli proje,
            String goreliKlasorYolu
    ) throws IOException {

        if (proje == null) {
            throw new IllegalArgumentException(
                    "Proje modeli null olamaz."
            );
        }

        String temizYol =
                goreliYolTemizle(
                        goreliKlasorYolu
                );

        File klasor =
                new File(
                        proje.getProjeKlasoru(),
                        temizYol
                );

        klasorOlusturZorunlu(
                klasor
        );

        return klasor;
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
            klasorOlusturZorunlu(
                    ustKlasor
            );
        }

        try (
                BufferedWriter writer =
                        Files.newBufferedWriter(
                                hedefDosya.toPath(),
                                StandardCharsets.UTF_8
                        )
        ) {

            writer.write(
                    icerik == null
                            ? ""
                            : icerik
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

        if (!hedefDosya.isFile()) {
            return "";
        }

        if (hedefDosya.length() > ProjeSabitleri.MAX_DOSYA_BOYUTU) {
            throw new IOException(
                    "Dosya boyutu izin verilen sınırı aşıyor."
            );
        }

        StringBuilder builder =
                new StringBuilder();

        try (
                BufferedReader reader =
                        Files.newBufferedReader(
                                hedefDosya.toPath(),
                                StandardCharsets.UTF_8
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
                        ProjeSabitleri.VARSAYILAN_JAVA_DOSYASI
                );

        dosyaKaydet(
                javaDosya,
                varsayilanJavaKodu()
        );

        DosyaModeli xmlDosya =
                xmlDosyasiOlustur(
                        proje,
                        ProjeSabitleri.VARSAYILAN_XML_DOSYASI
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

        proje.dosyaEkle(
                model
        );

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
    private void klasorOlusturZorunlu(
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
            return ProjeSabitleri.VARSAYILAN_PROJE_ADI;
        }

        return projeAdi
                .trim()
                .replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    /**
     * Göreli klasör yolunu güvenli hale getirir.
     */
    private String goreliYolTemizle(
            String yol
    ) {

        if (yol == null || yol.trim().isEmpty()) {
            return "YeniKlasor";
        }

        String[] parcalar =
                yol.trim()
                        .split("[/\\\\]+");

        StringBuilder temiz =
                new StringBuilder();

        for (String parca : parcalar) {

            String temizParca =
                    parca.trim()
                            .replaceAll("[^a-zA-Z0-9_\\-]", "_");

            if (temizParca.isEmpty()) {
                continue;
            }

            if (temiz.length() > 0) {
                temiz.append(File.separator);
            }

            temiz.append(temizParca);
        }

        if (temiz.length() == 0) {
            return "YeniKlasor";
        }

        return temiz.toString();
    }

    /**
     * Dosya adını güvenli hale getirir.
     */
    private String dosyaAdiTemizle(
            String dosyaAdi,
            String tur
    ) {

        String temizAd =
                dosyaAdi == null
                        ? ""
                        : dosyaAdi.trim();

        if (temizAd.isEmpty()) {

            if (DosyaModeli.TUR_XML.equals(tur)) {
                temizAd = ProjeSabitleri.VARSAYILAN_XML_DOSYASI;
            } else if (DosyaModeli.TUR_KOTLIN.equals(tur)) {
                temizAd = ProjeSabitleri.VARSAYILAN_KOTLIN_DOSYASI;
            } else {
                temizAd = ProjeSabitleri.VARSAYILAN_JAVA_DOSYASI;
            }
        }

        temizAd =
                temizAd.replaceAll(
                        "[/\\\\:*?\"<>|]",
                        "_"
                );

        if (DosyaModeli.TUR_XML.equals(tur)
                && !temizAd.toLowerCase().endsWith(
                        ProjeSabitleri.XML_UZANTISI
                )) {

            temizAd =
                    temizAd
                            + ProjeSabitleri.XML_UZANTISI;
        }

        if (DosyaModeli.TUR_JAVA.equals(tur)
                && !temizAd.toLowerCase().endsWith(
                        ProjeSabitleri.JAVA_UZANTISI
                )) {

            temizAd =
                    temizAd
                            + ProjeSabitleri.JAVA_UZANTISI;
        }

        if (DosyaModeli.TUR_KOTLIN.equals(tur)
                && !temizAd.toLowerCase().endsWith(
                        ProjeSabitleri.KOTLIN_UZANTISI
                )) {

            temizAd =
                    temizAd
                            + ProjeSabitleri.KOTLIN_UZANTISI;
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
                + "        android:id=\"@+id/baslikMetni\"\n"
                + "        android:layout_width=\"wrap_content\"\n"
                + "        android:layout_height=\"wrap_content\"\n"
                + "        android:text=\"Mini Kod Editörü\"\n"
                + "        android:textSize=\"24sp\"\n"
                + "        android:textColor=\"#FFFFFF\" />\n\n"
                + "    <Button\n"
                + "        android:id=\"@+id/testButonu\"\n"
                + "        android:layout_width=\"match_parent\"\n"
                + "        android:layout_height=\"wrap_content\"\n"
                + "        android:text=\"Buton Test\" />\n\n"
                + "</LinearLayout>\n";
    }
    }
