package org.fy.kodeditoru;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Profesyonel splash ekran Activity modülü.
 *
 * Bu sınıf:
 * - uygulama açılış ekranını oluşturur
 * - logo animasyonu oynatır
 * - edge-to-edge pencere davranışı uygular
 * - belirli süre sonunda MainActivity ekranına geçer
 *
 * Kural:
 * - dosya işlemi yapmaz
 * - editör sistemi çalıştırmaz
 * - XML önizleme üretmez
 * - sadece splash ekranı yönetir
 */
public final class SplashActivity extends Activity {

    private static final long SPLASH_SURE_MS = 1800L;

    private Handler handler;

    /**
     * Splash ekranını oluşturur.
     */
    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        pencereyiHazirla();

        splashArayuzuOlustur();

        anaEkranaGecisiPlanla();
    }

    /**
     * Android pencere davranışını hazırlar.
     */
    private void pencereyiHazirla() {

        Window window = getWindow();

        if (window == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.clearFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            );

            window.addFlags(
                    WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            );

            window.setStatusBarColor(0x00000000);

            window.setNavigationBarColor(0xFF000000);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            window.setDecorFitsSystemWindows(false);
        }
    }

    /**
     * Splash arayüzünü oluşturur.
     */
    private void splashArayuzuOlustur() {

        FrameLayout root = new FrameLayout(this);

        root.setBackgroundResource(
                R.drawable.splash_background
        );

        ImageView logo = new ImageView(this);

        logo.setImageResource(
                R.mipmap.ic_launcher
        );

        int logoBoyut = dpToPx(120);

        FrameLayout.LayoutParams logoParams =
                new FrameLayout.LayoutParams(
                        logoBoyut,
                        logoBoyut
                );

        logoParams.gravity = Gravity.CENTER;

        logo.setLayoutParams(logoParams);

        root.addView(logo);

        setContentView(root);

        logoAnimasyonuBaslat(logo);
    }

    /**
     * Logo animasyonunu başlatır.
     */
    private void logoAnimasyonuBaslat(
            ImageView logo
    ) {

        PropertyValuesHolder scaleX =
                PropertyValuesHolder.ofFloat(
                        "scaleX",
                        0.82f,
                        1f
                );

        PropertyValuesHolder scaleY =
                PropertyValuesHolder.ofFloat(
                        "scaleY",
                        0.82f,
                        1f
                );

        PropertyValuesHolder alpha =
                PropertyValuesHolder.ofFloat(
                        "alpha",
                        0.25f,
                        1f
                );

        ObjectAnimator animator =
                ObjectAnimator.ofPropertyValuesHolder(
                        logo,
                        scaleX,
                        scaleY,
                        alpha
                );

        animator.setDuration(900L);

        animator.setInterpolator(
                new DecelerateInterpolator()
        );

        animator.start();
    }

    /**
     * MainActivity geçişini planlar.
     */
    private void anaEkranaGecisiPlanla() {

        handler = new Handler(
                Looper.getMainLooper()
        );

        handler.postDelayed(() -> {

            Intent intent =
                    new Intent(
                            SplashActivity.this,
                            MainActivity.class
                    );

            startActivity(intent);

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );

            finish();

        }, SPLASH_SURE_MS);
    }

    /**
     * Activity kapanırken handler temizler.
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * Dp değerini piksele çevirir.
     */
    private int dpToPx(
            int dp
    ) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        if (density <= 0f) {
            density = 1f;
        }

        return Math.round(dp * density);
    }
}