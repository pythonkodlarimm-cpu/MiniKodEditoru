package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Akıllı ekran ölçek yöneticisi.
 *
 * Bu sınıf:
 * - pencere ölçülerini WindowMetricsYoneticisi üzerinden alır.
 * - density ve scaledDensity bilgisini hesaplar.
 * - telefon/tablet/büyük ekran/yatay durumunu kullanır.
 * - UI için akıllı padding, yükseklik ve yazı boyutları üretir.
 *
 * Kural:
 * - UI üretmez.
 * - View oluşturmaz.
 * - dosya işlemi yapmaz.
 * - editör işlemi yapmaz.
 * - XML önizleme üretmez.
 * - sadece ölçü hesabı yapar.
 */
public final class EkranOlcekYoneticisi {

    private final WindowMetricsYoneticisi windowMetricsYoneticisi;

    private final int ekranGenislikPx;
    private final int ekranYukseklikPx;

    private final float density;
    private final float scaledDensity;

    private final int kisaKenarDp;
    private final int uzunKenarDp;

    private final boolean yatayMod;
    private final boolean tablet;
    private final boolean buyukEkran;

    /**
     * Cihaz ekran ölçü yöneticisi oluşturur.
     */
    public EkranOlcekYoneticisi(
            Activity activity
    ) {

        if (activity == null) {
            throw new IllegalArgumentException(
                    "Activity null olamaz."
            );
        }

        windowMetricsYoneticisi =
                new WindowMetricsYoneticisi(
                        activity
                );

        DisplayMetrics metrics =
                activity.getResources()
                        .getDisplayMetrics();

        density =
                metrics.density <= 0f
                        ? 1f
                        : metrics.density;

        scaledDensity =
                metrics.scaledDensity <= 0f
                        ? density
                        : metrics.scaledDensity;

        ekranGenislikPx =
                windowMetricsYoneticisi.pencereGenisligiPx();

        ekranYukseklikPx =
                windowMetricsYoneticisi.pencereYuksekligiPx();

        kisaKenarDp =
                Math.round(
                        windowMetricsYoneticisi.kisaKenarDp()
                );

        uzunKenarDp =
                Math.round(
                        windowMetricsYoneticisi.uzunKenarPx()
                                / density
                );

        yatayMod =
                windowMetricsYoneticisi.yatayMi();

        tablet =
                windowMetricsYoneticisi.tabletMi();

        buyukEkran =
                windowMetricsYoneticisi.buyukEkranMi();
    }

    /**
     * Ekran genişliğini piksel döndürür.
     */
    public int getEkranGenislikPx() {
        return ekranGenislikPx;
    }

    /**
     * Ekran yüksekliğini piksel döndürür.
     */
    public int getEkranYukseklikPx() {
        return ekranYukseklikPx;
    }

    /**
     * Density değerini döndürür.
     */
    public float getDensity() {
        return density;
    }

    /**
     * Scaled density değerini döndürür.
     */
    public float getScaledDensity() {
        return scaledDensity;
    }

    /**
     * Kısa kenarı dp döndürür.
     */
    public int getKisaKenarDp() {
        return kisaKenarDp;
    }

    /**
     * Uzun kenarı dp döndürür.
     */
    public int getUzunKenarDp() {
        return uzunKenarDp;
    }

    /**
     * Yatay mod bilgisini döndürür.
     */
    public boolean isYatayMod() {
        return yatayMod;
    }

    /**
     * Tablet sınıfı cihaz bilgisini döndürür.
     */
    public boolean isTablet() {
        return tablet;
    }

    /**
     * Büyük ekran sınıfı bilgisini döndürür.
     */
    public boolean isBuyukEkran() {
        return buyukEkran;
    }

    /**
     * Ana ekran dış boşluğunu piksel üretir.
     */
    public int anaPaddingPx() {

        if (buyukEkran) {
            return dpToPx(28);
        }

        if (tablet) {
            return dpToPx(22);
        }

        if (kisaKenarDp <= 360) {
            return dpToPx(10);
        }

        return dpToPx(14);
    }

    /**
     * Üst bar yüksekliğini piksel üretir.
     */
    public int ustBarYukseklikPx() {

        if (buyukEkran) {
            return dpToPx(80);
        }

        if (tablet) {
            return dpToPx(74);
        }

        if (kisaKenarDp <= 360) {
            return dpToPx(56);
        }

        return dpToPx(64);
    }

    /**
     * Buton yüksekliğini piksel üretir.
     */
    public int butonYukseklikPx() {

        if (buyukEkran) {
            return dpToPx(54);
        }

        if (tablet) {
            return dpToPx(52);
        }

        return dpToPx(48);
    }

    /**
     * Kart iç boşluğunu piksel üretir.
     */
    public int kartPaddingPx() {

        if (buyukEkran) {
            return dpToPx(20);
        }

        if (tablet) {
            return dpToPx(18);
        }

        if (kisaKenarDp <= 360) {
            return dpToPx(10);
        }

        return dpToPx(14);
    }

    /**
     * Alanlar arası boşluğu piksel üretir.
     */
    public int alanBoslukPx() {

        if (buyukEkran) {
            return dpToPx(18);
        }

        if (tablet) {
            return dpToPx(16);
        }

        if (kisaKenarDp <= 360) {
            return dpToPx(8);
        }

        return dpToPx(12);
    }

    /**
     * Editör yazı boyutunu sp üretir.
     */
    public float editorYaziBoyutuSp() {

        if (buyukEkran) {
            return 16f;
        }

        if (tablet) {
            return 15.5f;
        }

        if (kisaKenarDp <= 360) {
            return 13f;
        }

        return 14f;
    }

    /**
     * Başlık yazı boyutunu sp üretir.
     */
    public float baslikYaziBoyutuSp() {

        if (buyukEkran) {
            return 25f;
        }

        if (tablet) {
            return 23f;
        }

        if (kisaKenarDp <= 360) {
            return 18f;
        }

        return 21f;
    }

    /**
     * Durum metni yazı boyutunu sp üretir.
     */
    public float durumYaziBoyutuSp() {

        if (tablet || buyukEkran) {
            return 14f;
        }

        return 12.5f;
    }

    /**
     * Editör ağırlığını üretir.
     */
    public float editorAgirlik() {

        if (yatayMod && buyukEkran) {
            return 1.15f;
        }

        if (yatayMod) {
            return 1.25f;
        }

        return 1.45f;
    }

    /**
     * Önizleme ağırlığını üretir.
     */
    public float onizlemeAgirlik() {

        if (yatayMod && buyukEkran) {
            return 0.85f;
        }

        if (yatayMod) {
            return 0.75f;
        }

        if (tablet || buyukEkran) {
            return 0.9f;
        }

        return 0.8f;
    }

    /**
     * Piksel değerini dp değerine çevirir.
     */
    public int pxToDp(
            int px
    ) {

        return Math.round(
                px / density
        );
    }

    /**
     * Dp değerini piksel değerine çevirir.
     */
    public int dpToPx(
            int dp
    ) {

        return Math.round(
                dp * density
        );
    }

    /**
     * Sp değerini piksel değerine çevirir.
     */
    public int spToPx(
            float sp
    ) {

        return Math.round(
                sp * scaledDensity
        );
    }

    /**
     * Ölçek özetini döndürür.
     */
    public String ozet() {

        return "EkranOlcek{"
                + "width=" + ekranGenislikPx
                + ", height=" + ekranYukseklikPx
                + ", density=" + density
                + ", scaledDensity=" + scaledDensity
                + ", shortDp=" + kisaKenarDp
                + ", longDp=" + uzunKenarDp
                + ", yatay=" + yatayMod
                + ", tablet=" + tablet
                + ", buyukEkran=" + buyukEkran
                + '}';
    }
                }
