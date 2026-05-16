package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.List;

/**
 * Ana ekran UI kurucu modülü.
 *
 * Bu sınıf:
 * - ana editör ekranı bileşenlerini Java tarafına bağlar.
 * - proje, dosya ve runtime aksiyonlarını yönetir.
 * - görünür XML önizleme alanı kullanmadan kod editörünü ana çalışma alanı yapar.
 * - Çalıştır işlemini runtime ekranına yönlendirir.
 * - kullanıcı aksiyonlarını Android/data log dosyasına yazar.
 *
 * Kural:
 * - APK derlemez.
 * - reklam sistemi çalıştırmaz.
 * - ödeme sistemi başlatmaz.
 * - ağır işlem yapmaz.
 */
public final class AnaEkranKurucu {

    private static final int TUR_XML_ID = 1001;
    private static final int TUR_JAVA_ID = 1002;
    private static final int TUR_KOTLIN_ID = 1003;

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
    private final Button calistirButonu;
    private final Button klasorButonu;

    private final Button editorKopyalaButonu;
    private final Button editorYapistirButonu;
    private final Button editorTemizleButonu;

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
     * Kart padding uygular.
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

        yeniDosyaButonu.setOnClickListener(v -> yeniOgePenceresiAc());
        kaydetButonu.setOnClickListener(v -> kaydet());
        xmlOnizlemeButonu.setOnClickListener(v -> koduKontrolEt());
        calistirButonu.setOnClickListener(v -> calistir());
        klasorButonu.setOnClickListener(v -> projePenceresiAc());

        editorKopyalaButonu.setOnClickListener(v -> editorIceriginiKopyala());
        editorYapistirButonu.setOnClickListener(v -> editoreYapistir());
        editorTemizleButonu.setOnClickListener(v -> editoruTemizleOnayli());

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

            mesajGoster("Proje hazırlanamadı.");
            logYaz("Proje hazırlama hatası: " + hata.getMessage());
        }
    }

    /**
     * Yeni öğe penceresi açar.
     */
    private void yeniOgePenceresiAc() {

        LinearLayout panel = new LinearLayout(activity);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(
                olcek.dpToPx(20),
                olcek.dpToPx(12),
                olcek.dpToPx(20),
                olcek.dpToPx(4)
        );

        TextView bilgi = new TextView(activity);
        bilgi.setText("Dosya adını yaz ve tür seç.");
        bilgi.setTextColor(Color.DKGRAY);
        bilgi.setTextSize(14f);

        EditText adInput = new EditText(activity);
        adInput.setHint("Örnek: activity_login");
        adInput.setSingleLine(true);

        RadioGroup turGrubu = new RadioGroup(activity);
        turGrubu.setOrientation(RadioGroup.VERTICAL);
        turGrubu.addView(radioButonOlustur("XML layout dosyası", TUR_XML_ID));
        turGrubu.addView(radioButonOlustur("Java sınıfı", TUR_JAVA_ID));
        turGrubu.addView(radioButonOlustur("Kotlin sınıfı", TUR_KOTLIN_ID));
        turGrubu.check(TUR_XML_ID);

        panel.addView(bilgi);
        panel.addView(adInput);
        panel.addView(turGrubu);

        new AlertDialog.Builder(activity)
                .setTitle("Yeni öğe oluştur")
                .setView(panel)
                .setNegativeButton("Vazgeç", null)
                .setPositiveButton(
                        "Oluştur",
                        (dialog, which) -> yeniDosyaOlustur(
                                adInput.getText().toString(),
                                turGrubu.getCheckedRadioButtonId()
                        )
                )
                .show();
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

            if (secilenTurId == TUR_JAVA_ID) {
                aktifDosya =
                        projeDosyaServisi.javaDosyasiOlustur(
                                aktifProje,
                                dosyaAdi
                        );
            } else if (secilenTurId == TUR_KOTLIN_ID) {
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

            durumRozeti.setText("Yeni");
            mesajGoster("Dosya oluşturuldu: " + aktifDosya.getAd());
            logYaz("Dosya oluşturuldu: " + aktifDosya.getTamYol());

        } catch (IOException hata) {

            mesajGoster("Yeni dosya oluşturulamadı.");
            logYaz("Yeni dosya hatası: " + hata.getMessage());
        }
    }

    /**
     * Dosyayı kaydeder.
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

            mesajGoster("Dosya kaydedilemedi.");
            logYaz("Kaydet hatası: " + hata.getMessage());
        }
    }

    /**
     * Kodu hızlı kontrol eder.
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
     * Runtime ekranı başlatır.
     */
    private void calistir() {

        String xmlIcerik = editorYoneticisi.icerikGetir();

        if (xmlIcerik.trim().isEmpty()) {
            mesajGoster("Çalıştırmak için XML yaz.");
            return;
        }

        if (aktifDosya == null || !aktifDosya.isXml()) {
            mesajGoster("Çalıştırmak için bir XML dosyası seç.");
            return;
        }

        RuntimeVeriDeposu.veriAyarla(
                xmlIcerik,
                aktifJavaIcerigiGetir(),
                aktifDosya.getAd()
        );

        activity.startActivity(
                new Intent(
                        activity,
                        RuntimeEkranActivity.class
                )
        );

        durumRozeti.setText("Çalışıyor");
        logYaz("Runtime başlatıldı: " + aktifDosya.getAd());
    }

    /**
     * Aktif Java içeriğini getirir.
     */
    private String aktifJavaIcerigiGetir() {

        if (aktifProje == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (DosyaModeli dosya : aktifProje.javaDosyalari()) {

            try {
                builder.append(projeDosyaServisi.dosyaOku(dosya));
                builder.append('\n');
            } catch (IOException hata) {
                logYaz("Java okuma hatası: " + hata.getMessage());
            }
        }

        return builder.toString();
    }

    /**
     * Proje penceresi açar.
     */
    private void projePenceresiAc() {

        if (aktifProje == null) {
            varsayilanProjeyiHazirla();
            return;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("Seçili proje: ")
                .append(aktifProje.getProjeAdi())
                .append("\n\n");

        builder.append("Dosyalar:\n");

        for (DosyaModeli dosya : aktifProje.getDosyalar()) {
            builder.append("• ")
                    .append(dosya.getAd())
                    .append("\n");
        }

        new AlertDialog.Builder(activity)
                .setTitle("Proje yöneticisi")
                .setMessage(builder.toString())
                .setNegativeButton("Kapat", null)
                .show();

        durumRozeti.setText("Projeler");
    }

    /**
     * Editör içeriğini panoya kopyalar.
     */
    private void editorIceriginiKopyala() {

        String icerik = editorYoneticisi.icerikGetir();

        if (icerik.trim().isEmpty()) {
            mesajGoster("Kopyalanacak içerik yok.");
            return;
        }

        ClipboardManager clipboardManager =
                (ClipboardManager)
                        activity.getSystemService(
                                Context.CLIPBOARD_SERVICE
                        );

        if (clipboardManager == null) {
            mesajGoster("Pano kullanılamıyor.");
            return;
        }

        clipboardManager.setPrimaryClip(
                ClipData.newPlainText(
                        "MiniKodEditoru",
                        icerik
                )
        );

        mesajGoster("İçerik kopyalandı.");
    }

    /**
     * Panodaki içeriği yapıştırır.
     */
    private void editoreYapistir() {

        ClipboardManager clipboardManager =
                (ClipboardManager)
                        activity.getSystemService(
                                Context.CLIPBOARD_SERVICE
                        );

        if (clipboardManager == null
                || !clipboardManager.hasPrimaryClip()
                || clipboardManager.getPrimaryClip() == null
                || clipboardManager.getPrimaryClip().getItemCount() == 0) {

            mesajGoster("Panoda içerik yok.");
            return;
        }

        CharSequence metin =
                clipboardManager
                        .getPrimaryClip()
                        .getItemAt(0)
                        .coerceToText(activity);

        if (metin == null) {
            mesajGoster("Pano okunamadı.");
            return;
        }

        int baslangic =
                Math.max(
                        kodEditoru.getSelectionStart(),
                        0
                );

        kodEditoru.getText().insert(
                baslangic,
                metin
        );

        mesajGoster("Yapıştırıldı.");
    }

    /**
     * Editörü temizleme onayı gösterir.
     */
    private void editoruTemizleOnayli() {

        new AlertDialog.Builder(activity)
                .setTitle("Editörü temizle")
                .setMessage("Tüm içerik silinsin mi?")
                .setNegativeButton("Vazgeç", null)
                .setPositiveButton(
                        "Sil",
                        (dialog, which) -> {
                            kodEditoru.setText("");
                            xmlOnizlemeYoneticisi.onizlemeTemizle();
                            durumRozeti.setText("Temiz");
                            mesajGoster("Editör temizlendi.");
                        }
                )
                .show();
    }

    /**
     * Aktif XML dosyasını seçer.
     */
    private void aktifXmlDosyasiniSec() {

        if (aktifProje == null) {
            return;
        }

        List<DosyaModeli> xmlDosyalari = aktifProje.xmlDosyalari();

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
            dosyaYoluMetni.setText(
                    "Seçili proje: " + aktifProje.getProjeAdi()
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

        if (turId == TUR_JAVA_ID) {
            return "JavaSinifi_" + zaman + ".java";
        }

        if (turId == TUR_KOTLIN_ID) {
            return "KotlinSinifi_" + zaman + ".kt";
        }

        return "layout_" + zaman + ".xml";
    }

    /**
     * RadioButton üretir.
     */
    private RadioButton radioButonOlustur(
            String metin,
            int id
    ) {

        RadioButton radioButton = new RadioButton(activity);

        radioButton.setId(id);
        radioButton.setText(metin);

        return radioButton;
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
