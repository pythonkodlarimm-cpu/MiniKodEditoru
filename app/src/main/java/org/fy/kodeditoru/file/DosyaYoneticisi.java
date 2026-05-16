package org.fy.kodeditoru.file;

import org.fy.kodeditoru.core.ProjeSabitleri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Merkezi dosya yönetim modülü.
 *
 * Bu sınıf:
 * - dosya oluşturur.
 * - klasör oluşturur.
 * - dosya okur.
 * - dosya yazar.
 * - dosya siler.
 * - klasör siler.
 * - güvenli temel dosya işlemleri sağlar.
 *
 * Kural:
 * - UI üretmez.
 * - Android Context taşımaz.
 * - thread başlatmaz.
 * - editör işlemi yapmaz.
 * - syntax highlight işlemi yapmaz.
 * - XML önizleme üretmez.
 */
public final class DosyaYoneticisi {

    /**
     * Constructor engeli.
     */
    private DosyaYoneticisi() {

        throw new IllegalStateException(
                "Utility class oluşturulamaz."
        );
    }

    /**
     * Yeni klasör oluşturur.
     */
    public static boolean klasorOlustur(
            String klasorYolu
    ) {

        if (klasorYolu == null || klasorYolu.trim().isEmpty()) {
            return false;
        }

        try {

            File klasor =
                    new File(
                            klasorYolu
                    );

            if (klasor.exists()) {
                return klasor.isDirectory();
            }

            return klasor.mkdirs();

        } catch (SecurityException hata) {

            return false;
        }
    }

    /**
     * Yeni dosya oluşturur.
     */
    public static boolean dosyaOlustur(
            String dosyaYolu
    ) {

        if (dosyaYolu == null || dosyaYolu.trim().isEmpty()) {
            return false;
        }

        try {

            File dosya =
                    new File(
                            dosyaYolu
                    );

            File parent =
                    dosya.getParentFile();

            if (parent != null && !parent.exists()) {
                //noinspection ResultOfMethodCallIgnored
                parent.mkdirs();
            }

            if (dosya.exists()) {
                return dosya.isFile();
            }

            return dosya.createNewFile();

        } catch (IOException | SecurityException hata) {

            return false;
        }
    }

    /**
     * Dosya içeriğini tamamen yazar.
     */
    public static boolean dosyaYaz(
            String dosyaYolu,
            String icerik
    ) {

        if (dosyaYolu == null || dosyaYolu.trim().isEmpty()) {
            return false;
        }

        try {

            File dosya =
                    new File(
                            dosyaYolu
                    );

            File parent =
                    dosya.getParentFile();

            if (parent != null && !parent.exists()) {
                //noinspection ResultOfMethodCallIgnored
                parent.mkdirs();
            }

            try (
                    BufferedWriter writer =
                            Files.newBufferedWriter(
                                    dosya.toPath(),
                                    StandardCharsets.UTF_8
                            )
            ) {

                writer.write(
                        icerik == null
                                ? ""
                                : icerik
                );

                writer.flush();
            }

            return true;

        } catch (IOException | SecurityException hata) {

            return false;
        }
    }

    /**
     * Dosya içeriğini string olarak döndürür.
     */
    public static String dosyaOku(
            String dosyaYolu
    ) {

        if (dosyaYolu == null || dosyaYolu.trim().isEmpty()) {
            return "";
        }

        File dosya =
                new File(
                        dosyaYolu
                );

        if (!dosya.exists() || !dosya.isFile()) {
            return "";
        }

        if (dosya.length() > ProjeSabitleri.MAX_DOSYA_BOYUTU) {
            return "";
        }

        StringBuilder builder =
                new StringBuilder();

        try (
                BufferedReader reader =
                        Files.newBufferedReader(
                                dosya.toPath(),
                                StandardCharsets.UTF_8
                        )
        ) {

            String satir;

            while ((satir = reader.readLine()) != null) {

                builder.append(satir);
                builder.append('\n');
            }

        } catch (IOException | SecurityException hata) {

            return "";
        }

        return builder.toString();
    }

    /**
     * Dosyayı siler.
     */
    public static boolean dosyaSil(
            String dosyaYolu
    ) {

        if (dosyaYolu == null || dosyaYolu.trim().isEmpty()) {
            return false;
        }

        try {

            File dosya =
                    new File(
                            dosyaYolu
                    );

            if (!dosya.exists() || !dosya.isFile()) {
                return false;
            }

            return dosya.delete();

        } catch (SecurityException hata) {

            return false;
        }
    }

    /**
     * Klasörü recursive siler.
     */
    public static boolean klasorSil(
            String klasorYolu
    ) {

        if (klasorYolu == null || klasorYolu.trim().isEmpty()) {
            return false;
        }

        try {

            File klasor =
                    new File(
                            klasorYolu
                    );

            return recursiveSil(
                    klasor
            );

        } catch (SecurityException hata) {

            return false;
        }
    }

    /**
     * Dosya var mı kontrolü yapar.
     */
    public static boolean dosyaVarMi(
            String dosyaYolu
    ) {

        if (dosyaYolu == null || dosyaYolu.trim().isEmpty()) {
            return false;
        }

        File dosya =
                new File(
                        dosyaYolu
                );

        return dosya.exists()
                && dosya.isFile();
    }

    /**
     * Klasör var mı kontrolü yapar.
     */
    public static boolean klasorVarMi(
            String klasorYolu
    ) {

        if (klasorYolu == null || klasorYolu.trim().isEmpty()) {
            return false;
        }

        File klasor =
                new File(
                        klasorYolu
                );

        return klasor.exists()
                && klasor.isDirectory();
    }

    /**
     * Dosya boyutunu bayt olarak döndürür.
     */
    public static long dosyaBoyutu(
            String dosyaYolu
    ) {

        if (dosyaYolu == null || dosyaYolu.trim().isEmpty()) {
            return 0L;
        }

        File dosya =
                new File(
                        dosyaYolu
                );

        if (!dosya.exists() || !dosya.isFile()) {
            return 0L;
        }

        return dosya.length();
    }

    /**
     * Recursive güvenli silme işlemi yapar.
     */
    private static boolean recursiveSil(
            File file
    ) {

        if (file == null || !file.exists()) {
            return false;
        }

        if (file.isDirectory()) {

            File[] children =
                    file.listFiles();

            if (children != null) {

                for (File child : children) {
                    recursiveSil(child);
                }
            }
        }

        return file.delete();
    }
            }
