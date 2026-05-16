package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.fy.kodeditoru.file.ProjeDosyaServisi;
import org.fy.kodeditoru.proje.ProjeModeli;

import java.util.List;

/**
 * Proje yöneticisi ekran modülü.
 *
 * Bu sınıf:
 * - mevcut projeleri listeler.
 * - kullanıcının proje seçmesini sağlar.
 * - seçilen projeyi çağıran ekrana bildirir.
 * - proje seçimi sonrası kullanıcıya bilgi verir.
 *
 * Kural:
 * - dosya içeriği okumaz.
 * - dosya kaydetmez.
 * - dosya silmez.
 * - klasör yönetmez.
 * - APK derlemez.
 * - runtime başlatmaz.
 * - editör yönetmez.
 */
public final class ProjeYoneticisiEkrani {

    /**
     * Proje seçimi geri bildirimi.
     */
    public interface ProjeSecimDinleyici {

        /**
         * Proje seçildiğinde çağrılır.
         */
        void projeSecildi(
                ProjeModeli proje
        );
    }

    private final Activity activity;
    private final ProjeDosyaServisi projeDosyaServisi;

    /**
     * Proje yöneticisi ekranı oluşturur.
     */
    public ProjeYoneticisiEkrani(
            Activity activity,
            ProjeDosyaServisi projeDosyaServisi
    ) {

        if (activity == null) {
            throw new IllegalArgumentException(
                    "Activity null olamaz."
            );
        }

        if (projeDosyaServisi == null) {
            throw new IllegalArgumentException(
                    "ProjeDosyaServisi null olamaz."
            );
        }

        this.activity = activity;
        this.projeDosyaServisi = projeDosyaServisi;
    }

    /**
     * Proje seçme penceresini açar.
     */
    public void ac(
            ProjeSecimDinleyici dinleyici
    ) {

        List<ProjeModeli> projeler =
                projeDosyaServisi.projeleriListele();

        if (projeler.isEmpty()) {
            bosProjeMesajiGoster();
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
                        .setTitle("Projeler")
                        .setView(scrollView)
                        .setNegativeButton("Kapat", null)
                        .create();

        for (ProjeModeli proje : projeler) {

            TextView satir =
                    projeSatiriOlustur(proje);

            satir.setOnClickListener(v -> {

                if (dinleyici != null) {
                    dinleyici.projeSecildi(proje);
                }

                Toast.makeText(
                        activity,
                        "Seçili proje: " + proje.getProjeAdi(),
                        Toast.LENGTH_SHORT
                ).show();

                dialog.dismiss();
            });

            liste.addView(satir);
        }

        dialog.show();
    }

    /**
     * Proje satırı oluşturur.
     */
    private TextView projeSatiriOlustur(
            ProjeModeli proje
    ) {

        TextView satir =
                new TextView(activity);

        satir.setText(
                "▣  "
                        + proje.getProjeAdi()
                        + "\n"
                        + proje.getDosyalar().size()
                        + " dosya"
        );

        satir.setTextColor(Color.rgb(15, 23, 42));
        satir.setTextSize(15f);
        satir.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        satir.setGravity(Gravity.CENTER_VERTICAL);
        satir.setPadding(28, 20, 28, 20);

        return satir;
    }

    /**
     * Boş proje mesajı gösterir.
     */
    private void bosProjeMesajiGoster() {

        new AlertDialog.Builder(activity)
                .setTitle("Proje yok")
                .setMessage("Henüz oluşturulmuş proje bulunamadı.")
                .setPositiveButton("Tamam", null)
                .show();
    }
    }
