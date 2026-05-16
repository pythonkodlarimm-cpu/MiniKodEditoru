package org.fy.kodeditoru.runtime;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.fy.kodeditoru.preview.XmlOnizlemeYoneticisi;

import java.util.Map;

/**
 * Runtime ekran Activity modülü.
 *
 * Bu sınıf:
 * - editörden gelen XML içeriğini tam ekran gösterir.
 * - XML içeriğini gerçek Android View ağacı olarak render eder.
 * - Java davranış parser sonucunu runtime davranış yöneticisine bağlar.
 * - buton tıklama, Toast ve TextView setText davranışlarını test ettirir.
 * - APK üretmeden görsel uygulama testi sağlar.
 *
 * Kural:
 * - APK derlemez.
 * - gerçek Java JVM çalıştırmaz.
 * - dosya okuma/yazma yapmaz.
 * - proje yönetmez.
 * - sadece runtime görsel test ekranıdır.
 */
public final class RuntimeEkranActivity extends Activity {

    private FrameLayout kokAlan;
    private XmlOnizlemeYoneticisi xmlOnizlemeYoneticisi;
    private RuntimeDavranisYoneticisi runtimeDavranisYoneticisi;
    private JavaDavranisParser javaDavranisParser;

    /**
     * Activity oluşturulduğunda runtime ekranını başlatır.
     */
    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        runtimeBilesenleriniHazirla();
        runtimeEkraniniKur();
        runtimeIceriginiYukle();
    }

    /**
     * Runtime bileşenlerini hazırlar.
     */
    private void runtimeBilesenleriniHazirla() {

        kokAlan =
                new FrameLayout(this);

        kokAlan.setBackgroundColor(
                Color.parseColor("#020617")
        );

        xmlOnizlemeYoneticisi =
                new XmlOnizlemeYoneticisi(
                        this,
                        kokAlan
                );

        runtimeDavranisYoneticisi =
                new RuntimeDavranisYoneticisi(
                        this
                );

        javaDavranisParser =
                new JavaDavranisParser();
    }

    /**
     * Runtime ekranını kurar.
     */
    private void runtimeEkraniniKur() {

        setContentView(
                kokAlan,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );
    }

    /**
     * Runtime içeriğini yükler.
     */
    private void runtimeIceriginiYukle() {

        if (!RuntimeVeriDeposu.xmlVarMi()) {
            hataGoster("Çalıştırılacak XML içeriği yok.");
            return;
        }

        xmlOnizlemeYoneticisi.onizlemeGuncelle(
                RuntimeVeriDeposu.xmlIcerikGetir()
        );

        davranislariBagla();
    }

    /**
     * Java davranışlarını runtime ekrana bağlar.
     */
    private void davranislariBagla() {

        if (!RuntimeVeriDeposu.javaVarMi()) {
            return;
        }

        Map<String, RuntimeDavranisModeli> davranislar =
                javaDavranisParser.davranislariCikar(
                        RuntimeVeriDeposu.javaIcerikGetir()
                );

        runtimeDavranisYoneticisi.davranislariUygula(
                davranislar
        );
    }

    /**
     * Runtime hata mesajı gösterir.
     */
    private void hataGoster(
            String mesaj
    ) {

        kokAlan.removeAllViews();

        TextView hataMetni =
                new TextView(this);

        hataMetni.setText(
                mesaj == null
                        ? "Runtime hatası."
                        : mesaj
        );

        hataMetni.setTextColor(Color.WHITE);
        hataMetni.setTextSize(16f);
        hataMetni.setGravity(Gravity.CENTER);
        hataMetni.setPadding(32, 32, 32, 32);

        kokAlan.addView(
                hataMetni,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );
    }

    /**
     * Geri tuşunda runtime verisini temizler.
     */
    @Override
    public void finish() {

        RuntimeVeriDeposu.temizle();

        super.finish();
    }

    /**
     * Root runtime alanını döndürür.
     */
    public View getKokAlan() {
        return kokAlan;
    }
  }
