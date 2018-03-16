package com.ambuja.clubapp.utils;

/**
 * Created by pulkitm on 3/8/2016.
 */


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.squareup.picasso.Transformation;

public class CircleTransformGray implements Transformation {
    Boolean showborder = false;

    public CircleTransformGray(Boolean border) {
        showborder = border;
    }

    public CircleTransformGray() {
        showborder = false;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        Paint paintBorder = new Paint();
        paintBorder.setAntiAlias(true);
        paintBorder.setStyle(Style.STROKE);
        paintBorder.setStrokeWidth(5);
        paintBorder.setColor(Color.parseColor("#272727"));
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r - 2, paint);
        if (showborder)
            canvas.drawCircle(r, r, r - 1, paintBorder);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}