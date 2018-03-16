package com.wandrip.fonts;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FontsOverride {

    private static final String DEFAULT_FONT_FILENAME = "fonts/Playball-Regular.ttf";
    private static final String DEFAULT_BOLD_FONT_FILENAME = "fonts/Playball-Regular.ttf";
    private static final String DEFAULT_ITALIC_FONT_FILENAME = "fonts/Playball-Regular.ttf";

    public static void setDefaultFont(Context context) {

        try {

            FontsOverride.setDefaultFont(context, "DEFAULT", DEFAULT_FONT_FILENAME);
            FontsOverride.setDefaultFont(context, "DEFAULT_BOLD", DEFAULT_BOLD_FONT_FILENAME);

            FontsOverride.setDefaultFont(context, "SERIF", DEFAULT_FONT_FILENAME);
            FontsOverride.setDefaultFont(context, "MONOSPACE", DEFAULT_FONT_FILENAME);
            FontsOverride.setDefaultFont(context, "SANS_SERIF", DEFAULT_FONT_FILENAME);

            final Typeface bold = Typeface.createFromAsset(context.getAssets(), DEFAULT_BOLD_FONT_FILENAME);
            final Typeface italic = Typeface.createFromAsset(context.getAssets(), DEFAULT_ITALIC_FONT_FILENAME);
            final Typeface boldItalic = Typeface.createFromAsset(context.getAssets(), DEFAULT_ITALIC_FONT_FILENAME);
            final Typeface regularTypeface = Typeface.createFromAsset(context.getAssets(), DEFAULT_FONT_FILENAME);

            Field sDefaults = Typeface.class.getDeclaredField("sDefaults");
            sDefaults.setAccessible(true);
            sDefaults.set(null, new Typeface[]{italic, bold, regularTypeface, boldItalic});

            ViewGroup view = (ViewGroup) ((Activity) context).getWindow().getDecorView()
                    .findViewById(android.R.id.content);

            setAppFont(view, regularTypeface, true);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(context, "Message" + e, Toast.LENGTH_LONG).show();
        }
    }

    protected static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);

        replaceFont(staticTypefaceFieldName, regular);

    }

    protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static final void setAppFont(Context context) {
        final Typeface regularTypeface = Typeface.createFromAsset(context.getAssets(), DEFAULT_FONT_FILENAME);

        ViewGroup view = (ViewGroup) ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);

        setAppFont(view, regularTypeface, true);
    }

    public static final void setAppFont(Context context, ViewGroup view) {
        final Typeface regularTypeface = Typeface.createFromAsset(context.getAssets(), DEFAULT_FONT_FILENAME);
        setAppFont(view, regularTypeface, true);
    }

    protected static final void setAppFont(ViewGroup mContainer, Typeface mFont, boolean reflect) {
        if (mContainer == null || mFont == null)
            return;

        final int mCount = mContainer.getChildCount();

        // Loop through all of the children.
        for (int i = 0; i < mCount; ++i) {
            final View mChild = mContainer.getChildAt(i);
            if (mChild instanceof TextView) {
                // Set the font if it is a TextView.
                ((TextView) mChild).setTypeface(mFont);
            } else if (mChild instanceof ViewGroup) {
                // Recursively attempt another ViewGroup.
                setAppFont((ViewGroup) mChild, mFont, reflect);
            } else if (reflect) {
                try {

                    Method mSetTypeface = mChild.getClass().getMethod("setTypeface", Typeface.class);
                    mSetTypeface.invoke(mChild, mFont);
                } catch (Exception e) {

                    /***** Do something... ******/
                }
            }
        }
    }

}
