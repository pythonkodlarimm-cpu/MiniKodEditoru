package org.fy.kodeditoru.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Merkezi dosya yönetim modülü.
 *
 * Bu sınıf:
 * - dosya oluşturur
 * - klasör oluşturur
 * - dosya okur
 * - dosya yazar
 * - dosya siler
 * - klasör siler
 * - güvenli temel dosya işlemleri sağlar
 *
 * Kural:
 * - UI üretmez
 * - Android Context taşımaz
 * - thread başlatmaz
 * - editör işlemi yapmaz
 * - syntax highlight işlemi yapmaz
 * - XML önizleme üretmez
 */
public final class DosyaYoneticisi {

    /**
     * Constructor engeli.
     */
    private DosyaYoneticisi() {
        throw new IllegalStateException(
                "Utility class olusturulamaz."
        );
    }

    /**
     * Yeni klasör oluşturur.
     */
    public static boolean klasorOlustur(
            String klasorYolu
    ) {

        try {

            File klasor = new File(klasorYolu);

            if (klasor.exists()) {
                return true;
            }

            return klasor.mkdirs();

        } catch (Exception hata) {
            return false;
        }
    }

    /**
     * Yeni dosya oluşturur.
     */
    public static boolean dosyaOlustur(
            String dosyaYolu
    ) {

        try {

            File dosya = new File(dosyaYolu);

            File parent = dosya.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            if (dosya.exists()) {
                return true;
            }

            return dosya.createNewFile();

        } catch (IOException hata) {
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

        FileWriter writer = null;

        try {

            File dosya = new File(dosyaYolu);

            File parent = dosya.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            writer = new FileWriter(
                    dosya,
                    false
            );

            writer.write(
                    icerik == null ? "" : icerik
            );

            writer.flush();

            return true;

        } catch (IOException hata) {
            return false;

        } finally {

            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Dosya içeriğini string olarak döndürür.
     */
    public static String dosyaOku(
            String dosyaYolu
    ) {

        File dosya = new File(dosyaYolu);

        if (!dosya.exists()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        BufferedReader reader = null;

        try {

            reader = new BufferedReader(
                    new FileReader(dosya)
            );

            String satir;

            while ((satir = reader.readLine()) != null) {

                builder.append(satir);
                builder.append("\n");
            }

        } catch (IOException hata) {
            return "";

        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }

        return builder.toString();
    }

    /**
     * Dosyayı siler.
     */
    public static boolean dosyaSil(
            String dosyaYolu
    ) {

        try {

            File dosya = new File(dosyaYolu);

            if (!dosya.exists()) {
                return false;
            }

            return dosya.delete();

        } catch (Exception hata) {
            return false;
        }
    }

    /**
     * Klasörü recursive siler.
     */
    public static boolean klasorSil(
            String klasorYolu
    ) {

        try {

            File klasor = new File(klasorYolu);

            return recursiveSil(klasor);

        } catch (Exception hata) {
            return false;
        }
    }

    /**
     * Dosya var mı kontrolü.
     */
    public static boolean dosyaVarMi(
            String dosyaYolu
    ) {

        return new File(dosyaYolu).exists();
    }

    /**
     * Recursive güvenli silme işlemi.
     */
    private static boolean recursiveSil(
            File file
    ) {

        if (file == null || !file.exists()) {
            return false;
        }

        if (file.isDirectory()) {

            File[] children = file.listFiles();

            if (children != null) {

                for (File child : children) {
                    recursiveSil(child);
                }
            }
        }

        return file.delete();
    }
    }
