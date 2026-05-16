package org.fy.kodeditoru.preview;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * XML önizleme yönetim modülü.
 *
 * Bu sınıf:
 * - basit Android XML layout metnini okur.
 * - desteklenen widgetları gerçek Android View nesnesine çevirir.
 * - iç içe LinearLayout, FrameLayout ve ScrollView yapılarını destekler.
 * - TextView, Button, EditText ve ImageView destekler.
 * - android:id değerlerini runtime davranış eşleşmesi için tag olarak saklar.
 * - android:text, android:hint, android:orientation ve temel renkleri okur.
 * - hatalı XML durumunda hata görünümü üretir.
 *
 * Kural:
 * - APK derlemez.
 * - dosya okuma/yazma yapmaz.
 * - Java kodu çalıştırmaz.
 * - Activity yönetmez.
 * - sadece basit XML önizleme üretir.
 */
public final class XmlOnizlemeYoneticisi {

    public static final String RUNTIME_ID_TAG_ON_EKI =
            "runtime_id:";

    private final Context context;
    private final FrameLayout onizlemeAlani;

    /**
     * XML önizleme yöneticisi oluşturur.
     */
    public XmlOnizlemeYoneticisi(
            Context context,
            FrameLayout onizlemeAlani
    ) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context null olamaz."
            );
        }

        if (onizlemeAlani == null) {
            throw new IllegalArgumentException(
                    "Önizleme alanı null olamaz."
            );
        }

        this.context = context;
        this.onizlemeAlani = onizlemeAlani;
    }

    /**
     * XML içeriğini önizleme alanına uygular.
     */
    public void onizlemeGuncelle(
            String xmlIcerik
    ) {

        onizlemeAlani.removeAllViews();

        if (xmlIcerik == null || xmlIcerik.trim().isEmpty()) {
            hataGoster("XML içerik boş.");
            return;
        }

        try {

            View kokView =
                    xmlViewOlustur(
                            xmlIcerik
                    );

            onizlemeAlani.addView(
                    kokView,
                    new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                    )
            );

        } catch (Exception hata) {

            hataGoster(
                    "XML önizleme hatası: "
                            + hata.getMessage()
            );
        }
    }

    /**
     * Önizleme alanını temizler.
     */
    public void onizlemeTemizle() {

        onizlemeAlani.removeAllViews();
    }

    /**
     * Önizleme ana alanını döndürür.
     */
    public FrameLayout getOnizlemeAlani() {

        return onizlemeAlani;
    }

    /**
     * XML metninden kök View üretir.
     */
    private View xmlViewOlustur(
            String xmlIcerik
    ) throws Exception {

        XmlPullParserFactory factory =
                XmlPullParserFactory.newInstance();

        factory.setNamespaceAware(true);

        XmlPullParser parser =
                factory.newPullParser();

        parser.setInput(
                new StringReader(
                        xmlIcerik
                )
        );

        View kokView = null;

        Deque<ViewGroup> parentStack =
                new ArrayDeque<>();

        Deque<Boolean> viewGroupStack =
                new ArrayDeque<>();

        int eventType =
                parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {

                View yeniView =
                        tagIcinViewOlustur(
                                parser,
                                parser.getName()
                        );

                if (yeniView == null) {
                    viewGroupStack.push(false);
                } else {

                    viewIdTagUygula(
                            parser,
                            yeniView
                    );

                    layoutParametreleriniUygula(
                            parser,
                            yeniView
                    );

                    if (kokView == null) {
                        kokView = yeniView;
                    } else if (!parentStack.isEmpty()) {
                        cocukViewEkle(
                                parentStack.peek(),
                                yeniView
                        );
                    }

                    boolean viewGroupMu =
                            yeniView instanceof ViewGroup;

                    viewGroupStack.push(viewGroupMu);

                    if (viewGroupMu) {
                        parentStack.push(
                                (ViewGroup) yeniView
                        );
                    }
                }

            } else if (eventType == XmlPullParser.END_TAG) {

                if (!viewGroupStack.isEmpty()
                        && viewGroupStack.pop()
                        && !parentStack.isEmpty()) {

                    parentStack.pop();
                }
            }

            eventType =
                    parser.next();
        }

        if (kokView == null) {
            throw new IllegalArgumentException(
                    "Desteklenen kök View bulunamadı."
            );
        }

        return kokView;
    }

    /**
     * XML tag adına göre View üretir.
     */
    private View tagIcinViewOlustur(
            XmlPullParser parser,
            String tag
    ) {

        if ("LinearLayout".equals(tag)) {
            return linearLayoutOlustur(parser);
        }

        if ("FrameLayout".equals(tag)) {
            return frameLayoutOlustur(parser);
        }

        if ("ScrollView".equals(tag)) {
            return scrollViewOlustur();
        }

        if ("TextView".equals(tag)) {
            return textViewOlustur(parser);
        }

        if ("Button".equals(tag)) {
            return buttonOlustur(parser);
        }

        if ("EditText".equals(tag)) {
            return editTextOlustur(parser);
        }

        if ("ImageView".equals(tag)) {
            return imageViewOlustur();
        }

        return null;
    }

    /**
     * LinearLayout üretir.
     */
    private LinearLayout linearLayoutOlustur(
            XmlPullParser parser
    ) {

        LinearLayout layout =
                new LinearLayout(context);

        layout.setOrientation(
                orientationGetir(parser)
        );

        layout.setGravity(
                gravityGetir(parser)
        );

        int padding =
                dpToPx(12);

        layout.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        layout.setBackgroundColor(
                renkGetir(
                        parser,
                        "background",
                        Color.WHITE
                )
        );

        return layout;
    }

    /**
     * FrameLayout üretir.
     */
    private FrameLayout frameLayoutOlustur(
            XmlPullParser parser
    ) {

        FrameLayout frameLayout =
                new FrameLayout(context);

        frameLayout.setBackgroundColor(
                renkGetir(
                        parser,
                        "background",
                        Color.WHITE
                )
        );

        return frameLayout;
    }

    /**
     * ScrollView üretir.
     */
    private ScrollView scrollViewOlustur() {

        ScrollView scrollView =
                new ScrollView(context);

        scrollView.setFillViewport(true);

        return scrollView;
    }

    /**
     * TextView üretir.
     */
    private TextView textViewOlustur(
            XmlPullParser parser
    ) {

        TextView textView =
                new TextView(context);

        textView.setText(
                textGetir(
                        parser,
                        "TextView"
                )
        );

        textView.setTextColor(
                renkGetir(
                        parser,
                        "textColor",
                        Color.BLACK
                )
        );

        textView.setTextSize(
                yaziBoyutuGetir(
                        parser,
                        16f
                )
        );

        int padding =
                dpToPx(8);

        textView.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        return textView;
    }

    /**
     * Button üretir.
     */
    private Button buttonOlustur(
            XmlPullParser parser
    ) {

        Button button =
                new Button(context);

        button.setText(
                textGetir(
                        parser,
                        "Button"
                )
        );

        return button;
    }

    /**
     * EditText üretir.
     */
    private EditText editTextOlustur(
            XmlPullParser parser
    ) {

        EditText editText =
                new EditText(context);

        editText.setHint(
                textGetir(
                        parser,
                        "EditText"
                )
        );

        editText.setSingleLine(false);
        editText.setMinLines(2);

        return editText;
    }

    /**
     * ImageView placeholder üretir.
     */
    private ImageView imageViewOlustur() {

        ImageView imageView =
                new ImageView(context);

        imageView.setBackgroundColor(Color.LTGRAY);
        imageView.setMinimumHeight(dpToPx(120));

        return imageView;
    }

    /**
     * View id bilgisini runtime tag olarak uygular.
     */
    private void viewIdTagUygula(
            XmlPullParser parser,
            View view
    ) {

        String id =
                ozellikGetir(
                        parser,
                        "id"
                );

        String temizId =
                idTemizle(
                        id
                );

        if (!temizId.isEmpty()) {
            view.setTag(
                    RUNTIME_ID_TAG_ON_EKI
                            + temizId
            );
        }
    }

    /**
     * Parent türüne göre child View ekler.
     */
    private void cocukViewEkle(
            ViewGroup parent,
            View child
    ) {

        if (parent instanceof LinearLayout) {

            parent.addView(
                    child,
                    linearLayoutParamsUret(child)
            );

            return;
        }

        if (parent instanceof FrameLayout) {

            parent.addView(
                    child,
                    frameLayoutParamsUret(child)
            );

            return;
        }

        if (parent instanceof ScrollView) {

            if (parent.getChildCount() == 0) {
                parent.addView(
                        child,
                        new ScrollView.LayoutParams(
                                child.getLayoutParams().width,
                                child.getLayoutParams().height
                        )
                );
            }

            return;
        }

        parent.addView(child);
    }

    /**
     * LinearLayout child parametresi üretir.
     */
    private LinearLayout.LayoutParams linearLayoutParamsUret(
            View view
    ) {

        ViewGroup.LayoutParams params =
                view.getLayoutParams();

        return new LinearLayout.LayoutParams(
                params.width,
                params.height
        );
    }

    /**
     * FrameLayout child parametresi üretir.
     */
    private FrameLayout.LayoutParams frameLayoutParamsUret(
            View view
    ) {

        ViewGroup.LayoutParams params =
                view.getLayoutParams();

        return new FrameLayout.LayoutParams(
                params.width,
                params.height
        );
    }

    /**
     * View layout parametrelerini uygular.
     */
    private void layoutParametreleriniUygula(
            XmlPullParser parser,
            View view
    ) {

        int width =
                boyutGetir(
                        parser,
                        "layout_width",
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        int height =
                boyutGetir(
                        parser,
                        "layout_height",
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        view.setLayoutParams(
                new ViewGroup.LayoutParams(
                        width,
                        height
                )
        );
    }

    /**
     * XML orientation değerini okur.
     */
    private int orientationGetir(
            XmlPullParser parser
    ) {

        String orientation =
                ozellikGetir(
                        parser,
                        "orientation"
                );

        if ("horizontal".equals(orientation)) {
            return LinearLayout.HORIZONTAL;
        }

        return LinearLayout.VERTICAL;
    }

    /**
     * XML gravity değerini okur.
     */
    private int gravityGetir(
            XmlPullParser parser
    ) {

        String gravity =
                ozellikGetir(
                        parser,
                        "gravity"
                );

        if (gravity.contains("center")) {
            return Gravity.CENTER;
        }

        if (gravity.contains("end")) {
            return Gravity.END;
        }

        if (gravity.contains("right")) {
            return Gravity.RIGHT;
        }

        return Gravity.START;
    }

    /**
     * XML text/hint değerini okur.
     */
    private String textGetir(
            XmlPullParser parser,
            String varsayilan
    ) {

        String text =
                ozellikGetir(
                        parser,
                        "text"
                );

        if (text.isEmpty()) {
            text =
                    ozellikGetir(
                            parser,
                            "hint"
                    );
        }

        if (text.isEmpty()) {
            return varsayilan;
        }

        return text;
    }

    /**
     * Boyut değerini okur.
     */
    private int boyutGetir(
            XmlPullParser parser,
            String ad,
            int varsayilan
    ) {

        String deger =
                ozellikGetir(
                        parser,
                        ad
                );

        if ("match_parent".equals(deger)) {
            return ViewGroup.LayoutParams.MATCH_PARENT;
        }

        if ("wrap_content".equals(deger)) {
            return ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        if (deger.endsWith("dp")) {
            return dpToPx(
                    sayiAyikla(
                            deger
                    )
            );
        }

        return varsayilan;
    }

    /**
     * XML renk değerini okur.
     */
    private int renkGetir(
            XmlPullParser parser,
            String ad,
            int varsayilan
    ) {

        String deger =
                ozellikGetir(
                        parser,
                        ad
                );

        if (!deger.startsWith("#")) {
            return varsayilan;
        }

        try {

            return Color.parseColor(deger);

        } catch (IllegalArgumentException hata) {

            return varsayilan;
        }
    }

    /**
     * Text size değerini okur.
     */
    private float yaziBoyutuGetir(
            XmlPullParser parser,
            float varsayilan
    ) {

        String deger =
                ozellikGetir(
                        parser,
                        "textSize"
                );

        if (deger.endsWith("sp")) {
            return sayiAyikla(deger);
        }

        return varsayilan;
    }

    /**
     * XML attribute değerini güvenli okur.
     */
    private String ozellikGetir(
            XmlPullParser parser,
            String ad
    ) {

        String deger =
                parser.getAttributeValue(
                        null,
                        ad
                );

        if (deger == null) {
            deger =
                    parser.getAttributeValue(
                            "http://schemas.android.com/apk/res/android",
                            ad
                    );
        }

        if (deger == null) {
            deger =
                    parser.getAttributeValue(
                            null,
                            "android:" + ad
                    );
        }

        if (deger == null) {
            return "";
        }

        return deger.trim();
    }

    /**
     * Android id değerinden sade id adını çıkarır.
     */
    private String idTemizle(
            String idDegeri
    ) {

        if (idDegeri == null || idDegeri.trim().isEmpty()) {
            return "";
        }

        return idDegeri
                .trim()
                .replace("@+id/", "")
                .replace("@id/", "");
    }

    /**
     * Metinden sayı ayıklar.
     */
    private int sayiAyikla(
            String deger
    ) {

        try {

            return Math.round(
                    Float.parseFloat(
                            deger.replaceAll(
                                    "[^0-9.]",
                                    ""
                            )
                    )
            );

        } catch (NumberFormatException hata) {

            return 0;
        }
    }

    /**
     * Dp değerini piksele çevirir.
     */
    private int dpToPx(
            int dp
    ) {

        float density =
                context.getResources()
                        .getDisplayMetrics()
                        .density;

        if (density <= 0f) {
            density = 1f;
        }

        return Math.round(
                dp * density
        );
    }

    /**
     * Önizleme alanında hata mesajı gösterir.
     */
    private void hataGoster(
            String mesaj
    ) {

        TextView hataView =
                new TextView(context);

        hataView.setText(
                mesaj == null
                        ? "Bilinmeyen hata."
                        : mesaj
        );

        hataView.setTextColor(Color.RED);
        hataView.setTextSize(14f);
        hataView.setGravity(Gravity.CENTER);

        int padding =
                dpToPx(16);

        hataView.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        onizlemeAlani.addView(
                hataView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );
    }
                                 }
