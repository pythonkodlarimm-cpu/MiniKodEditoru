package org.fy.kodeditoru.runtime;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

/**
 * Runtime davranış yönetim modülü.
 *
 * Bu sınıf:
 * - runtime ekranındaki View nesnelerini yönetir.
 * - Java parser tarafından çıkarılan davranışları uygular.
 * - buton tıklama olaylarını bağlar.
 * - Toast davranışlarını çalıştırır.
 * - TextView setText davranışlarını uygular.
 * - gerçek uygulama hissine yakın runtime test davranışı sağlar.
 *
 * Örnek:
 * - Button tıklandı
 * - Toast gösterildi
 * - TextView text değişti
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

    /**
     * RuntimeDavranisYoneticisi oluşturur.
     */
    public RuntimeDavranisYoneticisi(
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

        int viewId =
                activity.getResources().getIdentifier(
                        model.getTetikleyiciViewId(),
                        "id",
                        activity.getPackageName()
                );

        if (viewId == 0) {
            return;
        }

        View hedefView =
                activity.findViewById(viewId);

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

            String hedefId =
                    entry.getKey();

            String yeniMetin =
                    entry.getValue();

            int viewId =
                    activity.getResources().getIdentifier(
                            hedefId,
                            "id",
                            activity.getPackageName()
                    );

            if (viewId == 0) {
                continue;
            }

            View hedefView =
                    activity.findViewById(viewId);

            if (!(hedefView instanceof TextView)) {
                continue;
            }

            ((TextView) hedefView).setText(
                    yeniMetin
            );
        }
    }
}
