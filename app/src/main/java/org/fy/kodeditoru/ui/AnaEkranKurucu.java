package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.fy.kodeditoru.R;
import org.fy.kodeditoru.core.ProjeSabitleri;
import org.fy.kodeditoru.editor.EditorYoneticisi;
import org.fy.kodeditoru.file.ProjeDosyaServisi;
import org.fy.kodeditoru.log.LogYoneticisi;
import org.fy.kodeditoru.preview.XmlOnizlemeYoneticisi;
import org.fy.kodeditoru.proje.DosyaModeli;
import org.fy.kodeditoru.proje.ProjeModeli;
import org.fy.kodeditoru.runtime.RuntimeEkranActivity;
import org.fy.kodeditoru.runtime.RuntimeVeriDeposu;

import java.io.IOException;

/**
 * Ana ekran kurucu modülü.
 *
 * Bu sınıf:
 * - ana ekran XML bileşenlerini bağlar.
 * - yardımcı UI modüllerini başlatır.
 * - ana ekran butonlarını ilgili modüllere yönlendirir.
 * - aktif proje ve aktif dosya durumunu ekranda gösterir.
 * - seçili projeyi runtime ekranına gönderir.
 *
 * Kural:
 * - hata penceresi üretimini RuntimeHataEkrani modülüne devreder.
 * - editör kopyala/yapıştır/temizle işlemlerini EditorAracCubugu modülüne devreder.
 * - yeni öğe giriş ekranını YeniOgeEkrani modülüne devreder.
 * - proje seçim ekranını ProjeYoneticisiEkrani modülüne devreder.
 * - APK derlemez.
 * - reklam sistemi çalıştırmaz.
 * - ödeme sistemi başlatmaz.
 */
public final class AnaEkranKurucu {

    private final Activity activity;
    private final EkranOlcekYoneticisi olcek;
    private final SafeAreaYoneticisi safeAreaYoneticisi;
    private final LogYoneticisi logYoneticisi;
    private final ProjeDosyaServisi projeDosyaServisi;
    private final RuntimeHataEkrani runtimeHataEkrani;

    private final LinearLayout kokAlan;
    private final LinearLayout ustBar;
    private final LinearLayout editorKart;
    private final LinearLayout onizlemeKart;

    private final TextView baslikMetni;
    private final TextView altBaslikMetni;
    private final TextView durumRozeti;
    private final TextView dosyaYoluMetni;

    private final Button yeniDosyaButonu;
    private final Button kaydetButonu;
    private final Button xmlOnizlemeButonu;
    private final Button calistirButonu;
    private final Button klasorButonu;
    private final Button editorKopyalaButonu;
    private final Button editorYapistirButonu;
    private final Button editorTemizleButonu;

    private final EditText kodEditoru;
    private final FrameLayout onizlemeAlani;

    private final EditorYoneticisi editorYoneticisi;
    private final XmlOnizlemeYoneticisi xmlOnizlemeYoneticisi;
    private final EditorAracCubugu editorAracCubugu;

    private ProjeModeli aktifProje;
    private DosyaModeli aktifDosya;

    /**
     * Ana ekran kurucu oluşturur.
     */
    public AnaEkranKurucu(
            Activity activity
    ) {

        if (activity == null) {
            throw new IllegalArgumentException("Activity null olamaz.");
        }

        this.activity = activity;
        this.olcek = new EkranOlcekYoneticisi(activity);
        this.logYoneticisi = new LogYoneticisi(activity);
        this.projeDosyaServisi = new ProjeDosyaServisi(activity);
        this.runtimeHataEkrani = new RuntimeHataEkrani(activity);

        kokAlan = activity.findViewById(R.id.kokAlan);
        ustBar = activity.findViewById(R.id.ustBar);
        editorKart = activity.findViewById(R.id.editorKart);
        onizlemeKart = activity.findViewById(R.id.onizlemeKart);

        baslikMetni = activity.findViewById(R.id.baslikMetni);
        altBaslikMetni = activity.findViewById(R.id.altBaslikMetni);
        durumRozeti = activity.findViewById(R.id.durumRozeti);
        dosyaYoluMetni = activity.findViewById(R.id.dosyaYoluMetni);

        yeniDosyaButonu = activity.findViewById(R.id.yeniDosyaButonu);
        kaydetButonu = activity.findViewById(R.id.kaydetButonu);
        xmlOnizlemeButonu = activity.findViewById(R.id.xmlOnizlemeButonu);
        calistirButonu = activity.findViewById(R.id.calistirButonu);
        klasorButonu = activity.findViewById(R.id.klasorButonu);

        editorKopyalaButonu = activity.findViewById(R.id.editorKopyalaButonu);
        editorYapistirButonu = activity.findViewById(R.id.editorYapistirButonu);
        editorTemizleButonu = activity.findViewById(R.id.editorTemizleButonu);

        kodEditoru = activity.findViewById(R.id.kodEditoru);
        onizlemeAlani = activity.findViewById(R.id.onizlemeAlani);

        editorYoneticisi = new EditorYoneticisi(kodEditoru);

        xmlOnizlemeYoneticisi =
                new XmlOnizlemeYoneticisi(
                        activity,
                        onizlemeAlani
                );

        editorAracCubugu =
                new EditorAracCubugu(
                        activity,
                        kodEditoru
                );

        safeAreaYoneticisi =
                new SafeAreaYoneticisi(
                        activity,
                        kokAlan
                );

        arayuzuYapilandir();
    }

    /**
     * Ana ekran görünümünü yapılandırır.
     */
    private void arayuzuYapilandir() {

        kokAlan.setPadding(
                olcek.anaPaddingPx(),
                olcek.anaPaddingPx(),
                olcek.anaPaddingPx(),
                olcek.anaPaddingPx()
        );

        ustBarYuksekligiUygula();
        yaziBoyutlariniUygula();
        kartPaddingleriniUygula();
        butonlariYapilandir();
        editoruYapilandir();
        varsayilanProjeyiHazirla();

        safeAreaYoneticisi.baslat();

        logYaz("Ana ekran yapılandırıldı.");
    }

    /**
     * Üst bar yüksekliğini uygular.
     */
    private void ustBarYuksekligiUygula() {

        ViewGroup.LayoutParams params = ustBar.getLayoutParams();
        params.height = olcek.ustBarYukseklikPx();
        ustBar.setLayoutParams(params);
    }

    /**
     * Yazı boyutlarını uygular.
     */
    private void yaziBoyutlariniUygula() {

        baslikMetni.setTextSize(olcek.baslikYaziBoyutuSp());
        altBaslikMetni.setTextSize(12f);
        durumRozeti.setTextSize(olcek.durumYaziBoyutuSp());
        dosyaYoluMetni.setTextSize(12f);
        kodEditoru.setTextSize(olcek.editorYaziBoyutuSp());
    }

    /**
     * Kart padding değerlerini uygular.
     */
    private void kartPaddingleriniUygula() {

        int padding = olcek.kartPaddingPx();

        editorKart.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        if (onizlemeKart != null) {
            onizlemeKart.setPadding(0, 0, 0, 0);
        }
    }

    /**
     * Buton olaylarını bağlar.
     */
    private void butonlariYapilandir() {

        int yukseklik = olcek.butonYukseklikPx();

        yukseklikUygula(yeniDosyaButonu, yukseklik);
        yukseklikUygula(kaydetButonu, yukseklik);
        yukseklikUygula(xmlOnizlemeButonu, yukseklik);
        yukseklikUygula(calistirButonu, yukseklik);
        yukseklikUygula(klasorButonu, yukseklik);
        yukseklikUygula(editorKopyalaButonu, yukseklik);
        yukseklikUygula(editorYapistirButonu, yukseklik);
        yukseklikUygula(editorTemizleButonu, yukseklik);

        yeniDosyaButonu.setOnClickListener(v -> yeniOgeEkraniAc());
        kaydetButonu.setOnClickListener(v -> kaydet());
        xmlOnizlemeButonu.setOnClickListener(v -> koduKontrolEt());
        calistirButonu.setOnClickListener(v -> calistir());
        klasorButonu.setOnClickListener(v -> projeYoneticisiniAc());

        editorKopyalaButonu.setOnClickListener(v -> editorAracCubugu.kopyala());
        editorYapistirButonu.setOnClickListener(v -> editorAracCubugu.yapistir());
        editorTemizleButonu.setOnClickListener(
                v -> editorAracCubugu.temizleOnayli(
                        () -> {
                            xmlOnizlemeYoneticisi.onizlemeTemizle();
                            durumRozeti.setText("Temiz");
                        }
                )
        );

        logYaz("Ana ekran butonları bağlandı.");
    }

    /**
     * Varsayılan projeyi hazırlar.
     */
    private void varsayilanProjeyiHazirla() {

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

            durumRozeti.setText("Hazır");
            ustDurumGuncelle();

        } catch (IOException hata) {

            runtimeHataEkrani.goster(
                    "Proje hazırlanamadı",
                    "Varsayılan proje açılırken hata oluştu.",
                    hata.getMessage()
            );
        }
    }

    /**
     * Proje yöneticisini açar.
     */
    private void projeYoneticisiniAc() {

        ProjeYoneticisiEkrani ekran =
                new ProjeYoneticisiEkrani(
                        activity,
                        projeDosyaServisi
                );

        ekran.ac(this::projeSecildi);
    }

    /**
     * Yeni öğe ekranını açar.
     */
    private void yeniOgeEkraniAc() {

        YeniOgeEkrani ekran =
                new YeniOgeEkrani(activity);

        ekran.ac(this::yeniDosyaOlustur);
    }

    /**
     * Seçilen projeyi aktif proje yapar.
     */
    private void projeSecildi(
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

            durumRozeti.setText("Proje");
            ustDurumGuncelle();

            mesajGoster("Seçili proje: " + aktifProje.getProjeAdi());
            logYaz("Proje seçildi: " + aktifProje.getProjeAdi());

        } catch (IOException hata) {

            runtimeHataEkrani.goster(
                    "Proje seçildi ama dosya açılamadı",
                    "Seçilen projenin ilk dosyası editöre yüklenemedi.",
                    hata.getMessage()
            );
        }
    }

    /**
     * Yeni dosya oluşturur.
     */
    private void yeniDosyaOlustur(
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
                aktifDosya = projeDosyaServisi.javaDosyasiOlustur(aktifProje, dosyaAdi);
            } else if (secilenTurId == YeniOgeEkrani.TUR_KOTLIN_ID) {
                aktifDosya = projeDosyaServisi.kotlinDosyasiOlustur(aktifProje, dosyaAdi);
            } else {
                aktifDosya = projeDosyaServisi.xmlDosyasiOlustur(aktifProje, dosyaAdi);
            }

            aktifProje.aktifDosyaAyarla(aktifDosya);
            aktifDosyayiEditoreYukle();

            durumRozeti.setText("Yeni");
            ustDurumGuncelle();

            mesajGoster("Dosya oluşturuldu: " + aktifDosya.getAd());
            logYaz("Dosya oluşturuldu: " + aktifDosya.getTamYol());

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
    private void kaydet() {

        String icerik = editorYoneticisi.icerikGetir();

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

            durumRozeti.setText("Kaydedildi");
            ustDurumGuncelle();

            mesajGoster("Kaydedildi: " + aktifDosya.getAd());
            logYaz("Dosya kaydedildi: " + aktifDosya.getTamYol());

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
    private void koduKontrolEt() {

        String icerik = editorYoneticisi.icerikGetir();

        if (icerik.trim().isEmpty()) {
            mesajGoster("Kontrol edilecek içerik yok.");
            return;
        }

        if (aktifDosya != null && aktifDosya.isXml()) {
            xmlOnizlemeYoneticisi.onizlemeGuncelle(icerik);
            durumRozeti.setText("XML uygun");
            mesajGoster("XML temel kontrol tamamlandı.");
            return;
        }

        durumRozeti.setText("Kontrol");
        mesajGoster("Dosya kontrol edildi.");
    }

    /**
     * Seçili projeyi runtime ekranında çalıştırır.
     */
    private void calistir() {

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
                        "Aranan öncelik: " + ProjeSabitleri.VARSAYILAN_XML_DOSYASI
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

            durumRozeti.setText("Çalışıyor");
            mesajGoster("Proje çalıştırılıyor: " + aktifProje.getProjeAdi());
            logYaz("Proje runtime başlatıldı: " + aktifProje.getProjeAdi());

        } catch (IOException hata) {

            runtimeHataEkrani.goster(
                    "Proje çalıştırılamadı",
                    "Runtime başlatma sırasında hata oluştu.",
                    hata.getMessage()
            );
        }
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
            dosyaYoluMetni.setText("Dosya seçilmedi");
            return;
        }

        String icerik =
                projeDosyaServisi.dosyaOku(
                        aktifDosya
                );

        kodEditoru.setText(icerik);
        ustDurumGuncelle();

        if (aktifDosya.isXml()) {
            xmlOnizlemeYoneticisi.onizlemeGuncelle(icerik);
        } else {
            xmlOnizlemeYoneticisi.onizlemeTemizle();
        }
    }

    /**
     * Üst durum metnini günceller.
     */
    private void ustDurumGuncelle() {

        if (aktifProje == null) {
            dosyaYoluMetni.setText("Proje seçilmedi");
            return;
        }

        if (aktifDosya == null) {
            dosyaYoluMetni.setText("Seçili proje: " + aktifProje.getProjeAdi());
            return;
        }

        dosyaYoluMetni.setText(
                "Proje: "
                        + aktifProje.getProjeAdi()
                        + "  •  Dosya: "
                        + aktifDosya.getAd()
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

        long zaman = System.currentTimeMillis();

        if (turId == YeniOgeEkrani.TUR_JAVA_ID) {
            return "JavaSinifi_" + zaman + ".java";
        }

        if (turId == YeniOgeEkrani.TUR_KOTLIN_ID) {
            return "KotlinSinifi_" + zaman + ".kt";
        }

        return "layout_" + zaman + ".xml";
    }

    /**
     * Editör stilini uygular.
     */
    private void editoruYapilandir() {

        kodEditoru.setTextColor(Color.parseColor("#E2E8F0"));
        kodEditoru.setHintTextColor(Color.parseColor("#64748B"));
        kodEditoru.setBackgroundColor(Color.parseColor("#020617"));
    }

    /**
     * View yüksekliği uygular.
     */
    private void yukseklikUygula(
            View view,
            int yukseklik
    ) {

        if (view == null) {
            return;
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = yukseklik;
        view.setLayoutParams(params);
    }

    /**
     * Toast mesajı gösterir.
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

    /**
     * Log kaydı yazar.
     */
    private void logYaz(
            String mesaj
    ) {

        logYoneticisi.logYaz(mesaj);
    }

    /**
     * Editör yöneticisini döndürür.
     */
    public EditorYoneticisi getEditorYoneticisi() {
        return editorYoneticisi;
    }

    /**
     * XML önizleme yöneticisini döndürür.
     */
    public XmlOnizlemeYoneticisi getXmlOnizlemeYoneticisi() {
        return xmlOnizlemeYoneticisi;
    }

    /**
     * Güvenli alan yöneticisini döndürür.
     */
    public SafeAreaYoneticisi getSafeAreaYoneticisi() {
        return safeAreaYoneticisi;
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
            }
