package com.example.mycalendar.CustomView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomWeekView extends View {

    private static final String TAG = "CustomWeekView";

    private Paint mTtPaint;//字体颜色

    private int mTotleWidth;//总宽度

    private final String[] WEEK_ARRAY = new String[]{"日", "一", "二", "三", "四", "五", "六"};

    public CustomWeekView(Context context) {
        super(context);
        initPaint();
    }

    public CustomWeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public CustomWeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸

        float heightSize = FontUtil.getFontHeight(mTtPaint);//保证week显示区域可以容下最高的字母

        mTotleWidth = widthSize / 7;
        setMeasuredDimension(widthSize, Math.round(heightSize));
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        for(int i = 0; i < WEEK_ARRAY.length; i++){
            int len = (int)FontUtil.getFontlength(mTtPaint, WEEK_ARRAY[i]);
            int x = i * mTotleWidth + (mTotleWidth - len) / 2;
            canvas.drawText(WEEK_ARRAY[i], x, FontUtil.getFontLeading(mTtPaint), mTtPaint);
        }
    }

    private void initPaint() {
        float density = getContext().getApplicationContext().getResources().getDisplayMetrics().density;
        mTtPaint = new Paint();
        mTtPaint.setTextSize(12 * density);
        mTtPaint.setColor(Color.GRAY);
        mTtPaint.setAntiAlias(true);
    }

}
