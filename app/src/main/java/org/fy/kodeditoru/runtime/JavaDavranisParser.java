package org.fy.kodeditoru.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java davranış parser modülü.
 *
 * Bu sınıf:
 * - basit Java kodu içinden buton tıklama davranışlarını okur.
 * - findViewById eşleşmelerini yakalar.
 * - setOnClickListener bloklarını yakalar.
 * - Toast mesajlarını çıkarır.
 * - setText işlemlerini çıkarır.
 * - runtime yöneticisinin kullanacağı sade davranış haritası üretir.
 *
 * Kural:
 * - gerçek Java kodu çalıştırmaz.
 * - JVM yorumlayıcısı değildir.
 * - APK derlemez.
 * - UI üretmez.
 * - dosya okuma/yazma yapmaz.
 */
public final class JavaDavranisParser {

    private static final Pattern VIEW_ID_PATTERN =
            Pattern.compile(
                    "(\\w+)\\s*=\\s*findViewById\\s*\\(\\s*R\\.id\\.(\\w+)\\s*\\)"
            );

    private static final Pattern CLICK_PATTERN =
            Pattern.compile(
                    "(\\w+)\\.setOnClickListener\\s*\\([^\\{]*\\{([\\s\\S]*?)\\}\\s*\\)",
                    Pattern.MULTILINE
            );

    private static final Pattern TOAST_PATTERN =
            Pattern.compile(
                    "Toast\\.makeText\\s*\\([^,]+,\\s*\"([^\"]*)\"",
                    Pattern.MULTILINE
            );

    private static final Pattern SET_TEXT_PATTERN =
            Pattern.compile(
                    "(\\w+)\\.setText\\s*\\(\\s*\"([^\"]*)\"\\s*\\)",
                    Pattern.MULTILINE
            );

    /**
     * JavaDavranisParser oluşturur.
     */
    public JavaDavranisParser() {
    }

    /**
     * Java kodundan davranış haritası üretir.
     */
    public Map<String, RuntimeDavranisModeli> davranislariCikar(
            String javaKodu
    ) {

        Map<String, RuntimeDavranisModeli> davranislar =
                new HashMap<>();

        if (javaKodu == null || javaKodu.trim().isEmpty()) {
            return davranislar;
        }

        Map<String, String> degiskenIdHaritasi =
                viewIdHaritasiCikar(javaKodu);

        Matcher clickMatcher =
                CLICK_PATTERN.matcher(javaKodu);

        while (clickMatcher.find()) {

            String degiskenAdi =
                    clickMatcher.group(1);

            String govde =
                    clickMatcher.group(2);

            String viewId =
                    degiskenIdHaritasi.get(degiskenAdi);

            if (viewId == null || viewId.trim().isEmpty()) {
                continue;
            }

            RuntimeDavranisModeli model =
                    govdedenDavranisCikar(
                            viewId,
                            govde,
                            degiskenIdHaritasi
                    );

            davranislar.put(
                    viewId,
                    model
            );
        }

        return davranislar;
    }

    /**
     * findViewById değişken-id haritasını çıkarır.
     */
    private Map<String, String> viewIdHaritasiCikar(
            String javaKodu
    ) {

        Map<String, String> sonuc =
                new HashMap<>();

        Matcher matcher =
                VIEW_ID_PATTERN.matcher(javaKodu);

        while (matcher.find()) {

            sonuc.put(
                    matcher.group(1),
                    matcher.group(2)
            );
        }

        return sonuc;
    }

    /**
     * Tıklama gövdesinden davranış modeli çıkarır.
     */
    private RuntimeDavranisModeli govdedenDavranisCikar(
            String tetikleyiciId,
            String govde,
            Map<String, String> degiskenIdHaritasi
    ) {

        RuntimeDavranisModeli model =
                new RuntimeDavranisModeli(
                        tetikleyiciId
                );

        Matcher toastMatcher =
                TOAST_PATTERN.matcher(govde);

        if (toastMatcher.find()) {
            model.toastMesajiAyarla(
                    toastMatcher.group(1)
            );
        }

        Matcher setTextMatcher =
                SET_TEXT_PATTERN.matcher(govde);

        while (setTextMatcher.find()) {

            String hedefDegisken =
                    setTextMatcher.group(1);

            String hedefId =
                    degiskenIdHaritasi.get(hedefDegisken);

            if (hedefId == null || hedefId.trim().isEmpty()) {
                continue;
            }

            model.setTextEkle(
                    hedefId,
                    setTextMatcher.group(2)
            );
        }

        return model;
    }
    }
