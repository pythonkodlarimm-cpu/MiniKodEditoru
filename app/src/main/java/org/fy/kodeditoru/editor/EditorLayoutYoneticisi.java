package org.fy.kodeditoru.editor;

import android.view.View;
import android.widget.LinearLayout;

/**
 * Editör layout yönetim modülü.
 *
 * Bu sınıf:
 * - editör ve preview alanlarının görünüm düzenini yönetir.
 * - aktif moda göre layout ağırlıklarını uygular.
 * - editör alanını büyütür/küçültür.
 * - preview alanını büyütür/küçültür.
 * - split görünüm davranışını yönetir.
 *
 * Modlar:
 * - editor fullscreen davranışı
 * - split görünüm davranışı
 * - preview fullscreen davranışı
 *
 * Kural:
 * - keyboard işlemi yapmaz.
 * - syntax highlight yapmaz.
 * - XML render yapmaz.
 * - scroll yönetmez.
 * - editor içerik işlemi yapmaz.
 * - sadece layout görünüm yönetimi yapar.
 */
public final class EditorLayoutYoneticisi {

    private final View editorKart;

    private final View onizlemeKart;

    /**
     * Layout yöneticisi oluşturur.
     */
    public EditorLayoutYoneticisi(
            View editorKart,
            View onizlemeKart
    ) {

        if (editorKart == null) {
            throw new IllegalArgumentException(
                    "Editor kartı null olamaz."
            );
        }

        if (onizlemeKart == null) {
            throw new IllegalArgumentException(
                    "Önizleme kartı null olamaz."
            );
        }

        this.editorKart = editorKart;
        this.onizlemeKart = onizlemeKart;
    }

    /**
     * Aktif moda göre layout görünümünü uygular.
     */
    public void moduUygula(
            int mod
    ) {

        if (mod == EditorModYoneticisi.EDITOR_MODU) {
            editorModunuUygula();
            return;
        }

        if (mod == EditorModYoneticisi.ONIZLEME_MODU) {
            onizlemeModunuUygula();
            return;
        }

        bolunmusModuUygula();
    }

    /**
     * Editör fullscreen görünümünü uygular.
     */
    public void editorModunuUygula() {

        editorKart.setVisibility(View.VISIBLE);

        onizlemeKart.setVisibility(View.GONE);

        agirlikUygula(
                editorKart,
                1f
        );
    }

    /**
     * Bölünmüş görünümü uygular.
     */
    public void bolunmusModuUygula() {

        editorKart.setVisibility(View.VISIBLE);

        onizlemeKart.setVisibility(View.VISIBLE);

        agirlikUygula(
                editorKart,
                1.35f
        );

        agirlikUygula(
                onizlemeKart,
                0.85f
        );
    }

    /**
     * Önizleme fullscreen görünümünü uygular.
     */
    public void onizlemeModunuUygula() {

        editorKart.setVisibility(View.GONE);

        onizlemeKart.setVisibility(View.VISIBLE);

        agirlikUygula(
                onizlemeKart,
                1f
        );
    }

    /**
     * View ağırlığını uygular.
     */
    private void agirlikUygula(
            View view,
            float agirlik
    ) {

        if (!(view.getLayoutParams()
                instanceof LinearLayout.LayoutParams)) {

            return;
        }

        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams)
                        view.getLayoutParams();

        params.width =
                LinearLayout.LayoutParams.MATCH_PARENT;

        params.height = 0;

        params.weight = agirlik;

        view.setLayoutParams(params);
    }
  }
