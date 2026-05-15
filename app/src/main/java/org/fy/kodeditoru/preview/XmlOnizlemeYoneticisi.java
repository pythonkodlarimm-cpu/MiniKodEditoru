package org.fy.kodeditoru.preview;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
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

/**
 * XML önizleme yönetim modülü.
 *
 * Bu sınıf:
 * - basit Android XML layout metnini okur
 * - desteklenen widgetları gerçek Android View nesnesine çevirir
 * - LinearLayout, ScrollView, TextView, Button, EditText ve ImageView destekler
 * - hatalı XML durumunda hata görünümü üretir
 *
 * Kural:
 * - APK derlemez
 * - dosya okuma/yazma yapmaz
 * - Java kodu çalıştırmaz
 * - Activity yönetmez
 * - sadece basit XML önizleme üretir
 */
public final class XmlOnizlemeYoneticisi {

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
            throw new IllegalArgumentException("Context null olamaz.");
        }

        if (onizlemeAlani == null) {
            throw new IllegalArgumentException("Önizleme alanı null olamaz.");
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

            View kokView = xmlViewOlustur(xmlIcerik);

            onizlemeAlani.addView(
                    kokView,
                    new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                    )
            );

        } catch (Exception hata) {
            hataGoster("XML önizleme hatası: " + hata.getMessage());
        }
    }

    /**
     * Önizleme alanını temizler.
     */
    public void onizlemeTemizle() {

        onizlemeAlani.removeAllViews();
    }

    /**
     * XML metninden kök View üretir.
     */
    private View xmlViewOlustur(
            String xmlIcerik
    ) throws Exception {

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);

        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(xmlIcerik));

        View kokView = null;
        LinearLayout aktifLinearLayout = null;
        ScrollView aktifScrollView = null;

        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {

                String tag = parser.getName();
                View yeniView = tagIcinViewOlustur(parser, tag);

                if (yeniView != null) {

                    if (kokView == null) {
                        kokView = yeniView;
                    } else if (aktifLinearLayout != null) {
                        aktifLinearLayout.addView(yeniView);
                    } else if (aktifScrollView != null) {
                        aktifScrollView.addView(yeniView);
                    }

                    if (yeniView instanceof LinearLayout) {
                        aktifLinearLayout = (LinearLayout) yeniView;
                    }

                    if (yeniView instanceof ScrollView) {
                        aktifScrollView = (ScrollView) yeniView;
                    }
                }

            } else if (eventType == XmlPullParser.END_TAG) {

                String tag = parser.getName();

                if ("LinearLayout".equals(tag)) {
                    aktifLinearLayout = null;
                }

                if ("ScrollView".equals(tag)) {
                    aktifScrollView = null;
                }
            }

            eventType = parser.next();
        }

        if (kokView == null) {
            throw new IllegalArgumentException("Desteklenen kök View bulunamadı.");
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

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(orientationGetir(parser));
        layout.setPadding(24, 24, 24, 24);
        layout.setBackgroundColor(Color.WHITE);

        return layout;
    }

    /**
     * ScrollView üretir.
     */
    private ScrollView scrollViewOlustur() {

        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);

        return scrollView;
    }

    /**
     * TextView üretir.
     */
    private TextView textViewOlustur(
            XmlPullParser parser
    ) {

        TextView textView = new TextView(context);
        textView.setText(textGetir(parser, "TextView"));
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16f);
        textView.setPadding(12, 12, 12, 12);

        return textView;
    }

    /**
     * Button üretir.
     */
    private Button buttonOlustur(
            XmlPullParser parser
    ) {

        Button button = new Button(context);
        button.setText(textGetir(parser, "Button"));

        return button;
    }

    /**
     * EditText üretir.
     */
    private EditText editTextOlustur(
            XmlPullParser parser
    ) {

        EditText editText = new EditText(context);
        editText.setHint(textGetir(parser, "EditText"));
        editText.setSingleLine(false);
        editText.setMinLines(2);

        return editText;
    }

    /**
     * ImageView placeholder üretir.
     */
    private ImageView imageViewOlustur() {

        ImageView imageView = new ImageView(context);
        imageView.setBackgroundColor(Color.LTGRAY);
        imageView.setMinimumHeight(160);

        return imageView;
    }

    /**
     * XML orientation değerini okur.
     */
    private int orientationGetir(
            XmlPullParser parser
    ) {

        String orientation = ozellikGetir(
                parser,
                "orientation"
        );

        if ("horizontal".equals(orientation)) {
            return LinearLayout.HORIZONTAL;
        }

        return LinearLayout.VERTICAL;
    }

    /**
     * XML text/hint değerini okur.
     */
    private String textGetir(
            XmlPullParser parser,
            String varsayilan
    ) {

        String text = ozellikGetir(parser, "text");

        if (text.isEmpty()) {
            text = ozellikGetir(parser, "hint");
        }

        if (text.startsWith("@string/")) {
            return text;
        }

        if (text.isEmpty()) {
            return varsayilan;
        }

        return text;
    }

    /**
     * XML attribute değerini güvenli okur.
     */
    private String ozellikGetir(
            XmlPullParser parser,
            String ad
    ) {

        String deger = parser.getAttributeValue(null, ad);

        if (deger == null) {
            deger = parser.getAttributeValue(
                    "http://schemas.android.com/apk/res/android",
                    ad
            );
        }

        if (deger == null) {
            return "";
        }

        return deger.trim();
    }

    /**
     * Önizleme alanında hata mesajı gösterir.
     */
    private void hataGoster(
            String mesaj
    ) {

        TextView hataView = new TextView(context);
        hataView.setText(mesaj == null ? "Bilinmeyen hata." : mesaj);
        hataView.setTextColor(Color.RED);
        hataView.setTextSize(14f);
        hataView.setGravity(Gravity.CENTER);
        hataView.setPadding(24, 24, 24, 24);

        onizlemeAlani.addView(
                hataView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );
    }
    }
