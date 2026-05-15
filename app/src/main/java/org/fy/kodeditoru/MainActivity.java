package org.fy.kodeditoru;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.fy.kodeditoru.ui.AnaEkranKurucu;

/**
 * Mini Kod Editörü ana Activity modülü.
 *
 * Bu sınıf:
 * - uygulamanın ana ekranını açar
 * - activity_main.xml arayüzünü yükler
 * - AnaEkranKurucu sınıfını başlatır
 * - edge-to-edge pencere davranışını uygular
 * - ekran ölçek ve editör sisteminin giriş noktasını oluşturur
 *
 * Kural:
 * - dosya okuma/yazma yapmaz
 * - XML önizleme üretmez
 * - APK derlemez
 * - proje dosyası yönetmez
 * - iş mantığını alt yöneticilere devreder
 */
public final class MainActivity extends Activity {

    private AnaEkranKurucu anaEkranKurucu;

    /**
     * Activity oluşturulduğunda ana ekranı başlatır.
     */
    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        pencereyiYapilandir();

        setContentView(R.layout.activity_main);

        anaEkranKurucu =
                new AnaEkranKurucu(this);
    }

    /**
     * Android pencere davranışını yapılandırır.
     */
    private void pencereyiYapilandir() {

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

            window.setStatusBarColor(
                    Color.TRANSPARENT
            );

            window.setNavigationBarColor(
                    Color.BLACK
            );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            window.setDecorFitsSystemWindows(false);

        } else {

            int flags =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

            window.getDecorView()
                    .setSystemUiVisibility(flags);
        }
    }

    /**
     * Ana ekran kurucu referansını döndürür.
     */
    public AnaEkranKurucu getAnaEkranKurucu() {
        return anaEkranKurucu;
    }
}