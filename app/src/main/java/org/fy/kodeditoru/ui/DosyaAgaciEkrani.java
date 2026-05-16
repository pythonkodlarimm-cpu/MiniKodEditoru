package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.fy.kodeditoru.proje.DosyaModeli;
import org.fy.kodeditoru.proje.ProjeModeli;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Dosya ağacı ekran modülü.
 *
 * Bu sınıf:
 * - seçili projenin klasör ve dosya ağacını gösterir.
 * - kullanıcının dosya seçmesini sağlar.
 * - seçilen dosyayı çağıran ekrana bildirir.
 * - klasörleri ve dosyaları gerçek proje yapısına göre listeler.
 *
 * Kural:
 * - dosya içeriği okumaz.
 * - dosya kaydetmez.
 * - dosya silmez.
 * - dosya taşımaz.
 * - APK derlemez.
 * - runtime başlatmaz.
 */
public final class DosyaAgaciEkrani {

    /**
     * Dosya seçimi geri bildirimi.
     */
    public interface DosyaSecimDinleyici {

        /**
         * Dosya seçildiğinde çağrılır.
         */
        void dosyaSecildi(
                DosyaModeli dosya
        );
    }

    private final Activity activity;

    /**
     * Dosya ağacı ekranı oluşturur.
     */
    public DosyaAgaciEkrani(
            Activity activity
    ) {

        if (activity == null) {
            throw new IllegalArgumentException(
                    "Activity null olamaz."
            );
        }

        this.activity = activity;
    }

    /**
     * Dosya ağacı penceresini açar.
     */
    public void ac(
            ProjeModeli proje,
            DosyaSecimDinleyici dinleyici
    ) {

        if (proje == null) {
            bosMesajGoster(
                    "Proje seçilmedi",
                    "Dosya ağacını görmek için önce bir proje seçmelisin."
            );
            return;
        }

        File kokKlasor =
                proje.getProjeKlasoru();

        if (kokKlasor == null || !kokKlasor.exists()) {
            bosMesajGoster(
                    "Proje klasörü yok",
                    "Seçili projenin klasörü bulunamadı."
            );
            return;
        }

        ScrollView scrollView =
                new ScrollView(activity);

        LinearLayout liste =
                new LinearLayout(activity);

        liste.setOrientation(LinearLayout.VERTICAL);
        liste.setPadding(24, 20, 24, 20);

        scrollView.addView(liste);

        AlertDialog dialog =
                new AlertDialog.Builder(activity)
                        .setTitle("Dosya ağacı")
                        .setView(scrollView)
                        .setNegativeButton("Kapat", null)
                        .create();

        agaciEkle(
                liste,
                kokKlasor,
                kokKlasor,
                0,
                dinleyici,
                dialog
        );

        dialog.show();
    }

    /**
     * Klasör ağacını recursive olarak listeye ekler.
     */
    private void agaciEkle(
            LinearLayout liste,
            File kokKlasor,
            File aktifKlasor,
            int seviye,
            DosyaSecimDinleyici dinleyici,
            AlertDialog dialog
    ) {

        File[] ogeler =
                aktifKlasor.listFiles();

        if (ogeler == null || ogeler.length == 0) {

            if (seviye == 0) {
                bosSatirEkle(
                        liste,
                        "Bu projede dosya yok."
                );
            }

            return;
        }

        Arrays.sort(
                ogeler,
                Comparator
                        .comparing(File::isFile)
                        .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER)
        );

        for (File oge : ogeler) {

            if (oge.isDirectory()) {

                liste.addView(
                        klasorSatiriOlustur(
                                kokKlasor,
                                oge,
                                seviye
                        )
                );

                agaciEkle(
                        liste,
                        kokKlasor,
                        oge,
                        seviye + 1,
                        dinleyici,
                        dialog
                );

            } else {

                TextView satir =
                        dosyaSatiriOlustur(
                                kokKlasor,
                                oge,
                                seviye
                        );

                satir.setOnClickListener(v -> {

                    if (dinleyici != null) {
                        dinleyici.dosyaSecildi(
                                new DosyaModeli(
                                        oge
                                )
                        );
                    }

                    Toast.makeText(
                            activity,
                            "Açılan dosya: " + oge.getName(),
                            Toast.LENGTH_SHORT
                    ).show();

                    dialog.dismiss();
                });

                liste.addView(satir);
            }
        }
    }

    /**
     * Klasör satırı oluşturur.
     */
    private TextView klasorSatiriOlustur(
            File kokKlasor,
            File klasor,
            int seviye
    ) {

        TextView satir =
                temelSatirOlustur(
                        seviye
                );

        satir.setText(
                "📁 "
                        + goreliYolGetir(
                                kokKlasor,
                                klasor
                        )
        );

        satir.setTextColor(
                Color.rgb(15, 23, 42)
        );

        satir.setTypeface(
                satir.getTypeface(),
                android.graphics.Typeface.BOLD
        );

        return satir;
    }

    /**
     * Dosya satırı oluşturur.
     */
    private TextView dosyaSatiriOlustur(
            File kokKlasor,
            File dosya,
            int seviye
    ) {

        TextView satir =
                temelSatirOlustur(
                        seviye
                );

        satir.setText(
                dosyaIkonuGetir(dosya)
                        + " "
                        + goreliYolGetir(
                                kokKlasor,
                                dosya
                        )
        );

        satir.setTextColor(
                Color.rgb(51, 65, 85)
        );

        return satir;
    }

    /**
     * Temel satır görünümü oluşturur.
     */
    private TextView temelSatirOlustur(
            int seviye
    ) {

        TextView satir =
                new TextView(activity);

        satir.setTextSize(14.5f);
        satir.setGravity(Gravity.CENTER_VERTICAL);
        satir.setPadding(
                20 + (seviye * 34),
                18,
                20,
                18
        );

        return satir;
    }

    /**
     * Boş satır ekler.
     */
    private void bosSatirEkle(
            LinearLayout liste,
            String mesaj
    ) {

        TextView satir =
                temelSatirOlustur(0);

        satir.setText(mesaj);
        satir.setTextColor(Color.GRAY);

        liste.addView(satir);
    }

    /**
     * Boş mesaj gösterir.
     */
    private void bosMesajGoster(
            String baslik,
            String mesaj
    ) {

        new AlertDialog.Builder(activity)
                .setTitle(baslik)
                .setMessage(mesaj)
                .setPositiveButton("Tamam", null)
                .show();
    }

    /**
     * Dosya türüne göre ikon üretir.
     */
    private String dosyaIkonuGetir(
            File dosya
    ) {

        String ad =
                dosya.getName().toLowerCase();

        if (ad.endsWith(".xml")) {
            return "🧩";
        }

        if (ad.endsWith(".java")) {
            return "☕";
        }

        if (ad.endsWith(".kt")) {
            return "🟣";
        }

        if (ad.endsWith(".json")) {
            return "{}";
        }

        return "📄";
    }

    /**
     * Kök klasöre göre göreli yol üretir.
     */
    private String goreliYolGetir(
            File kokKlasor,
            File hedef
    ) {

        String kokYol =
                kokKlasor.getAbsolutePath();

        String hedefYol =
                hedef.getAbsolutePath();

        if (!hedefYol.startsWith(kokYol)) {
            return hedef.getName();
        }

        String sonuc =
                hedefYol.substring(kokYol.length());

        if (sonuc.startsWith(File.separator)) {
            sonuc = sonuc.substring(1);
        }

        if (sonuc.trim().isEmpty()) {
            return hedef.getName();
        }

        return sonuc;
    }
                }
