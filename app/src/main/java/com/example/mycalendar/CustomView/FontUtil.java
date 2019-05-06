package com.example.mycalendar.CustomView;

import android.graphics.Paint;

class FontUtil {

    static float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    static float getFontHeight(Paint paint)  {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    static float getFontLeading(Paint paint)  {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }

}
