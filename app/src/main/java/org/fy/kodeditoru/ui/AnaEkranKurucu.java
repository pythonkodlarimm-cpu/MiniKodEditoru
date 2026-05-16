package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.graphics.Color;
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

import java.io.IOException;
import java.util.List;

/**
 * Ana ekran UI kurucu modülü.
 *
 * Bu sınıf:
 * - XML ekran bileşenlerini Java tarafına bağlar.
 * - akıllı ekran ölçülerini uygular.
 * - güvenli ekran alanı yöneticisini başlatır.
 * - editör yöneticisini başlatır.
 * - XML önizleme yöneticisini başlatır.
 * - proje dosya servisini UI aksiyonlarına bağlar.
 * - üst buton davranışlarını yönetir.
 * - kullanıcı aksiyonlarını Android/data uygulama alanındaki log dosyasına yazar.
 *
 * Kural:
 * - APK derlemez.
 * - reklam sistemi çalıştırmaz.
 * - ödeme sistemi başlatmaz.
 * - ağır işlem yapmaz.
 */
public final class AnaEkranKurucu {

    private final Activity activity;
    private final EkranOlcekYoneticisi olcek;
    private final SafeAreaYoneticisi safeAreaYoneticisi;
    private final LogYoneticisi logYoneticisi;
    private final ProjeDosyaServisi projeDosyaServisi;

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
    private final Button klasorButonu;

    private final EditText kodEditoru;
    private final FrameLayout onizlemeAlani;

    private final EditorYoneticisi editorYoneticisi;
    private final XmlOnizlemeYoneticisi xmlOnizlemeYoneticisi;

    private ProjeModeli aktifProje;
    private DosyaModeli aktifDosya;

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
        this.projeDosyaServisi = new ProjeDosyaServisi(activity);

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
        klasorButonu = activity.findViewById(R.id.klasorButonu);

        kodEditoru = activity.findViewById(R.id.kodEditoru);
        onizlemeAlani = activity.findViewById(R.id.onizlemeAlani);

        editorYoneticisi = new EditorYoneticisi(kodEditoru);

        xmlOnizlemeYoneticisi = new XmlOnizlemeYoneticisi(
                activity,
                onizlemeAlani
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
        agirliklariUygula();
        butonlariYapilandir();
        editoruYapilandir();
        cihazSinifiniUygula();
        varsayilanProjeyiHazirla();

        safeAreaYoneticisi.baslat();

        logYaz("Ana ekran yapılandırıldı.");
        logYaz("Log dosyası: " + logYoneticisi.logDosyaYoluGetir());
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
     * Dinamik yazı boyutlarını uygular.
     */
    private void yaziBoyutlariniUygula() {

        baslikMetni.setTextSize(olcek.baslikYaziBoyutuSp());
        altBaslikMetni.setTextSize(12f);
        durumRozeti.setTextSize(olcek.durumYaziBoyutuSp());
        dosyaYoluMetni.setTextSize(12f);
        kodEditoru.setTextSize(olcek.editorYaziBoyutuSp());
    }

    /**
     * Kart iç boşluklarını uygular.
     */
    private void kartPaddingleriniUygula() {

        int padding = olcek.kartPaddingPx();

        editorKart.setPadding(padding, padding, padding, padding);
        onizlemeKart.setPadding(padding, padding, padding, padding);
    }

    /**
     * Editör ve önizleme ağırlıklarını uygular.
     */
    private void agirliklariUygula() {

        LinearLayout.LayoutParams editorParams =
                (LinearLayout.LayoutParams) editorKart.getLayoutParams();

        editorParams.weight = olcek.editorAgirlik();
        editorKart.setLayoutParams(editorParams);

        LinearLayout.LayoutParams onizlemeParams =
                (LinearLayout.LayoutParams) onizlemeKart.getLayoutParams();

        onizlemeParams.weight = olcek.onizlemeAgirlik();
        onizlemeKart.setLayoutParams(onizlemeParams);
    }

    /**
     * Buton davranışlarını yapılandırır.
     */
    private void butonlariYapilandir() {

        int butonYukseklik = olcek.butonYukseklikPx();

        yukseklikUygula(yeniDosyaButonu, butonYukseklik);
        yukseklikUygula(kaydetButonu, butonYukseklik);
        yukseklikUygula(xmlOnizlemeButonu, butonYukseklik);
        yukseklikUygula(klasorButonu, butonYukseklik);

        yeniDosyaButonu.setOnClickListener(v -> yeniDosyaOlustur());
        kaydetButonu.setOnClickListener(v -> kaydet());
        xmlOnizlemeButonu.setOnClickListener(v -> onizlemeYap());
        klasorButonu.setOnClickListener(v -> projelerButonuTiklandi());

        logYaz("Üst buton olayları bağlandı.");
    }

    /**
     * Varsayılan projeyi hazırlar ve ilk XML dosyasını editöre yükler.
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
            logYaz("Varsayılan proje hazırlandı.");

        } catch (IOException hata) {

            mesajGoster("Proje hazırlanamadı.");
            logYaz("Proje hazırlama hatası: " + hata.getMessage());
        }
    }

    /**
     * Yeni XML dosyası oluşturur.
     */
    private void yeniDosyaOlustur() {

        try {

            if (aktifProje == null) {
                aktifProje =
                        projeDosyaServisi.projeOlustur(
                                ProjeSabitleri.VARSAYILAN_PROJE_ADI
                        );
            }

            String dosyaAdi =
                    "layout_"
                            + System.currentTimeMillis()
                            + ".xml";

            aktifDosya =
                    projeDosyaServisi.xmlDosyasiOlustur(
                            aktifProje,
                            dosyaAdi
                    );

            aktifProje.aktifDosyaAyarla(aktifDosya);
            aktifDosyayiEditoreYukle();

            durumRozeti.setText("Yeni");
            mesajGoster("Yeni XML dosyası oluşturuldu.");
            logYaz("Yeni dosya oluşturuldu: " + aktifDosya.getTamYol());

        } catch (IOException hata) {

            mesajGoster("Yeni dosya oluşturulamadı.");
            logYaz("Yeni dosya hatası: " + hata.getMessage());
        }
    }

    /**
     * Aktif dosyayı kaydeder.
     */
    private void kaydet() {

        String icerik = editorYoneticisi.icerikGetir();

        if (icerik.trim().isEmpty()) {
            mesajGoster("Kaydedilecek kod yok.");
            logYaz("Kaydet butonu çalıştı: içerik boş.");
            return;
        }

        if (aktifDosya == null) {
            mesajGoster("Aktif dosya yok.");
            logYaz("Kaydet iptal: aktif dosya yok.");
            return;
        }

        try {

            projeDosyaServisi.dosyaKaydet(
                    aktifDosya,
                    icerik
            );

            dosyaYoluMetni.setText(aktifDosya.getTamYol());
            durumRozeti.setText("Kaydedildi");

            mesajGoster("Dosya kaydedildi.");
            logYaz("Dosya kaydedildi: " + aktifDosya.getTamYol());

        } catch (IOException hata) {

            mesajGoster("Dosya kaydedilemedi.");
            logYaz("Kaydet hatası: " + hata.getMessage());
        }
    }

    /**
     * XML önizleme davranışını çalıştırır.
     */
    private void onizlemeYap() {

        String xml = editorYoneticisi.icerikGetir();

        if (xml.trim().isEmpty()) {
            mesajGoster("Önizleme için XML kodu yaz.");
            logYaz("Önizle butonu çalıştı: içerik boş.");
            return;
        }

        xmlOnizlemeYoneticisi.onizlemeGuncelle(xml);
        durumRozeti.setText("Önizleme");

        mesajGoster("Önizleme güncellendi.");
        logYaz("Önizle çalıştı. Karakter sayısı: " + xml.length());
    }

    /**
     * Projeler butonunda aktif proje bilgisini gösterir.
     */
    private void projelerButonuTiklandi() {

        if (aktifProje == null) {
            varsayilanProjeyiHazirla();
            return;
        }

        String bilgi =
                aktifProje.getProjeAdi()
                        + " / "
                        + aktifProje.getDosyalar().size()
                        + " dosya";

        durumRozeti.setText("Projeler");
        dosyaYoluMetni.setText(aktifProje.getProjeKlasoru().getAbsolutePath());

        mesajGoster(bilgi);
        logYaz("Projeler butonu çalıştı: " + bilgi);
    }

    /**
     * Aktif XML dosyasını seçer.
     */
    private void aktifXmlDosyasiniSec() {

        if (aktifProje == null) {
            return;
        }

        List<DosyaModeli> xmlDosyalari =
                aktifProje.xmlDosyalari();

        if (!xmlDosyalari.isEmpty()) {
            aktifDosya = xmlDosyalari.get(0);
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
        dosyaYoluMetni.setText(aktifDosya.getTamYol());

        if (aktifDosya.isXml()) {
            xmlOnizlemeYoneticisi.onizlemeGuncelle(icerik);
        }
    }

    /**
     * Editör davranışını yapılandırır.
     */
    private void editoruYapilandir() {

        kodEditoru.setTextColor(Color.parseColor("#E2E8F0"));
        kodEditoru.setHintTextColor(Color.parseColor("#64748B"));
        kodEditoru.setBackgroundColor(Color.parseColor("#020617"));
    }

    /**
     * Telefon/tablet sınıfını uygular.
     */
    private void cihazSinifiniUygula() {

        if (olcek.isTablet()) {
            durumRozeti.setText("Tablet");
            logYaz("Cihaz sınıfı: Tablet");
            return;
        }

        durumRozeti.setText("Telefon");
        logYaz("Cihaz sınıfı: Telefon");
    }

    /**
     * View yüksekliği uygular.
     */
    private void yukseklikUygula(
            android.view.View view,
            int yukseklik
    ) {

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = yukseklik;
        view.setLayoutParams(params);
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

    /**
     * Dosya log kaydı yazar.
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
