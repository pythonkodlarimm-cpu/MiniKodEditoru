package org.fy.kodeditoru.editor;

/**
 * Editör mod yönetim modülü.
 *
 * Bu sınıf:
 * - editör ekranının aktif görünüm modunu yönetir.
 * - editör / bölünmüş / önizleme modlarını tek merkezde tutar.
 * - UI katmanına aktif görünüm bilgisini sağlar.
 * - editör davranış kararlarını sadeleştirir.
 *
 * Modlar:
 * - EDITOR_MODU:
 *   yalnızca editör görünür.
 *
 * - BOLUNMUS_MOD:
 *   editör + preview birlikte görünür.
 *
 * - ONIZLEME_MODU:
 *   yalnızca preview görünür.
 *
 * Kural:
 * - layout ölçüsü değiştirmez.
 * - keyboard işlemi yapmaz.
 * - scroll yönetmez.
 * - syntax highlight yapmaz.
 * - XML render yapmaz.
 * - view visibility değiştirmez.
 * - sadece aktif mod bilgisini yönetir.
 */
public final class EditorModYoneticisi {

    public static final int EDITOR_MODU = 1;

    public static final int BOLUNMUS_MOD = 2;

    public static final int ONIZLEME_MODU = 3;

    private int aktifMod = BOLUNMUS_MOD;

    /**
     * Editör mod yöneticisi oluşturur.
     */
    public EditorModYoneticisi() {

    }

    /**
     * Editör modunu aktif yapar.
     */
    public void editorModuAktifEt() {

        aktifMod = EDITOR_MODU;
    }

    /**
     * Bölünmüş modu aktif yapar.
     */
    public void bolunmusModuAktifEt() {

        aktifMod = BOLUNMUS_MOD;
    }

    /**
     * Önizleme modunu aktif yapar.
     */
    public void onizlemeModuAktifEt() {

        aktifMod = ONIZLEME_MODU;
    }

    /**
     * Aktif mod değerini döndürür.
     */
    public int aktifModGetir() {

        return aktifMod;
    }

    /**
     * Editör modu aktif mi kontrol eder.
     */
    public boolean editorModuMu() {

        return aktifMod == EDITOR_MODU;
    }

    /**
     * Bölünmüş mod aktif mi kontrol eder.
     */
    public boolean bolunmusModMu() {

        return aktifMod == BOLUNMUS_MOD;
    }

    /**
     * Önizleme modu aktif mi kontrol eder.
     */
    public boolean onizlemeModuMu() {

        return aktifMod == ONIZLEME_MODU;
    }
}
