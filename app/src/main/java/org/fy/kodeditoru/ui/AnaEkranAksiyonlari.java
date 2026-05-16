package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import org.fy.kodeditoru.core.ProjeSabitleri;
import org.fy.kodeditoru.editor.EditorYoneticisi;
import org.fy.kodeditoru.file.ProjeDosyaServisi;
import org.fy.kodeditoru.preview.XmlOnizlemeYoneticisi;
import org.fy.kodeditoru.proje.DosyaModeli;
import org.fy.kodeditoru.proje.ProjeModeli;
import org.fy.kodeditoru.runtime.RuntimeEkranActivity;
import org.fy.kodeditoru.runtime.RuntimeVeriDeposu;

import java.io.IOException;

/**
 * Ana ekran aksiyonları modülü.
 *
 * Bu sınıf:
 * - aktif proje durumunu yönetir.
 * - aktif dosya durumunu yönetir.
 * - dosya oluşturur.
 * - dosya kaydeder.
 * - kod kontrol işlemini yürütür.
 * - seçili projeyi runtime ekranında çalıştırır.
 *
 * Kural:
 * - ana ekran XML bileşeni bağlamaz.
 * - editör araç çubuğu işlemi yapmaz.
 * - proje seçim penceresi üretmez.
 * - yeni öğe penceresi üretmez.
 * - hata ekranı üretimini RuntimeHataEkrani modülüne devreder.
 * - APK derlemez.
 */
public final class AnaEkranAksiyonlari {

    /**
     * Ana ekran durum güncelleme geri bildirimi.
     */
    public interface DurumDinleyici {

        /**
         * Aktif proje veya aktif dosya değiştiğinde çağrılır.
         */
        void durumGuncellendi(
                ProjeModeli aktifProje,
                DosyaModeli aktifDosya,
                String rozetMetni
        );
    }

    private final Activity activity;
    private final ProjeDosyaServisi projeDosyaServisi;
    private final EditorYoneticisi editorYoneticisi;
    private final XmlOnizlemeYoneticisi xmlOnizlemeYoneticisi;
    private final RuntimeHataEkrani runtimeHataEkrani;
    private final DurumDinleyici durumDinleyici;

    private ProjeModeli aktifProje;
    private DosyaModeli aktifDosya;

    /**
     * Ana ekran aksiyonları oluşturur.
     */
    public AnaEkranAksiyonlari(
            Activity activity,
            ProjeDosyaServisi projeDosyaServisi,
            EditorYoneticisi editorYoneticisi,
            XmlOnizlemeYoneticisi xmlOnizlemeYoneticisi,
            RuntimeHataEkrani runtimeHataEkrani,
            DurumDinleyici durumDinleyici
    ) {

        if (activity == null) {
            throw new IllegalArgumentException("Activity null olamaz.");
        }

        if (projeDosyaServisi == null) {
            throw new IllegalArgumentException("ProjeDosyaServisi null olamaz.");
        }

        if (editorYoneticisi == null) {
            throw new IllegalArgumentException("EditorYoneticisi null olamaz.");
        }

        if (xmlOnizlemeYoneticisi == null) {
            throw new IllegalArgumentException("XmlOnizlemeYoneticisi null olamaz.");
        }

        if (runtimeHataEkrani == null) {
            throw new IllegalArgumentException("RuntimeHataEkrani null olamaz.");
        }

        this.activity = activity;
        this.projeDosyaServisi = projeDosyaServisi;
        this.editorYoneticisi = editorYoneticisi;
        this.xmlOnizlemeYoneticisi = xmlOnizlemeYoneticisi;
        this.runtimeHataEkrani = runtimeHataEkrani;
        this.durumDinleyici = durumDinleyici;
    }

    /**
     * Varsayılan projeyi hazırlar.
     */
    public void varsayilanProjeyiHazirla() {

        try {

            aktifProje =
                    projeDosyaServisi.projeAc(
                            ProjeSabitleri.VARSAYILAN_PROJE_ADI
                    );

            if (aktifProje.getDosyalar().isEmpty()) {
                aktifProje =
                        projeDosyaServisi.projeOlustur(
                                ProjeSabitleri.VARSAYILAN_PROJE_ADI
                        );
            }

            aktifXmlDosyasiniSec();
            aktifDosyayiEditoreYukle();
            durumYayinla("Hazır");

        } catch (IOException hata) {

            runtimeHataEkrani.goster(
                    "Proje hazırlanamadı",
                    "Varsayılan proje açılırken hata oluştu.",
                    hata.getMessage()
            );
        }
    }

    /**
     * Seçilen projeyi aktif proje yapar.
     */
    public void projeSec(
            ProjeModeli proje
    ) {

        if (proje == null) {
            mesajGoster("Proje seçilemedi.");
            return;
        }

        aktifProje = proje;
        aktifXmlDosyasiniSec();

        try {

            aktifDosyayiEditoreYukle();
            durumYayinla("Proje");
            mesajGoster("Seçili proje: " + aktifProje.getProjeAdi());

        } catch (IOException hata) {

            runtimeHataEkrani.goster(
                    "Proje seçildi ama dosya açılamadı",
                    "Seçilen projenin ilk dosyası editöre yüklenemedi.",
                    hata.getMessage()
            );
        }
    }

    /**
     * Seçilen dosyayı aktif dosya yapar.
     */
    public void dosyaSec(
            DosyaModeli dosya
    ) {

        if (dosya == null) {
            mesajGoster("Dosya seçilemedi.");
            return;
        }

        aktifDosya = dosya;

        if (aktifProje != null) {
            aktifProje.aktifDosyaAyarla(dosya);
        }

        try {

            aktifDosyayiEditoreYukle();
            durumYayinla("Dosya");
            mesajGoster("Açılan dosya: " + dosya.getAd());

        } catch (IOException hata) {

            runtimeHataEkrani.goster(
                    "Dosya açılamadı",
                    "Seçilen dosya editöre yüklenemedi.",
                    hata.getMessage()
            );
        }
    }

    /**
     * Yeni dosya oluşturur.
     */
    public void yeniDosyaOlustur(
            String girilenAd,
            int secilenTurId
    ) {

        try {

            aktifProjeyiGarantiEt();

            String dosyaAdi =
                    girilenAd == null || girilenAd.trim().isEmpty()
                            ? otomatikDosyaAdiUret(secilenTurId)
                            : girilenAd.trim();

            if (secilenTurId == YeniOgeEkrani.TUR_JAVA_ID) {
                aktifDosya =
                        projeDosyaServisi.javaDosyasiOlustur(
                                aktifProje,
                                dosyaAdi
                        );
            } else if (secilenTurId == YeniOgeEkrani.TUR_KOTLIN_ID) {
                aktifDosya =
                        projeDosyaServisi.kotlinDosyasiOlustur(
                                aktifProje,
                                dosyaAdi
                        );
            } else {
                aktifDosya =
                        projeDosyaServisi.xmlDosyasiOlustur(
                                aktifProje,
                                dosyaAdi
                        );
            }

            aktifProje.aktifDosyaAyarla(aktifDosya);
            aktifDosyayiEditoreYukle();
            durumYayinla("Yeni");

            mesajGoster("Dosya oluşturuldu: " + aktifDosya.getAd());

        } catch (IOException hata) {

            runtimeHataEkrani.goster(
                    "Yeni dosya oluşturulamadı",
                    "Dosya oluşturma işlemi tamamlanamadı.",
                    hata.getMessage()
            );
        }
    }

    /**
     * Aktif dosyayı kaydeder.
     */
    public void kaydet() {

        String icerik =
                editorYoneticisi.icerikGetir();

        if (icerik.trim().isEmpty()) {
            mesajGoster("Kaydedilecek kod yok.");
            return;
        }

        if (aktifDosya == null) {
            mesajGoster("Aktif dosya yok.");
            return;
        }

        try {

            projeDosyaServisi.dosyaKaydet(
                    aktifDosya,
                    icerik
            );

            durumYayinla("Kaydedildi");
            mesajGoster("Kaydedildi: " + aktifDosya.getAd());

        } catch (IOException hata) {

            runtimeHataEkrani.goster(
                    "Dosya kaydedilemedi",
                    "Aktif dosya kayıt sırasında hata verdi.",
                    hata.getMessage()
            );
        }
    }

    /**
     * Aktif kodu hızlı kontrol eder.
     */
    public void koduKontrolEt() {

        String icerik =
                editorYoneticisi.icerikGetir();

        if (icerik.trim().isEmpty()) {
            mesajGoster("Kontrol edilecek içerik yok.");
            return;
        }

        if (aktifDosya != null && aktifDosya.isXml()) {
            xmlOnizlemeYoneticisi.onizlemeGuncelle(icerik);
            durumYayinla("XML uygun");
            mesajGoster("XML temel kontrol tamamlandı.");
            return;
        }

        durumYayinla("Kontrol");
        mesajGoster("Dosya kontrol edildi.");
    }

    /**
     * Seçili projeyi runtime ekranında çalıştırır.
     */
    public void calistir() {

        if (aktifProje == null) {
            runtimeHataEkrani.goster(
                    "Proje çalıştırılamadı",
                    "Çalıştırmak için önce bir proje seçmelisin.",
                    "Aktif proje null."
            );
            return;
        }

        try {

            aktifDosyayiKaydetmedenOnceSakla();

            DosyaModeli anaXml =
                    projeDosyaServisi.anaXmlDosyasiBul(
                            aktifProje
                    );

            if (anaXml == null) {
                runtimeHataEkrani.goster(
                        "Proje çalıştırılamadı",
                        "Projede çalıştırılacak XML dosyası bulunamadı.",
                        "Aranan öncelik: "
                                + ProjeSabitleri.VARSAYILAN_XML_DOSYASI
                );
                return;
            }

            String xmlIcerik =
                    projeDosyaServisi.dosyaOku(
                            anaXml
                    );

            if (xmlIcerik.trim().isEmpty()) {
                runtimeHataEkrani.goster(
                        "Proje çalıştırılamadı",
                        "Ana XML dosyası boş.",
                        "Dosya: " + anaXml.getTamYol()
                );
                return;
            }

            RuntimeVeriDeposu.veriAyarla(
                    xmlIcerik,
                    projeDosyaServisi.javaIcerikleriniBirlesir(aktifProje),
                    anaXml.getAd()
            );

            activity.startActivity(
                    new Intent(
                            activity,
                            RuntimeEkranActivity.class
                    )
            );

            durumYayinla("Çalışıyor");
            mesajGoster("Proje çalıştırılıyor: " + aktifProje.getProjeAdi());

        } catch (IOException hata) {

            runtimeHataEkrani.goster(
                    "Proje çalıştırılamadı",
                    "Runtime başlatma sırasında hata oluştu.",
                    hata.getMessage()
            );
        }
    }

    /**
     * Aktif projeyi döndürür.
     */
    public ProjeModeli getAktifProje() {
        return aktifProje;
    }

    /**
     * Aktif dosyayı döndürür.
     */
    public DosyaModeli getAktifDosya() {
        return aktifDosya;
    }

    /**
     * Aktif XML dosyasını seçer.
     */
    private void aktifXmlDosyasiniSec() {

        if (aktifProje == null) {
            return;
        }

        DosyaModeli anaXml =
                projeDosyaServisi.anaXmlDosyasiBul(
                        aktifProje
                );

        if (anaXml != null) {
            aktifDosya = anaXml;
            aktifProje.aktifDosyaAyarla(aktifDosya);
            return;
        }

        if (!aktifProje.getDosyalar().isEmpty()) {
            aktifDosya = aktifProje.getDosyalar().get(0);
            aktifProje.aktifDosyaAyarla(aktifDosya);
        }
    }

    /**
     * Aktif dosyayı editöre yükler.
     */
    private void aktifDosyayiEditoreYukle()
            throws IOException {

        if (aktifDosya == null) {
            return;
        }

        String icerik =
                projeDosyaServisi.dosyaOku(
                        aktifDosya
                );

        editorYoneticisi.icerikAyarla(icerik);

        if (aktifDosya.isXml()) {
            xmlOnizlemeYoneticisi.onizlemeGuncelle(icerik);
            return;
        }

        xmlOnizlemeYoneticisi.onizlemeTemizle();
    }

    /**
     * Aktif editör içeriğini çalıştırmadan önce dosyaya kaydeder.
     */
    private void aktifDosyayiKaydetmedenOnceSakla()
            throws IOException {

        if (aktifDosya == null) {
            return;
        }

        projeDosyaServisi.dosyaKaydet(
                aktifDosya,
                editorYoneticisi.icerikGetir()
        );
    }

    /**
     * Aktif projeyi garanti eder.
     */
    private void aktifProjeyiGarantiEt()
            throws IOException {

        if (aktifProje != null) {
            return;
        }

        aktifProje =
                projeDosyaServisi.projeOlustur(
                        ProjeSabitleri.VARSAYILAN_PROJE_ADI
                );
    }

    /**
     * Otomatik dosya adı üretir.
     */
    private String otomatikDosyaAdiUret(
            int turId
    ) {

        long zaman =
                System.currentTimeMillis();

        if (turId == YeniOgeEkrani.TUR_JAVA_ID) {
            return "JavaSinifi_" + zaman + ".java";
        }

        if (turId == YeniOgeEkrani.TUR_KOTLIN_ID) {
            return "KotlinSinifi_" + zaman + ".kt";
        }

        return "layout_" + zaman + ".xml";
    }

    /**
     * Durum bilgisini ana ekrana bildirir.
     */
    private void durumYayinla(
            String rozetMetni
    ) {

        if (durumDinleyici == null) {
            return;
        }

        durumDinleyici.durumGuncellendi(
                aktifProje,
                aktifDosya,
                rozetMetni
        );
    }

    /**
     * Kullanıcıya kısa mesaj gösterir.
     */
    private void mesajGoster(
            String mesaj
    ) {

        Toast.makeText(
                activity,
                mesaj,
                Toast.LENGTH_SHORT
        ).show();
    }
  }
