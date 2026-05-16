package org.fy.kodeditoru.log;

import android.content.Context;

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
 *
 * Kural:
 * - UI üretmez.
 * - Toast göstermez.
 * - Thread yönetmez.
 * - ağ işlemi yapmaz.
 * - reklam sistemi çalıştırmaz.
 */
public final class LogYoneticisi {

    private static final String LOG_DOSYA_ADI = "debug.log";

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
                        "loglar"
                );

        if (!logKlasoru.exists()) {
            //noinspection ResultOfMethodCallIgnored
            logKlasoru.mkdirs();
        }

        logDosyasi =
                new File(
                        logKlasoru,
                        LOG_DOSYA_ADI
                );

        logYaz(
                "LOG BASLATILDI"
        );
    }

    /**
     * Dosyaya log satırı yazar.
     */
    public synchronized void logYaz(
            String mesaj
    ) {

        if (mesaj == null) {
            mesaj = "null";
        }

        String zaman =
                new SimpleDateFormat(
                        "HH:mm:ss",
                        Locale.getDefault()
                ).format(new Date());

        String satir =
                "[" + zaman + "] "
                + mesaj
                + "\n";

        try (
                FileWriter writer =
                        new FileWriter(
                                logDosyasi,
                                true
                        )
        ) {

            writer.append(satir);
            writer.flush();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Log dosya yolunu döndürür.
     */
    public String logDosyaYoluGetir() {

        return logDosyasi.getAbsolutePath();
    }
                  }
