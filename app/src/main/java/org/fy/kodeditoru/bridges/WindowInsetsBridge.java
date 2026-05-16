package org.fy.kodeditoru.bridges;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowMetrics;

import org.fy.kodeditoru.core.SafeAreaModel;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Android pencere güvenli alan köprüsü.
 *
 * Bu sınıf:
 * - status bar bilgisini okur.
 * - navigation bar bilgisini okur.
 * - klavye/IME alt alanını okur.
 * - cutout/notch güvenli alanını okur.
 * - görünür pencere alanını raporlar.
 * - kullanılabilir ekran alanını hesaplar.
 * - SafeAreaModel üretir.
 * - bilgileri JSON metni olarak döndürür.
 *
 * Kural:
 * - UI üretmez.
 * - padding uygulamaz.
 * - dosya işlemi yapmaz.
 * - editör işlemi yapmaz.
 * - sadece Android pencere gerçeklerini okur.
 */
public final class WindowInsetsBridge {

    private static final int KLAVYE_ESIK_DP = 120;
    private static final int TABLET_KISA_KENAR_ESIK_DP = 600;

    private final Activity activity;

    /**
     * WindowInsetsBridge oluşturur.
     */
    public WindowInsetsBridge(
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
     * Güncel güvenli alan modelini döndürür.
     */
    public SafeAreaModel safeAreaModelGetir() {

        View decorView =
                decorViewGetir();

        Rect visibleFrame =
                visibleFrameGetir(decorView);

        int rootWidth =
                guvenliRootWidthGetir(decorView, visibleFrame);

        int rootHeight =
                guvenliRootHeightGetir(decorView, visibleFrame);

        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        int keyboardBottom = 0;
        boolean keyboardVisible = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            WindowInsets insets =
                    decorView.getRootWindowInsets();

            if (insets != null) {

                android.graphics.Insets systemBars =
                        insets.getInsets(
                                WindowInsets.Type.systemBars()
                        );

                android.graphics.Insets ime =
                        insets.getInsets(
                                WindowInsets.Type.ime()
                        );

                left = systemBars.left;
                top = systemBars.top;
                right = systemBars.right;
                bottom = systemBars.bottom;

                keyboardBottom = ime.bottom;

                keyboardVisible =
                        insets.isVisible(
                                WindowInsets.Type.ime()
                        );

                int[] cutout =
                        cutoutDizisiGetir(insets);

                left = Math.max(left, cutout[0]);
                top = Math.max(top, cutout[1]);
                right = Math.max(right, cutout[2]);
                bottom = Math.max(bottom, cutout[3]);
            }

        } else {

            int altFark =
                    rootHeight - visibleFrame.bottom;

            keyboardVisible =
                    altFark > dpToPx(KLAVYE_ESIK_DP);

            left = Math.max(0, visibleFrame.left);
            top = Math.max(0, visibleFrame.top);
            right = Math.max(0, rootWidth - visibleFrame.right);
            bottom = keyboardVisible ? 0 : Math.max(0, altFark);
            keyboardBottom = keyboardVisible ? Math.max(0, altFark) : 0;
        }

        return new SafeAreaModel(
                left,
                top,
                right,
                bottom,
                keyboardBottom,
                keyboardVisible,
                rootWidth,
                rootHeight,
                tabletMi(rootWidth, rootHeight),
                yatayMi()
        );
    }

    /**
     * Güncel pencere bilgilerini JSON metni olarak döndürür.
     */
    public String insetsJsonGetir() {

        JSONObject json =
                new JSONObject();

        try {

            View decorView =
                    decorViewGetir();

            Rect visibleFrame =
                    visibleFrameGetir(decorView);

            SafeAreaModel safeArea =
                    safeAreaModelGetir();

            json.put("sdk", Build.VERSION.SDK_INT);

            json.put("visible_left", visibleFrame.left);
            json.put("visible_top", visibleFrame.top);
            json.put("visible_right", visibleFrame.right);
            json.put("visible_bottom", visibleFrame.bottom);

            json.put("root_width", safeArea.getScreenWidth());
            json.put("root_height", safeArea.getScreenHeight());

            json.put("safe_left", safeArea.getLeft());
            json.put("safe_top", safeArea.getTop());
            json.put("safe_right", safeArea.getRight());
            json.put("safe_bottom", safeArea.getBottom());

            json.put("keyboard_bottom", safeArea.getKeyboardBottom());
            json.put("keyboard_visible", safeArea.isKeyboardVisible());

            json.put("usable_width", safeArea.getUsableWidth());
            json.put("usable_height", safeArea.getUsableHeight());

            json.put("tablet", safeArea.isTablet());
            json.put("landscape", safeArea.isLandscape());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                modernInsetsJsonEkle(json, decorView);
            } else {
                eskiInsetsJsonEkle(json, decorView, visibleFrame);
            }

        } catch (JSONException hata) {
            return "{}";
        }

        return json.toString();
    }

    /**
     * Klavye açık mı bilgisini döndürür.
     */
    public boolean klavyeAcikMi() {

        return safeAreaModelGetir()
                .isKeyboardVisible();
    }

    /**
     * Klavye alt inset değerini piksel döndürür.
     */
    public int klavyeAltPx() {

        return safeAreaModelGetir()
                .getKeyboardBottom();
    }

    /**
     * Üst güvenli alanı piksel döndürür.
     */
    public int ustGuvenliAlanPx() {

        return safeAreaModelGetir()
                .getTop();
    }

    /**
     * Alt güvenli alanı piksel döndürür.
     */
    public int altGuvenliAlanPx() {

        return safeAreaModelGetir()
                .getEffectiveBottom();
    }

    /**
     * Android 11+ modern inset bilgilerini JSON yapısına ekler.
     */
    private void modernInsetsJsonEkle(
            JSONObject json,
            View decorView
    ) throws JSONException {

        WindowInsets insets =
                decorView.getRootWindowInsets();

        if (insets == null) {
            json.put("modern_insets", false);
            return;
        }

        android.graphics.Insets systemBars =
                insets.getInsets(
                        WindowInsets.Type.systemBars()
                );

        android.graphics.Insets ime =
                insets.getInsets(
                        WindowInsets.Type.ime()
                );

        android.graphics.Insets gestures =
                insets.getInsets(
                        WindowInsets.Type.systemGestures()
                );

        android.graphics.Insets mandatoryGestures =
                insets.getInsets(
                        WindowInsets.Type.mandatorySystemGestures()
                );

        android.graphics.Insets tappable =
                insets.getInsets(
                        WindowInsets.Type.tappableElement()
                );

        json.put("modern_insets", true);

        json.put("system_left", systemBars.left);
        json.put("system_top", systemBars.top);
        json.put("system_right", systemBars.right);
        json.put("system_bottom", systemBars.bottom);

        json.put("ime_left", ime.left);
        json.put("ime_top", ime.top);
        json.put("ime_right", ime.right);
        json.put("ime_bottom", ime.bottom);

        json.put("gesture_left", gestures.left);
        json.put("gesture_top", gestures.top);
        json.put("gesture_right", gestures.right);
        json.put("gesture_bottom", gestures.bottom);

        json.put("mandatory_gesture_left", mandatoryGestures.left);
        json.put("mandatory_gesture_top", mandatoryGestures.top);
        json.put("mandatory_gesture_right", mandatoryGestures.right);
        json.put("mandatory_gesture_bottom", mandatoryGestures.bottom);

        json.put("tappable_left", tappable.left);
        json.put("tappable_top", tappable.top);
        json.put("tappable_right", tappable.right);
        json.put("tappable_bottom", tappable.bottom);

        json.put(
                "ime_visible",
                insets.isVisible(
                        WindowInsets.Type.ime()
                )
        );

        cutoutJsonEkle(json, insets);
    }

    /**
     * Android 10 ve altı için fallback inset bilgisi ekler.
     */
    private void eskiInsetsJsonEkle(
            JSONObject json,
            View decorView,
            Rect visibleFrame
    ) throws JSONException {

        int rootHeight =
                guvenliRootHeightGetir(
                        decorView,
                        visibleFrame
                );

        int rootWidth =
                guvenliRootWidthGetir(
                        decorView,
                        visibleFrame
                );

        int altFark =
                rootHeight - visibleFrame.bottom;

        int sagFark =
                rootWidth - visibleFrame.right;

        boolean klavyeAcik =
                altFark > dpToPx(KLAVYE_ESIK_DP);

        json.put("modern_insets", false);
        json.put("system_left", Math.max(0, visibleFrame.left));
        json.put("system_top", Math.max(0, visibleFrame.top));
        json.put("system_right", Math.max(0, sagFark));
        json.put("system_bottom", klavyeAcik ? 0 : Math.max(0, altFark));

        json.put("ime_left", 0);
        json.put("ime_top", 0);
        json.put("ime_right", 0);
        json.put("ime_bottom", klavyeAcik ? Math.max(0, altFark) : 0);
        json.put("ime_visible", klavyeAcik);

        json.put("cutout_left", 0);
        json.put("cutout_top", 0);
        json.put("cutout_right", 0);
        json.put("cutout_bottom", 0);
    }

    /**
     * Cutout/notch güvenli alanlarını JSON yapısına ekler.
     */
    private void cutoutJsonEkle(
            JSONObject json,
            WindowInsets insets
    ) throws JSONException {

        int[] cutout =
                cutoutDizisiGetir(insets);

        json.put("cutout_left", cutout[0]);
        json.put("cutout_top", cutout[1]);
        json.put("cutout_right", cutout[2]);
        json.put("cutout_bottom", cutout[3]);
    }

    /**
     * Cutout değerlerini dizi olarak döndürür.
     */
    private int[] cutoutDizisiGetir(
            WindowInsets insets
    ) {

        int[] sonuc =
                new int[] {
                        0,
                        0,
                        0,
                        0
                };

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return sonuc;
        }

        DisplayCutout cutout =
                insets.getDisplayCutout();

        if (cutout == null) {
            return sonuc;
        }

        sonuc[0] = cutout.getSafeInsetLeft();
        sonuc[1] = cutout.getSafeInsetTop();
        sonuc[2] = cutout.getSafeInsetRight();
        sonuc[3] = cutout.getSafeInsetBottom();

        return sonuc;
    }

    /**
     * Görünür pencere çerçevesini döndürür.
     */
    private Rect visibleFrameGetir(
            View decorView
    ) {

        Rect visibleFrame =
                new Rect();

        decorView.getWindowVisibleDisplayFrame(
                visibleFrame
        );

        return visibleFrame;
    }

    /**
     * Güvenli root genişliğini döndürür.
     */
    private int guvenliRootWidthGetir(
            View decorView,
            Rect visibleFrame
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            WindowMetrics metrics =
                    activity.getWindowManager()
                            .getCurrentWindowMetrics();

            if (metrics != null) {
                return metrics.getBounds().width();
            }
        }

        int rootWidth =
                decorView.getRootView().getWidth();

        if (rootWidth > 0) {
            return rootWidth;
        }

        return Math.max(
                0,
                visibleFrame.width()
        );
    }

    /**
     * Güvenli root yüksekliğini döndürür.
     */
    private int guvenliRootHeightGetir(
            View decorView,
            Rect visibleFrame
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            WindowMetrics metrics =
                    activity.getWindowManager()
                            .getCurrentWindowMetrics();

            if (metrics != null) {
                return metrics.getBounds().height();
            }
        }

        int rootHeight =
                decorView.getRootView().getHeight();

        if (rootHeight > 0) {
            return rootHeight;
        }

        return Math.max(
                0,
                visibleFrame.height()
        );
    }

    /**
     * Tablet sınıfını hesaplar.
     */
    private boolean tabletMi(
            int widthPx,
            int heightPx
    ) {

        float density =
                activity.getResources()
                        .getDisplayMetrics()
                        .density;

        if (density <= 0f) {
            density = 1f;
        }

        int kisaKenarPx =
                Math.min(
                        widthPx,
                        heightPx
                );

        float kisaKenarDp =
                kisaKenarPx / density;

        return kisaKenarDp >= TABLET_KISA_KENAR_ESIK_DP;
    }

    /**
     * Yatay mod bilgisini döndürür.
     */
    private boolean yatayMi() {

        return activity.getResources()
                .getConfiguration()
                .orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * Activity decor view nesnesini döndürür.
     */
    private View decorViewGetir() {

        Window window =
                activity.getWindow();

        if (window == null) {
            throw new IllegalStateException(
                    "Window bulunamadı."
            );
        }

        return window.getDecorView();
    }

    /**
     * Dp değerini piksele çevirir.
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

        return Math.round(
                dp * density
        );
    }
        }
