package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;

/**
 * Android gesture inset yöneticisi.
 *
 * Bu sınıf:
 * - sistem gesture alanlarını okur.
 * - zorunlu gesture alanlarını okur.
 * - tappable element alanlarını okur.
 * - alt/yan güvenli hareket boşluklarını standartlaştırır.
 *
 * Kural:
 * - UI üretmez.
 * - padding uygulamaz.
 * - dosya işlemi yapmaz.
 * - editör işlemi yapmaz.
 * - sadece gesture inset değerlerini okur.
 */
public final class GestureInsetsYoneticisi {

    private final Activity activity;

    /**
     * GestureInsetsYoneticisi oluşturur.
     */
    public GestureInsetsYoneticisi(
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
     * Sol gesture alanını döndürür.
     */
    public int gestureLeftPx() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return 0;
        }

        WindowInsets insets =
                insetsGetir();

        if (insets == null) {
            return 0;
        }

        return insets.getSystemGestureInsets().left;
    }

    /**
     * Üst gesture alanını döndürür.
     */
    public int gestureTopPx() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return 0;
        }

        WindowInsets insets =
                insetsGetir();

        if (insets == null) {
            return 0;
        }

        return insets.getSystemGestureInsets().top;
    }

    /**
     * Sağ gesture alanını döndürür.
     */
    public int gestureRightPx() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return 0;
        }

        WindowInsets insets =
                insetsGetir();

        if (insets == null) {
            return 0;
        }

        return insets.getSystemGestureInsets().right;
    }

    /**
     * Alt gesture alanını döndürür.
     */
    public int gestureBottomPx() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return 0;
        }

        WindowInsets insets =
                insetsGetir();

        if (insets == null) {
            return 0;
        }

        return insets.getSystemGestureInsets().bottom;
    }

    /**
     * Zorunlu alt gesture alanını döndürür.
     */
    public int mandatoryGestureBottomPx() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return 0;
        }

        WindowInsets insets =
                insetsGetir();

        if (insets == null) {
            return 0;
        }

        return insets.getMandatorySystemGestureInsets().bottom;
    }

    /**
     * Tıklanabilir alt sistem alanını döndürür.
     */
    public int tappableBottomPx() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return 0;
        }

        WindowInsets insets =
                insetsGetir();

        if (insets == null) {
            return 0;
        }

        return insets.getTappableElementInsets().bottom;
    }

    /**
     * UI için kullanılacak güvenli alt gesture boşluğunu döndürür.
     */
    public int etkiliAltGesturePx() {

        return Math.max(
                gestureBottomPx(),
                Math.max(
                        mandatoryGestureBottomPx(),
                        tappableBottomPx()
                )
        );
    }

    /**
     * Gesture özetini döndürür.
     */
    public String ozet() {

        return "GestureInsets{"
                + "left=" + gestureLeftPx()
                + ", top=" + gestureTopPx()
                + ", right=" + gestureRightPx()
                + ", bottom=" + gestureBottomPx()
                + ", mandatoryBottom=" + mandatoryGestureBottomPx()
                + ", tappableBottom=" + tappableBottomPx()
                + ", effectiveBottom=" + etkiliAltGesturePx()
                + '}';
    }

    /**
     * Root WindowInsets döndürür.
     */
    private WindowInsets insetsGetir() {

        Window window =
                activity.getWindow();

        if (window == null) {
            return null;
        }

        View decorView =
                window.getDecorView();

        if (decorView == null) {
            return null;
        }

        return decorView.getRootWindowInsets();
    }
  }
