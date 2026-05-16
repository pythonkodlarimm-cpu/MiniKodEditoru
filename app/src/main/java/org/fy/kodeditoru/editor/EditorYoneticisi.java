package org.fy.kodeditoru.editor;

import android.text.InputType;
import android.widget.EditText;

/**
 * Merkezi editör yönetim modülü.
 *
 * Bu sınıf:
 * - EditText editör alanını yapılandırır.
 * - editöre metin ayarlar.
 * - editörden metin alır.
 * - editörü temizler.
 * - çok satırlı kod editörü davranışı kurar.
 * - temel editör standartlarını uygular.
 *
 * Kural:
 * - dosya okuma/yazma yapmaz.
 * - XML önizleme üretmez.
 * - syntax highlight işlemi yapmaz.
 * - thread başlatmaz.
 * - Activity yönetmez.
 * - proje yönetimi yapmaz.
 */
public final class EditorYoneticisi {

    private final EditText editor;

    /**
     * Merkezi editör yöneticisi oluşturur.
     */
    public EditorYoneticisi(
            EditText editor
    ) {

        if (editor == null) {
            throw new IllegalArgumentException(
                    "Editor null olamaz."
            );
        }

        this.editor = editor;

        editoruYapilandir();
    }

    /**
     * Editör temel davranışlarını kurar.
     */
    private void editoruYapilandir() {

        editor.setHorizontallyScrolling(true);
        editor.setSingleLine(false);

        editor.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        );

        editor.setTextIsSelectable(true);
        editor.setVerticalScrollBarEnabled(true);
        editor.setHorizontalScrollBarEnabled(true);

        editor.setPadding(
                24,
                24,
                24,
                24
        );

        editor.setTextSize(14f);
    }

    /**
     * Editöre içerik ayarlar.
     */
    public void icerikAyarla(
            String icerik
    ) {

        editor.setText(
                icerik == null ? "" : icerik
        );

        imlecSonaGit();
    }

    /**
     * Editöre içerik yükler.
     */
    public void icerikYukle(
            String icerik
    ) {

        icerikAyarla(icerik);
    }

    /**
     * Editördeki güncel içeriği döndürür.
     */
    public String icerikGetir() {

        if (editor.getText() == null) {
            return "";
        }

        return editor.getText().toString();
    }

    /**
     * Editörü temizler.
     */
    public void editorTemizle() {

        editor.setText("");
    }

    /**
     * Editörü aktif/pasif yapar.
     */
    public void editorAktifMi(
            boolean aktif
    ) {

        editor.setEnabled(aktif);
    }

    /**
     * İmleci satır sonuna taşır.
     */
    public void imlecSonaGit() {

        if (editor.getText() == null) {
            return;
        }

        int uzunluk =
                editor.getText().length();

        editor.setSelection(
                uzunluk
        );
    }

    /**
     * Editör referansını döndürür.
     */
    public EditText getEditor() {
        return editor;
    }
        }
