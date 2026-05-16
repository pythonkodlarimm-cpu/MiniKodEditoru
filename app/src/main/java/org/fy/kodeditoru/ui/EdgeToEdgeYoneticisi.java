package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Android edge-to-edge pencere yöneticisi.
 *
 * Bu sınıf:
 * - status bar rengini yönetir.
 * - navigation bar rengini yönetir.
 * - edge-to-edge pencere davranışını uygular.
 * - sistem bar görünürlüğünü standartlaştırır.
 * - Android sürüm farklarını tek merkezde yönetir.
 *
 * Kural:
 * - UI layout üretmez.
 * - padding uygulamaz.
 * - güvenli alan hesabı yapmaz.
 * - dosya işlemi yapmaz.
 * - editör işlemi yapmaz.
 */
public final class EdgeToEdgeYoneticisi {

    private final Activity activity;

    /**
     * EdgeToEdgeYoneticisi oluşturur.
     */
    public EdgeToEdgeYoneticisi(
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
     * Edge-to-edge pencere davranışını uygular.
     */
    public void uygula() {

        Window window =
                activity.getWindow();

        if (window == null) {
            return;
        }

        sistemBarRenkleriniUygula(window);
        pencereYerlesiminiUygula(window);
    }

    /**
     * Sistem bar renklerini uygular.
     */
    private void sistemBarRenkleriniUygula(
            Window window
    ) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        );

        window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
        );

        window.setStatusBarColor(
                Color.TRANSPARENT
        );

        window.setNavigationBarColor(
                Color.BLACK
        );
    }

    /**
     * Pencere yerleşim davranışını uygular.
     */
    private void pencereYerlesiminiUygula(
            Window window
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            window.setDecorFitsSystemWindows(false);
            return;
        }

        View decorView =
                window.getDecorView();

        if (decorView == null) {
            return;
        }

        int flags =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        decorView.setSystemUiVisibility(flags);
    }
          }
