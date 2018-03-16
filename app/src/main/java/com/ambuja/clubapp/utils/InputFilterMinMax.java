package com.ambuja.clubapp.utils;


import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {

    private long min, max;

    public InputFilterMinMax(Long min, Long max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Long.parseLong(min);
        this.max = Long.parseLong(max);
    }


    private boolean isInRange(Long a, Long b, Long c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }


    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            Long input = Long.parseLong(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }
}