package org.fy.kodeditoru.bridges;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Android pencere güvenli alan köprüsü.
 *
 * Bu sınıf:
 * - status bar bilgisini okur
 * - navigation bar bilgisini okur
 * - klavye/IME alt alanını okur
 * - cutout/notch güvenli alanını okur
 * - görünür pencere alanını raporlar
 * - bilgileri JSON metni olarak döndürür
 *
 * Kural:
 * - UI üretmez
 * - padding uygulamaz
 * - dosya işlemi yapmaz
 * - editör işlemi yapmaz
 * - sadece Android pencere gerçeklerini okur
 */
public final class WindowInsetsBridge {

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
     * Güncel pencere bilgilerini JSON metni olarak döndürür.
     */
    public String insetsJsonGetir() {

        JSONObject json = new JSONObject();

        try {

            Rect visibleFrame = new Rect();
            View decorView = decorViewGetir();

            decorView.getWindowVisibleDisplayFrame(
                    visibleFrame
            );

            json.put("sdk", Build.VERSION.SDK_INT);
            json.put("visible_left", visibleFrame.left);
            json.put("visible_top", visibleFrame.top);
            json.put("visible_right", visibleFrame.right);
            json.put("visible_bottom", visibleFrame.bottom);
            json.put("root_width", decorView.getRootView().getWidth());
            json.put("root_height", decorView.getRootView().getHeight());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                modernInsetsEkle(json, decorView);
            } else {
                eskiInsetsEkle(json, decorView, visibleFrame);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            WindowInsets insets =
                    decorViewGetir().getRootWindowInsets();

            if (insets == null) {
                return false;
            }

            return insets.isVisible(
                    WindowInsets.Type.ime()
            );
        }

        Rect visibleFrame = new Rect();
        View decorView = decorViewGetir();

        decorView.getWindowVisibleDisplayFrame(
                visibleFrame
        );

        int rootHeight =
                decorView.getRootView().getHeight();

        int fark =
                rootHeight - visibleFrame.bottom;

        return fark > dpToPx(120);
    }

    /**
     * Klavye alt inset değerini piksel döndürür.
     */
    public int klavyeAltPx() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            WindowInsets insets =
                    decorViewGetir().getRootWindowInsets();

            if (insets == null) {
                return 0;
            }

            return insets.getInsets(
                    WindowInsets.Type.ime()
            ).bottom;
        }

        Rect visibleFrame = new Rect();
        View decorView = decorViewGetir();

        decorView.getWindowVisibleDisplayFrame(
                visibleFrame
        );

        int rootHeight =
                decorView.getRootView().getHeight();

        int fark =
                rootHeight - visibleFrame.bottom;

        if (fark > dpToPx(120)) {
            return fark;
        }

        return 0;
    }

    /**
     * Android 11+ modern inset bilgilerini JSON yapısına ekler.
     */
    private void modernInsetsEkle(
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

        json.put("modern_insets", true);

        json.put("system_left", systemBars.left);
        json.put("system_top", systemBars.top);
        json.put("system_right", systemBars.right);
        json.put("system_bottom", systemBars.bottom);

        json.put("ime_left", ime.left);
        json.put("ime_top", ime.top);
        json.put("ime_right", ime.right);
        json.put("ime_bottom", ime.bottom);

        json.put(
                "ime_visible",
                insets.isVisible(WindowInsets.Type.ime())
        );

        cutoutEkle(json, insets);
    }

    /**
     * Android 10 ve altı için fallback inset bilgisi ekler.
     */
    private void eskiInsetsEkle(
            JSONObject json,
            View decorView,
            Rect visibleFrame
    ) throws JSONException {

        int rootHeight =
                decorView.getRootView().getHeight();

        int rootWidth =
                decorView.getRootView().getWidth();

        int altFark =
                rootHeight - visibleFrame.bottom;

        int sagFark =
                rootWidth - visibleFrame.right;

        boolean klavyeAcik =
                altFark > dpToPx(120);

        json.put("modern_insets", false);
        json.put("system_left", visibleFrame.left);
        json.put("system_top", visibleFrame.top);
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
    private void cutoutEkle(
            JSONObject json,
            WindowInsets insets
    ) throws JSONException {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            json.put("cutout_left", 0);
            json.put("cutout_top", 0);
            json.put("cutout_right", 0);
            json.put("cutout_bottom", 0);
            return;
        }

        DisplayCutout cutout =
                insets.getDisplayCutout();

        if (cutout == null) {
            json.put("cutout_left", 0);
            json.put("cutout_top", 0);
            json.put("cutout_right", 0);
            json.put("cutout_bottom", 0);
            return;
        }

        json.put("cutout_left", cutout.getSafeInsetLeft());
        json.put("cutout_top", cutout.getSafeInsetTop());
        json.put("cutout_right", cutout.getSafeInsetRight());
        json.put("cutout_bottom", cutout.getSafeInsetBottom());
    }

    /**
     * Activity decor view nesnesini döndürür.
     */
    private View decorViewGetir() {

        Window window = activity.getWindow();

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

        return Math.round(dp * density);
    }
                }
