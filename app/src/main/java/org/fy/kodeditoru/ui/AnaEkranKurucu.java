package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fy.kodeditoru.R;
import org.fy.kodeditoru.editor.EditorYoneticisi;
import org.fy.kodeditoru.file.ProjeDosyaServisi;
import org.fy.kodeditoru.log.LogYoneticisi;
import org.fy.kodeditoru.preview.XmlOnizlemeYoneticisi;
import org.fy.kodeditoru.proje.DosyaModeli;
import org.fy.kodeditoru.proje.ProjeModeli;

/**
 * Ana ekran kurucu modülü.
 *
 * Bu sınıf:
 * - ana ekran XML bileşenlerini bağlar.
 * - ekran ölçülerini uygular.
 * - yardımcı UI modüllerini oluşturur.
 * - buton olaylarını ilgili modüllere yönlendirir.
 * - aktif proje/dosya durumunu ekrana yansıtır.
 *
 * Kural:
 * - proje işlemi yapmaz.
 * - dosya oluşturmaz.
 * - dosya kaydetmez.
 * - runtime başlatmaz.
 * - hata penceresi üretmez.
 * - editör pano işlemi yapmaz.
 * - yalnızca ana ekran kurulum ve yönlendirme sorumluluğu taşır.
 */
public final class AnaEkranKurucu {

    private final Activity activity;
    private final EkranOlcekYoneticisi olcek;
    private final SafeAreaYoneticisi safeAreaYoneticisi;
    private final LogYoneticisi logYoneticisi;

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
    private final ProjeDosyaServisi projeDosyaServisi;
    private final RuntimeHataEkrani runtimeHataEkrani;
    private final EditorAracCubugu editorAracCubugu;
    private final AnaEkranAksiyonlari anaEkranAksiyonlari;

    /**
     * Ana ekran kurucu oluşturur.
     */
    public AnaEkranKurucu(
            Activity activity
    ) {

        if (activity == null) {
            throw new IllegalArgumentException(
                    "Activity null olamaz."
            );
        }

        this.activity = activity;
        this.olcek = new EkranOlcekYoneticisi(activity);
        this.logYoneticisi = new LogYoneticisi(activity);

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

        projeDosyaServisi =
                new ProjeDosyaServisi(activity);

        runtimeHataEkrani =
                new RuntimeHataEkrani(activity);

        editorAracCubugu =
                new EditorAracCubugu(
                        activity,
                        kodEditoru
                );

        anaEkranAksiyonlari =
                new AnaEkranAksiyonlari(
                        activity,
                        projeDosyaServisi,
                        editorYoneticisi,
                        xmlOnizlemeYoneticisi,
                        runtimeHataEkrani,
                        this::durumGuncelle
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

        safeAreaYoneticisi.baslat();

        anaEkranAksiyonlari.varsayilanProjeyiHazirla();

        logYaz("Ana ekran kuruldu.");
    }

    /**
     * Üst bar yüksekliğini uygular.
     */
    private void ustBarYuksekligiUygula() {

        ViewGroup.LayoutParams params =
                ustBar.getLayoutParams();

        params.height =
                olcek.ustBarYukseklikPx();

        ustBar.setLayoutParams(params);
    }

    /**
     * Yazı boyutlarını uygular.
     */
    private void yaziBoyutlariniUygula() {

        baslikMetni.setTextSize(
                olcek.baslikYaziBoyutuSp()
        );

        altBaslikMetni.setTextSize(12f);

        durumRozeti.setTextSize(
                olcek.durumYaziBoyutuSp()
        );

        dosyaYoluMetni.setTextSize(12f);

        kodEditoru.setTextSize(
                olcek.editorYaziBoyutuSp()
        );
    }

    /**
     * Kart padding değerlerini uygular.
     */
    private void kartPaddingleriniUygula() {

        int padding =
                olcek.kartPaddingPx();

        editorKart.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        if (onizlemeKart != null) {
            onizlemeKart.setPadding(
                    0,
                    0,
                    0,
                    0
            );
        }
    }

    /**
     * Buton olaylarını ilgili modüllere bağlar.
     */
    private void butonlariYapilandir() {

        int yukseklik =
                olcek.butonYukseklikPx();

        yukseklikUygula(yeniDosyaButonu, yukseklik);
        yukseklikUygula(kaydetButonu, yukseklik);
        yukseklikUygula(xmlOnizlemeButonu, yukseklik);
        yukseklikUygula(calistirButonu, yukseklik);
        yukseklikUygula(klasorButonu, yukseklik);
        yukseklikUygula(editorKopyalaButonu, yukseklik);
        yukseklikUygula(editorYapistirButonu, yukseklik);
        yukseklikUygula(editorTemizleButonu, yukseklik);

        yeniDosyaButonu.setOnClickListener(
                v -> yeniOgeEkraniAc()
        );

        kaydetButonu.setOnClickListener(
                v -> anaEkranAksiyonlari.kaydet()
        );

        xmlOnizlemeButonu.setOnClickListener(
                v -> anaEkranAksiyonlari.koduKontrolEt()
        );

        calistirButonu.setOnClickListener(
                v -> anaEkranAksiyonlari.calistir()
        );

        klasorButonu.setOnClickListener(
                v -> projeYoneticisiniAc()
        );

        editorKopyalaButonu.setOnClickListener(
                v -> editorAracCubugu.kopyala()
        );

        editorYapistirButonu.setOnClickListener(
                v -> editorAracCubugu.yapistir()
        );

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
     * Proje yöneticisi ekranını açar.
     */
    private void projeYoneticisiniAc() {

        ProjeYoneticisiEkrani ekran =
                new ProjeYoneticisiEkrani(
                        activity,
                        projeDosyaServisi
                );

        ekran.ac(
                anaEkranAksiyonlari::projeSec
        );
    }

    /**
     * Yeni öğe ekranını açar.
     */
    private void yeniOgeEkraniAc() {

        YeniOgeEkrani ekran =
                new YeniOgeEkrani(activity);

        ekran.ac(
                anaEkranAksiyonlari::yeniDosyaOlustur
        );
    }

    /**
     * Ana ekran durum metinlerini günceller.
     */
    private void durumGuncelle(
            ProjeModeli aktifProje,
            DosyaModeli aktifDosya,
            String rozetMetni
    ) {

        durumRozeti.setText(
                rozetMetni == null || rozetMetni.trim().isEmpty()
                        ? "Hazır"
                        : rozetMetni
        );

        if (aktifProje == null) {
            dosyaYoluMetni.setText("Proje seçilmedi.");
            return;
        }

        if (aktifDosya == null) {
            dosyaYoluMetni.setText(
                    "Seçili proje: "
                            + aktifProje.getProjeAdi()
            );
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
     * Editör stilini uygular.
     */
    private void editoruYapilandir() {

        kodEditoru.setTextColor(
                Color.parseColor("#E2E8F0")
        );

        kodEditoru.setHintTextColor(
                Color.parseColor("#64748B")
        );

        kodEditoru.setBackgroundColor(
                Color.parseColor("#020617")
        );
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

        ViewGroup.LayoutParams params =
                view.getLayoutParams();

        params.height =
                yukseklik;

        view.setLayoutParams(params);
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
     * Ana ekran aksiyonlarını döndürür.
     */
    public AnaEkranAksiyonlari getAnaEkranAksiyonlari() {
        return anaEkranAksiyonlari;
    }
            }
