package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;

import org.fy.kodeditoru.bridges.WindowInsetsBridge;
import org.fy.kodeditoru.core.SafeAreaModel;

/**
 * Android güvenli ekran alanı yöneticisi.
 *
 * Bu sınıf:
 * - WindowInsetsBridge üzerinden güvenli alan modelini alır.
 * - status bar ve navigation bar altında kalan UI sorunlarını azaltır.
 * - klavye açılınca alt güvenli alanı günceller.
 * - root view üzerine dinamik padding uygular.
 * - ekran/inset değişimlerinde UI paddingini yeniden hesaplar.
 *
 * Kural:
 * - dosya işlemi yapmaz.
 * - editör içeriğine müdahale etmez.
 * - XML önizleme üretmez.
 * - reklam/ödeme işlemi yapmaz.
 * - sadece güvenli alan padding yönetir.
 */
public final class SafeAreaYoneticisi {

    private final Activity activity;
    private final View hedefView;
    private final WindowInsetsBridge windowInsetsBridge;

    private int temelLeft;
    private int temelTop;
    private int temelRight;
    private int temelBottom;

    private SafeAreaModel sonModel;

    /**
     * SafeAreaYoneticisi oluşturur.
     */
    public SafeAreaYoneticisi(
            Activity activity,
            View hedefView
    ) {

        if (activity == null) {
            throw new IllegalArgumentException(
                    "Activity null olamaz."
            );
        }

        if (hedefView == null) {
            throw new IllegalArgumentException(
                    "Hedef view null olamaz."
            );
        }

        this.activity = activity;
        this.hedefView = hedefView;
        this.windowInsetsBridge = new WindowInsetsBridge(activity);

        temelPaddingleriKaydet();
    }

    /**
     * Güvenli alan dinleyicisini başlatır.
     */
    public void baslat() {

        hedefView.post(this::guvenliAlanUygula);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {

            hedefView.setOnApplyWindowInsetsListener(
                    (view, insets) -> {

                        guvenliAlanUygula();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            return WindowInsets.CONSUMED;
                        }

                        return insets;
                    }
            );

            hedefView.requestApplyInsets();
        }

        hedefView.addOnLayoutChangeListener(
                (view,
                 left,
                 top,
                 right,
                 bottom,
                 oldLeft,
                 oldTop,
                 oldRight,
                 oldBottom) -> {

                    if (right - left != oldRight - oldLeft
                            || bottom - top != oldBottom - oldTop) {

                        guvenliAlanUygula();
                    }
                }
        );
    }

    /**
     * Güncel güvenli alanı root view paddingine uygular.
     */
    public void guvenliAlanUygula() {

        SafeAreaModel model =
                windowInsetsBridge.safeAreaModelGetir();

        sonModel = model;

        int left =
                temelLeft
                + model.getLeft();

        int top =
                temelTop
                + model.getTop();

        int right =
                temelRight
                + model.getRight();

        int bottom =
                temelBottom
                + model.getEffectiveBottom();

        hedefView.setPadding(
                left,
                top,
                right,
                bottom
        );
    }

    /**
     * Son güvenli alan modelini döndürür.
     */
    public SafeAreaModel getSonModel() {
        return sonModel;
    }

    /**
     * Güvenli alan özetini döndürür.
     */
    public String ozetGetir() {

        if (sonModel == null) {
            return "SafeAreaModel yok.";
        }

        return sonModel.ozet();
    }

    /**
     * Hedef view'in başlangıç paddinglerini saklar.
     */
    private void temelPaddingleriKaydet() {

        temelLeft =
                hedefView.getPaddingLeft();

        temelTop =
                hedefView.getPaddingTop();

        temelRight =
                hedefView.getPaddingRight();

        temelBottom =
                hedefView.getPaddingBottom();
    }
  }
