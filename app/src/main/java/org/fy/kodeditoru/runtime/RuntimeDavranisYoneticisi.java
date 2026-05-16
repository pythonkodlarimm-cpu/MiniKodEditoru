package org.fy.kodeditoru.runtime;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.fy.kodeditoru.preview.XmlOnizlemeYoneticisi;

import java.util.Map;

/**
 * Runtime davranış yönetim modülü.
 *
 * Bu sınıf:
 * - runtime ekranındaki View nesnelerini yönetir.
 * - XML önizleme motorunun tag olarak sakladığı android:id değerlerini bulur.
 * - Java parser tarafından çıkarılan davranışları uygular.
 * - buton tıklama olaylarını bağlar.
 * - Toast davranışlarını çalıştırır.
 * - TextView setText davranışlarını uygular.
 *
 * Kural:
 * - gerçek Java kodu çalıştırmaz.
 * - JVM değildir.
 * - APK derlemez.
 * - XML parse etmez.
 * - dosya işlemi yapmaz.
 */
public final class RuntimeDavranisYoneticisi {

    private final Activity activity;
    private final View kokView;

    /**
     * RuntimeDavranisYoneticisi oluşturur.
     */
    public RuntimeDavranisYoneticisi(
            Activity activity,
            View kokView
    ) {

        if (activity == null) {
            throw new IllegalArgumentException(
                    "Activity null olamaz."
            );
        }

        if (kokView == null) {
            throw new IllegalArgumentException(
                    "Kök View null olamaz."
            );
        }

        this.activity = activity;
        this.kokView = kokView;
    }

    /**
     * Runtime davranışlarını ekrana uygular.
     */
    public void davranislariUygula(
            Map<String, RuntimeDavranisModeli> davranislar
    ) {

        if (davranislar == null || davranislar.isEmpty()) {
            return;
        }

        for (RuntimeDavranisModeli model : davranislar.values()) {
            davranisiBagla(model);
        }
    }

    /**
     * Tek davranışı View üzerine bağlar.
     */
    private void davranisiBagla(
            RuntimeDavranisModeli model
    ) {

        if (model == null) {
            return;
        }

        View hedefView =
                viewIdIleBul(
                        model.getTetikleyiciViewId()
                );

        if (hedefView == null) {
            return;
        }

        hedefView.setOnClickListener(
                v -> davranisiCalistir(model)
        );
    }

    /**
     * Runtime davranışını çalıştırır.
     */
    private void davranisiCalistir(
            RuntimeDavranisModeli model
    ) {

        toastDavranisiniCalistir(model);
        setTextDavranisiniCalistir(model);
    }

    /**
     * Toast davranışını çalıştırır.
     */
    private void toastDavranisiniCalistir(
            RuntimeDavranisModeli model
    ) {

        if (!model.toastVarMi()) {
            return;
        }

        Toast.makeText(
                activity,
                model.getToastMesaji(),
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * setText davranışlarını çalıştırır.
     */
    private void setTextDavranisiniCalistir(
            RuntimeDavranisModeli model
    ) {

        if (!model.setTextDavranisiVarMi()) {
            return;
        }

        for (Map.Entry<String, String> entry
                : model.getSetTextHaritasi().entrySet()) {

            View hedefView =
                    viewIdIleBul(
                            entry.getKey()
                    );

            if (!(hedefView instanceof TextView)) {
                continue;
            }

            ((TextView) hedefView).setText(
                    entry.getValue()
            );
        }
    }

    /**
     * Runtime tag içindeki android:id değerine göre View bulur.
     */
    private View viewIdIleBul(
            String viewId
    ) {

        if (viewId == null || viewId.trim().isEmpty()) {
            return null;
        }

        return viewIdIleBulRecursive(
                kokView,
                viewId.trim()
        );
    }

    /**
     * View ağacında recursive id araması yapar.
     */
    private View viewIdIleBulRecursive(
            View view,
            String viewId
    ) {

        if (view == null) {
            return null;
        }

        Object tag =
                view.getTag();

        String beklenenTag =
                XmlOnizlemeYoneticisi.RUNTIME_ID_TAG_ON_EKI
                        + viewId;

        if (beklenenTag.equals(tag)) {
            return view;
        }

        if (!(view instanceof ViewGroup)) {
            return null;
        }

        ViewGroup group =
                (ViewGroup) view;

        for (int i = 0; i < group.getChildCount(); i++) {

            View sonuc =
                    viewIdIleBulRecursive(
                            group.getChildAt(i),
                            viewId
                    );

            if (sonuc != null) {
                return sonuc;
            }
        }

        return null;
    }
}
