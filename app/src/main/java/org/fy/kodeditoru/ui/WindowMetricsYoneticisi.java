package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.WindowMetrics;

/**
 * Android pencere ölçü yöneticisi.
 *
 * Bu sınıf:
 * - gerçek pencere genişliğini hesaplar.
 * - gerçek pencere yüksekliğini hesaplar.
 * - kullanılabilir kısa kenarı hesaplar.
 * - telefon/tablet ayrımı yapar.
 * - yatay/dikey modu belirler.
 * - foldable/tablet benzeri büyük ekranları standartlaştırır.
 * - ekran yoğunluğu bilgisi sağlar.
 *
 * Kural:
 * - UI üretmez.
 * - padding uygulamaz.
 * - inset yönetmez.
 * - dosya işlemi yapmaz.
 * - editör işlemi yapmaz.
 */
public final class WindowMetricsYoneticisi {

    private static final int TABLET_KISA_KENAR_DP = 600;

    private final Activity activity;

    /**
     * WindowMetricsYoneticisi oluşturur.
     */
    public WindowMetricsYoneticisi(
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
     * Gerçek pencere genişliğini döndürür.
     */
    public int pencereGenisligiPx() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            WindowMetrics metrics =
                    activity.getWindowManager()
                            .getCurrentWindowMetrics();

            if (metrics != null) {
                return metrics.getBounds().width();
            }
        }

        DisplayMetrics metrics =
                displayMetricsGetir();

        return metrics.widthPixels;
    }

    /**
     * Gerçek pencere yüksekliğini döndürür.
     */
    public int pencereYuksekligiPx() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            WindowMetrics metrics =
                    activity.getWindowManager()
                            .getCurrentWindowMetrics();

            if (metrics != null) {
                return metrics.getBounds().height();
            }
        }

        DisplayMetrics metrics =
                displayMetricsGetir();

        return metrics.heightPixels;
    }

    /**
     * Kısa kenarı piksel döndürür.
     */
    public int kisaKenarPx() {

        return Math.min(
                pencereGenisligiPx(),
                pencereYuksekligiPx()
        );
    }

    /**
     * Uzun kenarı piksel döndürür.
     */
    public int uzunKenarPx() {

        return Math.max(
                pencereGenisligiPx(),
                pencereYuksekligiPx()
        );
    }

    /**
     * Ekran yoğunluğunu döndürür.
     */
    public float density() {

        float density =
                displayMetricsGetir().density;

        if (density <= 0f) {
            return 1f;
        }

        return density;
    }

    /**
     * Kısa kenarı dp döndürür.
     */
    public float kisaKenarDp() {

        return kisaKenarPx() / density();
    }

    /**
     * Tablet sınıfı mı döndürür.
     */
    public boolean tabletMi() {

        return kisaKenarDp()
                >= TABLET_KISA_KENAR_DP;
    }

    /**
     * Yatay mod mu döndürür.
     */
    public boolean yatayMi() {

        return activity.getResources()
                .getConfiguration()
                .orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * Büyük ekran sınıfı mı döndürür.
     */
    public boolean buyukEkranMi() {

        return kisaKenarDp() >= 720f;
    }

    /**
     * Foldable benzeri ekran mı döndürür.
     */
    public boolean foldableBenzeriMi() {

        float oran =
                ekranOrani();

        return oran > 1.8f
                && kisaKenarDp() >= 500f;
    }

    /**
     * Ekran oranını döndürür.
     */
    public float ekranOrani() {

        int uzun =
                uzunKenarPx();

        int kisa =
                kisaKenarPx();

        if (kisa <= 0) {
            return 1f;
        }

        return (float) uzun / (float) kisa;
    }

    /**
     * Ekran sınıfını metin olarak döndürür.
     */
    public String ekranSinifi() {

        if (buyukEkranMi()) {
            return "buyuk_ekran";
        }

        if (tabletMi()) {
            return "tablet";
        }

        if (foldableBenzeriMi()) {
            return "foldable";
        }

        return "telefon";
    }

    /**
     * Kısa özet döndürür.
     */
    public String ozet() {

        return "WindowMetrics{"
                + "width=" + pencereGenisligiPx()
                + ", height=" + pencereYuksekligiPx()
                + ", density=" + density()
                + ", shortDp=" + kisaKenarDp()
                + ", ratio=" + ekranOrani()
                + ", tablet=" + tabletMi()
                + ", landscape=" + yatayMi()
                + ", class=" + ekranSinifi()
                + '}';
    }

    /**
     * DisplayMetrics nesnesini döndürür.
     */
    private DisplayMetrics displayMetricsGetir() {

        DisplayMetrics metrics =
                new DisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            activity.getDisplay()
                    .getRealMetrics(metrics);

            return metrics;
        }

        WindowManager windowManager =
                activity.getWindowManager();

        if (windowManager != null) {

            windowManager.getDefaultDisplay()
                    .getRealMetrics(metrics);
        }

        return metrics;
    }
          }
