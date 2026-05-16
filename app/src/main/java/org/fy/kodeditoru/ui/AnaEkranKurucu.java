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
import org.fy.kodeditoru.editor.EditorYoneticisi;
import org.fy.kodeditoru.log.LogYoneticisi;
import org.fy.kodeditoru.preview.XmlOnizlemeYoneticisi;

/**
 * Ana ekran UI kurucu modülü.
 *
 * Bu sınıf:
 * - XML ekran bileşenlerini Java tarafına bağlar.
 * - akıllı ekran ölçülerini uygular.
 * - klavye ve sistem güvenli alanlarını uygular.
 * - editör yöneticisini başlatır.
 * - XML önizleme yöneticisini başlatır.
 * - üst buton davranışlarını bağlar.
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
    private final KlavyeInsetsYoneticisi klavyeInsetsYoneticisi;
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
    private final Button klasorButonu;

    private final EditText kodEditoru;
    private final FrameLayout onizlemeAlani;

    private final EditorYoneticisi editorYoneticisi;
    private final XmlOnizlemeYoneticisi xmlOnizlemeYoneticisi;

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
        klasorButonu = activity.findViewById(R.id.klasorButonu);

        kodEditoru = activity.findViewById(R.id.kodEditoru);
        onizlemeAlani = activity.findViewById(R.id.onizlemeAlani);

        editorYoneticisi = new EditorYoneticisi(kodEditoru);

        xmlOnizlemeYoneticisi = new XmlOnizlemeYoneticisi(
                activity,
                onizlemeAlani
        );

        klavyeInsetsYoneticisi = new KlavyeInsetsYoneticisi(
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

        klavyeInsetsYoneticisi.baslat();

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

        editorKart.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        onizlemeKart.setPadding(
                padding,
                padding,
                padding,
                padding
        );
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
     * Yeni dosya davranışını çalıştırır.
     */
    private void yeniDosyaOlustur() {

        editorYoneticisi.icerikTemizle();
        xmlOnizlemeYoneticisi.onizlemeGuncelle("");

        dosyaYoluMetni.setText("Yeni dosya");
        durumRozeti.setText("Yeni");

        mesajGoster("Yeni dosya hazırlandı.");
        logYaz("Yeni dosya butonu çalıştı.");
    }

    /**
     * Kaydet davranışını çalıştırır.
     *
     * Not:
     * Şu an gerçek dosya yazma servisi bağlı değildir.
     */
    private void kaydet() {

        String icerik = editorYoneticisi.icerikGetir();

        if (icerik.trim().isEmpty()) {
            mesajGoster("Kaydedilecek kod yok.");
            logYaz("Kaydet butonu çalıştı: içerik boş.");
            return;
        }

        dosyaYoluMetni.setText("Geçici kayıt hazır");
        durumRozeti.setText("Kayıt");

        mesajGoster("Kod geçici olarak hazırlandı.");
        logYaz(
                "Kaydet butonu çalıştı. Karakter sayısı: "
                + icerik.length()
        );
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
        logYaz(
                "Önizle butonu çalıştı. Karakter sayısı: "
                + xml.length()
        );
    }

    /**
     * Projeler butonu geçici davranışını çalıştırır.
     */
    private void projelerButonuTiklandi() {

        durumRozeti.setText("Projeler");

        mesajGoster("Projeler bölümü sonraki güncellemede eklenecek.");
        logYaz("Projeler butonu çalıştı.");
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
            }
