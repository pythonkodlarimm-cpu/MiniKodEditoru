package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowMetrics;

/**
 * Akıllı ekran ölçek yöneticisi.
 *
 * Bu sınıf:
 * - cihaz ekran genişliğini ve yüksekliğini okur
 * - density bilgisini hesaplar
 * - telefon/tablet ayrımı yapar
 * - yatay/dikey ekran durumunu belirler
 * - UI için akıllı padding, yükseklik ve yazı boyutları üretir
 *
 * Kural:
 * - UI üretmez
 * - View oluşturmaz
 * - dosya işlemi yapmaz
 * - editör işlemi yapmaz
 * - XML önizleme üretmez
 * - sadece ölçü hesabı yapar
 */
public final class EkranOlcekYoneticisi {

    private final Activity activity;
    private final int ekranGenislikPx;
    private final int ekranYukseklikPx;
    private final float density;
    private final float scaledDensity;
    private final int kisaKenarDp;
    private final int uzunKenarDp;
    private final boolean yatayMod;
    private final boolean tablet;

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

        this.activity = activity;

        DisplayMetrics metrics = activity.getResources()
                .getDisplayMetrics();

        this.density = metrics.density <= 0f ? 1f : metrics.density;
        this.scaledDensity = metrics.scaledDensity <= 0f
                ? this.density
                : metrics.scaledDensity;

        Point boyut = ekranBoyutuOku(activity, metrics);

        this.ekranGenislikPx = boyut.x;
        this.ekranYukseklikPx = boyut.y;

        int genislikDp = pxToDp(ekranGenislikPx);
        int yukseklikDp = pxToDp(ekranYukseklikPx);

        this.kisaKenarDp = Math.min(genislikDp, yukseklikDp);
        this.uzunKenarDp = Math.max(genislikDp, yukseklikDp);
        this.yatayMod = genislikDp > yukseklikDp;
        this.tablet = this.kisaKenarDp >= 600;
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
     * Ana ekran dış boşluğunu piksel üretir.
     */
    public int anaPaddingPx() {

        if (tablet) {
            return dpToPx(24);
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

        if (tablet) {
            return dpToPx(76);
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

        if (tablet) {
            return dpToPx(52);
        }

        return dpToPx(48);
    }

    /**
     * Kart iç boşluğunu piksel üretir.
     */
    public int kartPaddingPx() {

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

        if (tablet) {
            return 24f;
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

        if (tablet) {
            return 14f;
        }

        return 12.5f;
    }

    /**
     * Editör ağırlığını üretir.
     */
    public float editorAgirlik() {

        if (yatayMod) {
            return 1.25f;
        }

        return 1.45f;
    }

    /**
     * Önizleme ağırlığını üretir.
     */
    public float onizlemeAgirlik() {

        if (yatayMod) {
            return 0.75f;
        }

        if (tablet) {
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

        return Math.round(px / density);
    }

    /**
     * Dp değerini piksel değerine çevirir.
     */
    public int dpToPx(
            int dp
    ) {

        return Math.round(dp * density);
    }

    /**
     * Sp değerini piksel değerine çevirir.
     */
    public int spToPx(
            float sp
    ) {

        return Math.round(sp * scaledDensity);
    }

    /**
     * Ekran ölçüsünü Android sürümüne göre güvenli okur.
     */
    private static Point ekranBoyutuOku(
            Activity activity,
            DisplayMetrics metrics
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            WindowMetrics windowMetrics = activity.getWindowManager()
                    .getCurrentWindowMetrics();

            return new Point(
                    windowMetrics.getBounds().width(),
                    windowMetrics.getBounds().height()
            );
        }

        return new Point(
                metrics.widthPixels,
                metrics.heightPixels
        );
    }
}