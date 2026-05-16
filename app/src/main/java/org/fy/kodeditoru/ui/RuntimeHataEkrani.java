package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

/**
 * Runtime hata ekranı modülü.
 *
 * Bu sınıf:
 * - kullanıcıya anlaşılır hata penceresi gösterir.
 * - teknik hata detayını tek metin halinde hazırlar.
 * - hata detayını panoya kopyalatır.
 * - proje çalıştırma, dosya açma ve kayıt hatalarında ortak hata ekranı sağlar.
 *
 * Kural:
 * - dosya okuma/yazma yapmaz.
 * - runtime başlatmaz.
 * - proje seçmez.
 * - editör yönetmez.
 * - sadece hata gösterimi ve kopyalama işlemi yapar.
 */
public final class RuntimeHataEkrani {

    private final Activity activity;

    /**
     * Runtime hata ekranı oluşturur.
     */
    public RuntimeHataEkrani(
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
     * Kopyalanabilir hata penceresi gösterir.
     */
    public void goster(
            String baslik,
            String mesaj,
            String teknikDetay
    ) {

        String guvenliBaslik =
                metinGuvenliGetir(
                        baslik,
                        "İşlem tamamlanamadı"
                );

        String detay =
                hataMetniOlustur(
                        guvenliBaslik,
                        mesaj,
                        teknikDetay
                );

        new AlertDialog.Builder(activity)
                .setTitle(guvenliBaslik)
                .setMessage(detay)
                .setNegativeButton("Kapat", null)
                .setPositiveButton(
                        "Kopyala",
                        (dialog, which) -> panoyaKopyala(detay)
                )
                .show();
    }

    /**
     * Hata metnini kullanıcı ve teknik detay bölümleriyle üretir.
     */
    private String hataMetniOlustur(
            String baslik,
            String mesaj,
            String teknikDetay
    ) {

        return ""
                + metinGuvenliGetir(
                        baslik,
                        "İşlem tamamlanamadı"
                )
                + "\n\n"
                + metinGuvenliGetir(
                        mesaj,
                        "Beklenmeyen bir hata oluştu."
                )
                + "\n\n"
                + "Teknik detay:\n"
                + metinGuvenliGetir(
                        teknikDetay,
                        "Detay yok."
                );
    }

    /**
     * Metni panoya kopyalar.
     */
    private void panoyaKopyala(
            String metin
    ) {

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
                        "MiniKodEditoruHata",
                        metin == null ? "" : metin
                )
        );

        mesajGoster("Hata detayı kopyalandı.");
    }

    /**
     * Boş metin yerine varsayılan metin döndürür.
     */
    private String metinGuvenliGetir(
            String metin,
            String varsayilan
    ) {

        if (metin == null || metin.trim().isEmpty()) {
            return varsayilan;
        }

        return metin.trim();
    }

    /**
     * Kullanıcıya kısa bilgi mesajı gösterir.
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
