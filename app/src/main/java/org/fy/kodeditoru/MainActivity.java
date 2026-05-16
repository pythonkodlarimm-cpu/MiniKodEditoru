package org.fy.kodeditoru;

import android.app.Activity;
import android.os.Bundle;

import org.fy.kodeditoru.ui.AnaEkranKurucu;
import org.fy.kodeditoru.ui.EdgeToEdgeYoneticisi;

/**
 * Mini Kod Editörü ana Activity modülü.
 *
 * Bu sınıf:
 * - uygulamanın ana ekranını açar.
 * - activity_main.xml arayüzünü yükler.
 * - EdgeToEdgeYoneticisi ile pencere davranışını başlatır.
 * - AnaEkranKurucu sınıfını başlatır.
 * - ekran, editör ve önizleme sisteminin giriş noktasını oluşturur.
 *
 * Kural:
 * - dosya okuma/yazma yapmaz.
 * - XML önizleme üretmez.
 * - APK derlemez.
 * - proje dosyası yönetmez.
 * - pencere detaylarını doğrudan yönetmez.
 * - iş mantığını alt yöneticilere devreder.
 */
public final class MainActivity extends Activity {

    private EdgeToEdgeYoneticisi edgeToEdgeYoneticisi;
    private AnaEkranKurucu anaEkranKurucu;

    /**
     * Activity oluşturulduğunda ana ekranı başlatır.
     */
    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        edgeToEdgeYoneticisi =
                new EdgeToEdgeYoneticisi(this);

        edgeToEdgeYoneticisi.uygula();

        setContentView(R.layout.activity_main);

        anaEkranKurucu =
                new AnaEkranKurucu(this);
    }

    /**
     * Edge-to-edge yöneticisini döndürür.
     */
    public EdgeToEdgeYoneticisi getEdgeToEdgeYoneticisi() {
        return edgeToEdgeYoneticisi;
    }

    /**
     * Ana ekran kurucu referansını döndürür.
     */
    public AnaEkranKurucu getAnaEkranKurucu() {
        return anaEkranKurucu;
    }
}
