package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;

import org.fy.kodeditoru.bridges.WindowInsetsBridge;

/**
 * Klavye ve sistem güvenli alan yöneticisi.
 *
 * Bu sınıf:
 * - klavye görünürlüğünü algılar
 * - WindowInsetsBridge üzerinden gerçek klavye alt alanını okur
 * - navigation bar / sistem bar alt boşluğunu hesaplar
 * - kök alana güvenli padding uygular
 * - EditText alanının klavye altında kalmasını engellemeye çalışır
 *
 * Kural:
 * - UI üretmez
 * - dosya işlemi yapmaz
 * - editör içeriği yönetmez
 * - XML önizleme üretmez
 * - sadece mevcut View ölçülerini düzenler
 */
public final class KlavyeInsetsYoneticisi {

    private final Activity activity;
    private final View kokView;
    private final WindowInsetsBridge windowInsetsBridge;

    private final int temelSol;
    private final int temelUst;
    private final int temelSag;
    private final int temelAlt;

    /**
     * Klavye inset yöneticisi oluşturur.
     */
    public KlavyeInsetsYoneticisi(
            Activity activity,
            View kokView
    ) {

        if (activity == null) {
            throw new IllegalArgumentException(
                    "Activity null olamaz."
            );
        }

        if (kokView == null) {
            throw new IllegalArgumentException(
                    "Kök View null olamaz."
            );
        }

        this.activity = activity;
        this.kokView = kokView;
        this.windowInsetsBridge = new WindowInsetsBridge(activity);

        this.temelSol = kokView.getPaddingLeft();
        this.temelUst = kokView.getPaddingTop();
        this.temelSag = kokView.getPaddingRight();
        this.temelAlt = kokView.getPaddingBottom();
    }

    /**
     * Klavye ve sistem alanı dinleyicisini başlatır.
     */
    public void baslat() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            modernInsetsBaslat();
            return;
        }

        eskiInsetsBaslat();
    }

    /**
     * Android 11+ için modern inset sistemi.
     */
    private void modernInsetsBaslat() {

        kokView.setOnApplyWindowInsetsListener(
                (view, insets) -> {

                    int imeAlt = insets.getInsets(
                            WindowInsets.Type.ime()
                    ).bottom;

                    int sistemAlt = insets.getInsets(
                            WindowInsets.Type.systemBars()
                    ).bottom;

                    int bridgeKlavyeAlt =
                            windowInsetsBridge.klavyeAltPx();

                    int uygulanacakAlt = Math.max(
                            Math.max(imeAlt, sistemAlt),
                            bridgeKlavyeAlt
                    );

                    paddingUygula(uygulanacakAlt);

                    return insets;
                }
        );

        kokView.requestApplyInsets();
    }

    /**
     * Android 10 ve altı için görünür ekran alanı takibi.
     */
    private void eskiInsetsBaslat() {

        kokView.getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> {

                    int bridgeKlavyeAlt =
                            windowInsetsBridge.klavyeAltPx();

                    int fallbackAlt =
                            fallbackKlavyeAltPx();

                    int uygulanacakAlt = Math.max(
                            bridgeKlavyeAlt,
                            fallbackAlt
                    );

                    paddingUygula(uygulanacakAlt);
                });
    }

    /**
     * Eski Android sürümlerinde klavye alt boşluğunu hesaplar.
     */
    private int fallbackKlavyeAltPx() {

        Rect gorunenAlan = new Rect();

        kokView.getWindowVisibleDisplayFrame(
                gorunenAlan
        );

        int kokYukseklik =
                kokView.getRootView().getHeight();

        int fark =
                kokYukseklik - gorunenAlan.bottom;

        int esik =
                dpToPx(120);

        if (fark > esik) {
            return Math.max(0, fark);
        }

        return 0;
    }

    /**
     * Kök View padding değerini güvenli uygular.
     */
    private void paddingUygula(
            int altPadding
    ) {

        kokView.setPadding(
                temelSol,
                temelUst,
                temelSag,
                temelAlt + Math.max(0, altPadding)
        );
    }

    /**
     * Dp değerini piksel değerine çevirir.
     */
    private int dpToPx(
            int dp
    ) {

        float density =
                activity.getResources()
                        .getDisplayMetrics()
                        .density;

        if (density <= 0f) {
            density = 1f;
        }

        return Math.round(dp * density);
    }
}