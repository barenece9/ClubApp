package com.ambuja.clubapp.utils;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by pulkitm on 8/4/2016.
 */
public class MySpannable extends ClickableSpan {

    private boolean isUnderline = true;

    /**
     * Constructor
     */
    public MySpannable(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {

        ds.setUnderlineText(isUnderline);

    }

    @Override
    public void onClick(View widget) {

    }
}
