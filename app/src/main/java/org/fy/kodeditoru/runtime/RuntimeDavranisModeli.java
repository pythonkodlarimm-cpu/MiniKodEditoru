package org.fy.kodeditoru.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Runtime davranış veri modeli.
 *
 * Bu sınıf:
 * - tek bir View davranışını temsil eder.
 * - tıklama sonrası gösterilecek Toast mesajını taşır.
 * - setText uygulanacak hedef View bilgilerini taşır.
 * - runtime davranış yöneticisine sade veri modeli sağlar.
 *
 * Örnek:
 * - buttonKaydet tıklandı
 * - Toast göster
 * - sonucText setText("Kaydedildi")
 *
 * Kural:
 * - UI üretmez.
 * - gerçek Java çalıştırmaz.
 * - thread başlatmaz.
 * - dosya işlemi yapmaz.
 * - XML parse etmez.
 */
public final class RuntimeDavranisModeli {

    private final String tetikleyiciViewId;

    private String toastMesaji;

    private final Map<String, String> setTextHaritasi;

    /**
     * Runtime davranış modeli oluşturur.
     */
    public RuntimeDavranisModeli(
            String tetikleyiciViewId
    ) {

        if (tetikleyiciViewId == null
                || tetikleyiciViewId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Tetikleyici View id boş olamaz."
            );
        }

        this.tetikleyiciViewId =
                tetikleyiciViewId;

        this.toastMesaji = "";

        this.setTextHaritasi =
                new HashMap<>();
    }

    /**
     * Tetikleyici View id döndürür.
     */
    public String getTetikleyiciViewId() {

        return tetikleyiciViewId;
    }

    /**
     * Toast mesajı ayarlar.
     */
    public void toastMesajiAyarla(
            String toastMesaji
    ) {

        this.toastMesaji =
                toastMesaji == null
                        ? ""
                        : toastMesaji;
    }

    /**
     * Toast mesajı döndürür.
     */
    public String getToastMesaji() {

        return toastMesaji;
    }

    /**
     * Toast davranışı var mı kontrol eder.
     */
    public boolean toastVarMi() {

        return !toastMesaji.trim().isEmpty();
    }

    /**
     * setText davranışı ekler.
     */
    public void setTextEkle(
            String hedefViewId,
            String yeniMetin
    ) {

        if (hedefViewId == null
                || hedefViewId.trim().isEmpty()) {

            return;
        }

        setTextHaritasi.put(
                hedefViewId,
                yeniMetin == null
                        ? ""
                        : yeniMetin
        );
    }

    /**
     * setText davranış haritasını döndürür.
     */
    public Map<String, String> getSetTextHaritasi() {

        return Collections.unmodifiableMap(
                setTextHaritasi
        );
    }

    /**
     * setText davranışı var mı kontrol eder.
     */
    public boolean setTextDavranisiVarMi() {

        return !setTextHaritasi.isEmpty();
    }
}
