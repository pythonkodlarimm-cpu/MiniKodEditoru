package org.fy.kodeditoru.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Yeni öğe ekranı modülü.
 *
 * Bu sınıf:
 * - yeni dosya oluşturma penceresini gösterir.
 * - kullanıcıdan dosya adı alır.
 * - kullanıcıdan dosya türü seçimi alır.
 * - sonucu çağıran sınıfa bildirir.
 *
 * Kural:
 * - dosya oluşturmaz.
 * - proje yönetmez.
 * - editör yönetmez.
 * - runtime başlatmaz.
 * - sadece yeni öğe bilgisi toplar.
 */
public final class YeniOgeEkrani {

    public static final int TUR_XML_ID = 1001;
    public static final int TUR_JAVA_ID = 1002;
    public static final int TUR_KOTLIN_ID = 1003;

    /**
     * Yeni öğe seçim sonucu.
     */
    public interface YeniOgeDinleyici {

        /**
         * Kullanıcı oluşturmayı onayladığında çağrılır.
         */
        void yeniOgeSecildi(
                String ad,
                int turId
        );
    }

    private final Activity activity;

    /**
     * Yeni öğe ekranı oluşturur.
     */
    public YeniOgeEkrani(
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
     * Yeni öğe penceresini açar.
     */
    public void ac(
            YeniOgeDinleyici dinleyici
    ) {

        LinearLayout panel =
                new LinearLayout(activity);

        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(40, 24, 40, 8);

        TextView bilgi =
                new TextView(activity);

        bilgi.setText("Dosya adını yaz ve tür seç.");
        bilgi.setTextColor(Color.DKGRAY);
        bilgi.setTextSize(14f);

        EditText adInput =
                new EditText(activity);

        adInput.setHint("Örnek: activity_login");
        adInput.setSingleLine(true);

        RadioGroup turGrubu =
                new RadioGroup(activity);

        turGrubu.setOrientation(RadioGroup.VERTICAL);
        turGrubu.addView(radioButonOlustur("XML layout dosyası", TUR_XML_ID));
        turGrubu.addView(radioButonOlustur("Java sınıfı", TUR_JAVA_ID));
        turGrubu.addView(radioButonOlustur("Kotlin sınıfı", TUR_KOTLIN_ID));
        turGrubu.check(TUR_XML_ID);

        panel.addView(bilgi);
        panel.addView(adInput);
        panel.addView(turGrubu);

        new AlertDialog.Builder(activity)
                .setTitle("Yeni öğe oluştur")
                .setView(panel)
                .setNegativeButton("Vazgeç", null)
                .setPositiveButton(
                        "Oluştur",
                        (dialog, which) -> {
                            if (dinleyici != null) {
                                dinleyici.yeniOgeSecildi(
                                        adInput.getText().toString(),
                                        turGrubu.getCheckedRadioButtonId()
                                );
                            }
                        }
                )
                .show();
    }

    /**
     * RadioButton üretir.
     */
    private RadioButton radioButonOlustur(
            String metin,
            int id
    ) {

        RadioButton radioButton =
                new RadioButton(activity);

        radioButton.setId(id);
        radioButton.setText(metin);

        return radioButton;
    }
                              }
