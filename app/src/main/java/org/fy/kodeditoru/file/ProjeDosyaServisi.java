package org.fy.kodeditoru.file;

import android.content.Context;

import org.fy.kodeditoru.core.ProjeSabitleri;
import org.fy.kodeditoru.proje.DosyaModeli;
import org.fy.kodeditoru.proje.ProjeModeli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Merkezi proje dosya yönetim servisi.
 *
 * Bu sınıf:
 * - Android/data uygulama alanında proje klasörlerini yönetir.
 * - proje listeler.
 * - proje açar ve oluşturur.
 * - proje içinde klasör oluşturur.
 * - proje içinde dosya oluşturur.
 * - dosya okur ve kaydeder.
 * - dosya siler.
 * - klasör siler.
 * - dosya/klasör yeniden adlandırır.
 * - dosya/klasör taşır.
 * - ana XML dosyasını bulur.
 * - Java dosyalarını runtime için birleştirir.
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
    private static final String VARSAYILAN_KLASOR_ADI = "YeniKlasor";

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

        klasorOlusturZorunlu(projelerKlasoru);
    }

    /**
     * Tüm projeleri listeler.
     */
    public List<ProjeModeli> projeleriListele() {

        List<ProjeModeli> sonuc =
                new ArrayList<>();

        File[] klasorler =
                projelerKlasoru.listFiles();

        if (klasorler == null) {
            return sonuc;
        }

        for (File klasor : klasorler) {

            if (!klasor.isDirectory()) {
                continue;
            }

            ProjeModeli proje =
                    new ProjeModeli(klasor);

            dosyalariModeleYukle(
                    proje,
                    klasor
            );

            sonuc.add(proje);
        }

        return sonuc;
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
                new ProjeModeli(projeKlasoru);

        dosyalariModeleYukle(
                proje,
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
                new ProjeModeli(projeKlasoru);

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
                "",
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
                "",
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
                "",
                dosyaAdi,
                DosyaModeli.TUR_KOTLIN,
                varsayilanKotlinKodu()
        );
    }

    /**
     * Hedef klasör içinde Java dosyası oluşturur.
     */
    public DosyaModeli javaDosyasiOlustur(
            ProjeModeli proje,
            String goreliKlasorYolu,
            String dosyaAdi
    ) throws IOException {

        return dosyaOlustur(
                proje,
                goreliKlasorYolu,
                dosyaAdi,
                DosyaModeli.TUR_JAVA,
                varsayilanJavaKodu()
        );
    }

    /**
     * Hedef klasör içinde XML dosyası oluşturur.
     */
    public DosyaModeli xmlDosyasiOlustur(
            ProjeModeli proje,
            String goreliKlasorYolu,
            String dosyaAdi
    ) throws IOException {

        return dosyaOlustur(
                proje,
                goreliKlasorYolu,
                dosyaAdi,
                DosyaModeli.TUR_XML,
                varsayilanXmlKodu()
        );
    }

    /**
     * Hedef klasör içinde Kotlin dosyası oluşturur.
     */
    public DosyaModeli kotlinDosyasiOlustur(
            ProjeModeli proje,
            String goreliKlasorYolu,
            String dosyaAdi
    ) throws IOException {

        return dosyaOlustur(
                proje,
                goreliKlasorYolu,
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

        File klasor =
                new File(
                        proje.getProjeKlasoru(),
                        goreliYolTemizle(goreliKlasorYolu)
                );

        klasorOlusturZorunlu(klasor);

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
            klasorOlusturZorunlu(ustKlasor);
        }

        try (
                BufferedWriter writer =
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        new FileOutputStream(hedefDosya, false),
                                        StandardCharsets.UTF_8
                                )
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

        if (!hedefDosya.exists() || !hedefDosya.isFile()) {
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
                        new BufferedReader(
                                new InputStreamReader(
                                        new FileInputStream(hedefDosya),
                                        StandardCharsets.UTF_8
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
     * Dosyayı siler.
     */
    public boolean dosyaSil(
            DosyaModeli dosya
    ) {

        if (dosya == null) {
            return false;
        }

        File hedef =
                dosya.getDosya();

        return hedef.exists()
                && hedef.isFile()
                && hedef.delete();
    }

    /**
     * Klasörü recursive siler.
     */
    public boolean klasorSil(
            File klasor
    ) {

        return recursiveSil(klasor);
    }

    /**
     * Dosya veya klasör adını değiştirir.
     */
    public File yenidenAdlandir(
            File hedef,
            String yeniAd
    ) throws IOException {

        if (hedef == null || !hedef.exists()) {
            throw new IOException(
                    "Yeniden adlandırılacak hedef bulunamadı."
            );
        }

        String temizAd =
                dosyaKlasorAdiTemizle(yeniAd);

        File yeniHedef =
                new File(
                        hedef.getParentFile(),
                        temizAd
                );

        if (yeniHedef.exists()) {
            throw new IOException(
                    "Aynı isimde dosya veya klasör zaten var."
            );
        }

        boolean basarili =
                hedef.renameTo(yeniHedef);

        if (!basarili) {
            throw new IOException(
                    "Yeniden adlandırma işlemi başarısız oldu."
            );
        }

        return yeniHedef;
    }

    /**
     * Dosya veya klasörü hedef klasöre taşır.
     */
    public File tasi(
            File kaynak,
            File hedefKlasor
    ) throws IOException {

        if (kaynak == null || !kaynak.exists()) {
            throw new IOException(
                    "Taşınacak hedef bulunamadı."
            );
        }

        if (hedefKlasor == null) {
            throw new IOException(
                    "Hedef klasör null olamaz."
            );
        }

        klasorOlusturZorunlu(hedefKlasor);

        File hedef =
                new File(
                        hedefKlasor,
                        kaynak.getName()
                );

        if (hedef.exists()) {
            throw new IOException(
                    "Hedef klasörde aynı isimde öğe var."
            );
        }

        boolean basarili =
                kaynak.renameTo(hedef);

        if (!basarili) {
            throw new IOException(
                    "Taşıma işlemi başarısız oldu."
            );
        }

        return hedef;
    }

    /**
     * Projenin ana XML dosyasını bulur.
     */
    public DosyaModeli anaXmlDosyasiBul(
            ProjeModeli proje
    ) {

        if (proje == null) {
            return null;
        }

        for (DosyaModeli dosya : proje.xmlDosyalari()) {

            if (ProjeSabitleri.VARSAYILAN_XML_DOSYASI.equalsIgnoreCase(
                    dosya.getAd()
            )) {
                return dosya;
            }
        }

        List<DosyaModeli> xmlDosyalari =
                proje.xmlDosyalari();

        if (xmlDosyalari.isEmpty()) {
            return null;
        }

        return xmlDosyalari.get(0);
    }

    /**
     * Projedeki Java dosyalarının içeriklerini birleştirir.
     */
    public String javaIcerikleriniBirlesir(
            ProjeModeli proje
    ) {

        if (proje == null) {
            return "";
        }

        StringBuilder builder =
                new StringBuilder();

        for (DosyaModeli dosya : proje.javaDosyalari()) {

            try {
                builder.append(dosyaOku(dosya));
                builder.append('\n');
            } catch (IOException ignored) {
            }
        }

        return builder.toString();
    }

    /**
     * Projedeki klasörleri listeler.
     */
    public List<File> klasorleriListele(
            ProjeModeli proje
    ) {

        List<File> sonuc =
                new ArrayList<>();

        if (proje == null) {
            return sonuc;
        }

        klasorleriTopla(
                proje.getProjeKlasoru(),
                sonuc
        );

        return sonuc;
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

        javaDosyasiOlustur(
                proje,
                ProjeSabitleri.VARSAYILAN_JAVA_DOSYASI
        );

        xmlDosyasiOlustur(
                proje,
                ProjeSabitleri.VARSAYILAN_XML_DOSYASI
        );
    }

    /**
     * Merkezi dosya oluşturma işlemi.
     */
    private DosyaModeli dosyaOlustur(
            ProjeModeli proje,
            String goreliKlasorYolu,
            String dosyaAdi,
            String tur,
            String varsayilanIcerik
    ) throws IOException {

        if (proje == null) {
            throw new IllegalArgumentException(
                    "Proje modeli null olamaz."
            );
        }

        File hedefKlasor =
                proje.getProjeKlasoru();

        if (goreliKlasorYolu != null
                && !goreliKlasorYolu.trim().isEmpty()) {

            hedefKlasor =
                    new File(
                            proje.getProjeKlasoru(),
                            goreliYolTemizle(goreliKlasorYolu)
                    );
        }

        klasorOlusturZorunlu(hedefKlasor);

        String temizDosyaAdi =
                dosyaAdiTemizle(
                        dosyaAdi,
                        tur
                );

        File dosya =
                new File(
                        hedefKlasor,
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
                dosyalariModeleYukle(proje, dosya);
                continue;
            }

            proje.dosyaEkle(
                    new DosyaModeli(dosya)
            );
        }
    }

    /**
     * Klasörleri recursive toplar.
     */
    private void klasorleriTopla(
            File kok,
            List<File> sonuc
    ) {

        if (kok == null || !kok.exists() || !kok.isDirectory()) {
            return;
        }

        sonuc.add(kok);

        File[] altOgeler =
                kok.listFiles();

        if (altOgeler == null) {
            return;
        }

        for (File oge : altOgeler) {

            if (oge.isDirectory()) {
                klasorleriTopla(oge, sonuc);
            }
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
     * Recursive silme yapar.
     */
    private boolean recursiveSil(
            File hedef
    ) {

        if (hedef == null || !hedef.exists()) {
            return false;
        }

        if (hedef.isDirectory()) {

            File[] altOgeler =
                    hedef.listFiles();

            if (altOgeler != null) {

                for (File altOge : altOgeler) {
                    recursiveSil(altOge);
                }
            }
        }

        return hedef.delete();
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

        return dosyaKlasorAdiTemizle(projeAdi);
    }

    /**
     * Göreli klasör yolunu güvenli hale getirir.
     */
    private String goreliYolTemizle(
            String yol
    ) {

        if (yol == null || yol.trim().isEmpty()) {
            return VARSAYILAN_KLASOR_ADI;
        }

        String[] parcalar =
                yol.trim().split("[/\\\\]+");

        StringBuilder temiz =
                new StringBuilder();

        for (String parca : parcalar) {

            String temizParca =
                    dosyaKlasorAdiTemizle(parca);

            if (temizParca.isEmpty()) {
                continue;
            }

            if (temiz.length() > 0) {
                temiz.append(File.separator);
            }

            temiz.append(temizParca);
        }

        if (temiz.length() == 0) {
            return VARSAYILAN_KLASOR_ADI;
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
            temizAd = temizAd + ProjeSabitleri.XML_UZANTISI;
        }

        if (DosyaModeli.TUR_JAVA.equals(tur)
                && !temizAd.toLowerCase().endsWith(
                        ProjeSabitleri.JAVA_UZANTISI
                )) {
            temizAd = temizAd + ProjeSabitleri.JAVA_UZANTISI;
        }

        if (DosyaModeli.TUR_KOTLIN.equals(tur)
                && !temizAd.toLowerCase().endsWith(
                        ProjeSabitleri.KOTLIN_UZANTISI
                )) {
            temizAd = temizAd + ProjeSabitleri.KOTLIN_UZANTISI;
        }

        return temizAd;
    }

    /**
     * Dosya/klasör adını güvenli hale getirir.
     */
    private String dosyaKlasorAdiTemizle(
            String ad
    ) {

        if (ad == null || ad.trim().isEmpty()) {
            return "";
        }

        return ad.trim()
                .replaceAll("[^a-zA-Z0-9_\\-.]", "_");
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
