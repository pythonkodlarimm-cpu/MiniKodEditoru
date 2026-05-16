package org.fy.kodeditoru.log;

import android.content.Context;

import org.fy.kodeditoru.core.ProjeSabitleri;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Merkezi dosya log yöneticisi.
 *
 * Bu sınıf:
 * - Android/data uygulama alanına log yazar.
 * - debug.log dosyasını yönetir.
 * - zaman damgalı kayıt üretir.
 * - güvenli log klasörü oluşturur.
 * - log dosyası büyürse dosyayı sıfırlayıp yeni kayıt başlatır.
 *
 * Kural:
 * - UI üretmez.
 * - Toast göstermez.
 * - thread yönetmez.
 * - ağ işlemi yapmaz.
 * - reklam sistemi çalıştırmaz.
 */
public final class LogYoneticisi {

    private static final long MAKSIMUM_LOG_BOYUTU =
            512L * 1024L;

    private final File logDosyasi;

    /**
     * Log yöneticisi oluşturur.
     */
    public LogYoneticisi(
            Context context
    ) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context null olamaz."
            );
        }

        File anaKlasor =
                context.getExternalFilesDir(null);

        if (anaKlasor == null) {
            throw new IllegalStateException(
                    "Android external files alanı alınamadı."
            );
        }

        File logKlasoru =
                new File(
                        anaKlasor,
                        ProjeSabitleri.LOG_KLASORU
                );

        klasorOlustur(logKlasoru);

        logDosyasi =
                new File(
                        logKlasoru,
                        ProjeSabitleri.DEBUG_LOG_DOSYASI
                );

        logYaz("LOG BASLATILDI");
        logYaz("LOG_DOSYA=" + logDosyaYoluGetir());
    }

    /**
     * Dosyaya log satırı yazar.
     */
    public synchronized void logYaz(
            String mesaj
    ) {

        logBoyutunuKontrolEt();

        String satir =
                logSatiriOlustur(
                        mesaj
                );

        try (
                FileWriter writer =
                        new FileWriter(
                                logDosyasi,
                                true
                        )
        ) {

            writer.append(satir);
            writer.flush();

        } catch (IOException hata) {

            hata.printStackTrace();
        }
    }

    /**
     * Log dosya yolunu döndürür.
     */
    public String logDosyaYoluGetir() {

        return logDosyasi.getAbsolutePath();
    }

    /**
     * Log dosyasını temizler.
     */
    public synchronized void logTemizle() {

        try (
                FileWriter writer =
                        new FileWriter(
                                logDosyasi,
                                false
                        )
        ) {

            writer.write("");
            writer.flush();

        } catch (IOException hata) {

            hata.printStackTrace();
        }
    }

    /**
     * Log satırı oluşturur.
     */
    private String logSatiriOlustur(
            String mesaj
    ) {

        String guvenliMesaj =
                mesaj == null
                        ? "null"
                        : mesaj;

        String zaman =
                new SimpleDateFormat(
                        "HH:mm:ss",
                        Locale.getDefault()
                ).format(
                        new Date()
                );

        return "["
                + zaman
                + "] "
                + guvenliMesaj
                + "\n";
    }

    /**
     * Log dosyası büyürse temizler.
     */
    private void logBoyutunuKontrolEt() {

        if (!logDosyasi.exists()) {
            return;
        }

        if (logDosyasi.length() < MAKSIMUM_LOG_BOYUTU) {
            return;
        }

        logTemizle();
    }

    /**
     * Klasör yoksa oluşturur.
     */
    private void klasorOlustur(
            File klasor
    ) {

        if (klasor == null) {
            return;
        }

        if (!klasor.exists()) {
            //noinspection ResultOfMethodCallIgnored
            klasor.mkdirs();
        }
    }
            }
