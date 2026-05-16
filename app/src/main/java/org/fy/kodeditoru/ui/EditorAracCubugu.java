package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Editör araç çubuğu modülü.
 *
 * Bu sınıf:
 * - editör içeriğini panoya kopyalar.
 * - panodaki metni editöre yapıştırır.
 * - editör içeriğini kullanıcı onayıyla temizler.
 * - editör araç butonlarının davranışlarını tek merkezde toplar.
 *
 * Kural:
 * - dosya okuma/yazma yapmaz.
 * - proje yönetmez.
 * - runtime başlatmaz.
 * - XML önizleme yapmaz.
 * - APK derlemez.
 */
public final class EditorAracCubugu {

    private final Activity activity;
    private final EditText kodEditoru;

    /**
     * Editör araç çubuğu oluşturur.
     */
    public EditorAracCubugu(
            Activity activity,
            EditText kodEditoru
    ) {

        if (activity == null) {
            throw new IllegalArgumentException(
                    "Activity null olamaz."
            );
        }

        if (kodEditoru == null) {
            throw new IllegalArgumentException(
                    "Kod editörü null olamaz."
            );
        }

        this.activity = activity;
        this.kodEditoru = kodEditoru;
    }

    /**
     * Editör içeriğini panoya kopyalar.
     */
    public void kopyala() {

        String icerik =
                kodEditoru.getText() == null
                        ? ""
                        : kodEditoru.getText().toString();

        if (icerik.trim().isEmpty()) {
            mesajGoster("Kopyalanacak içerik yok.");
            return;
        }

        ClipboardManager clipboardManager =
                panoYoneticisiGetir();

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
     * Panodaki içeriği editöre yapıştırır.
     */
    public void yapistir() {

        ClipboardManager clipboardManager =
                panoYoneticisiGetir();

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

        if (metin == null || metin.toString().isEmpty()) {
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
     * Editör içeriğini onay alarak temizler.
     */
    public void temizleOnayli(
            Runnable temizlemeSonrasi
    ) {

        new AlertDialog.Builder(activity)
                .setTitle("Editörü temizle")
                .setMessage("Tüm içerik silinsin mi?")
                .setNegativeButton("Vazgeç", null)
                .setPositiveButton(
                        "Sil",
                        (dialog, which) -> {
                            kodEditoru.setText("");

                            if (temizlemeSonrasi != null) {
                                temizlemeSonrasi.run();
                            }

                            mesajGoster("Editör temizlendi.");
                        }
                )
                .show();
    }

    /**
     * Android pano yöneticisini döndürür.
     */
    private ClipboardManager panoYoneticisiGetir() {

        return (ClipboardManager)
                activity.getSystemService(
                        Context.CLIPBOARD_SERVICE
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
