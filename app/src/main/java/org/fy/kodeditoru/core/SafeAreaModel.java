package org.fy.kodeditoru.core;

/**
 * Android güvenli ekran alanı modelidir.
 *
 * Bu sınıf:
 * - status bar, navigation bar, gesture bar ve klavye alanlarını taşır.
 * - kullanılabilir ekran genişliği ve yüksekliğini saklar.
 * - telefon/tablet/yatay durum bilgisini standartlaştırır.
 * - UI katmanına sade güvenli alan verisi sağlar.
 *
 * Kural:
 * - UI üretmez.
 * - Android View taşımaz.
 * - Context taşımaz.
 * - dosya işlemi yapmaz.
 * - log yazmaz.
 * - hesaplama dışında yan etki oluşturmaz.
 */
public final class SafeAreaModel {

    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    private final int keyboardBottom;
    private final boolean keyboardVisible;

    private final int screenWidth;
    private final int screenHeight;

    private final int usableWidth;
    private final int usableHeight;

    private final boolean tablet;
    private final boolean landscape;

    /**
     * SafeAreaModel oluşturur.
     */
    public SafeAreaModel(
            int left,
            int top,
            int right,
            int bottom,
            int keyboardBottom,
            boolean keyboardVisible,
            int screenWidth,
            int screenHeight,
            boolean tablet,
            boolean landscape
    ) {

        this.left = Math.max(0, left);
        this.top = Math.max(0, top);
        this.right = Math.max(0, right);
        this.bottom = Math.max(0, bottom);
        this.keyboardBottom = Math.max(0, keyboardBottom);
        this.keyboardVisible = keyboardVisible;
        this.screenWidth = Math.max(0, screenWidth);
        this.screenHeight = Math.max(0, screenHeight);
        this.tablet = tablet;
        this.landscape = landscape;

        this.usableWidth =
                Math.max(
                        0,
                        this.screenWidth - this.left - this.right
                );

        this.usableHeight =
                Math.max(
                        0,
                        this.screenHeight
                                - this.top
                                - Math.max(this.bottom, this.keyboardBottom)
                );
    }

    /**
     * Sol güvenli alanı döndürür.
     */
    public int getLeft() {
        return left;
    }

    /**
     * Üst güvenli alanı döndürür.
     */
    public int getTop() {
        return top;
    }

    /**
     * Sağ güvenli alanı döndürür.
     */
    public int getRight() {
        return right;
    }

    /**
     * Alt güvenli alanı döndürür.
     */
    public int getBottom() {
        return bottom;
    }

    /**
     * Klavye alt alanını döndürür.
     */
    public int getKeyboardBottom() {
        return keyboardBottom;
    }

    /**
     * Klavyenin açık olup olmadığını döndürür.
     */
    public boolean isKeyboardVisible() {
        return keyboardVisible;
    }

    /**
     * Ekran genişliğini döndürür.
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * Ekran yüksekliğini döndürür.
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * Kullanılabilir genişliği döndürür.
     */
    public int getUsableWidth() {
        return usableWidth;
    }

    /**
     * Kullanılabilir yüksekliği döndürür.
     */
    public int getUsableHeight() {
        return usableHeight;
    }

    /**
     * Tablet sınıfı olup olmadığını döndürür.
     */
    public boolean isTablet() {
        return tablet;
    }

    /**
     * Yatay mod olup olmadığını döndürür.
     */
    public boolean isLandscape() {
        return landscape;
    }

    /**
     * UI için kullanılacak efektif alt boşluğu döndürür.
     */
    public int getEffectiveBottom() {

        return Math.max(
                bottom,
                keyboardBottom
        );
    }

    /**
     * Modeli loglanabilir kısa metne çevirir.
     */
    public String ozet() {

        return "SafeAreaModel{"
                + "left=" + left
                + ", top=" + top
                + ", right=" + right
                + ", bottom=" + bottom
                + ", keyboardBottom=" + keyboardBottom
                + ", keyboardVisible=" + keyboardVisible
                + ", screenWidth=" + screenWidth
                + ", screenHeight=" + screenHeight
                + ", usableWidth=" + usableWidth
                + ", usableHeight=" + usableHeight
                + ", tablet=" + tablet
                + ", landscape=" + landscape
                + '}';
    }
  }
