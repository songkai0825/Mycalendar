package com.example.mycalendar.CustomView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.example.mycalendar.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomDateView extends View {


    private boolean isPressed;                      /*是否有按下效果*/

    private String mSelectDateStr;                  /*选中日期*/

    private int mNormalTextColor;                   /*正常日期文字颜色*/

    private float mTextSize;                     /*日期文字大小*/

    private int mSelectTextColor;                   /*选中日期文字的颜色*/

    private int mCurrentTextColor;                  /*当前日期文字的颜色*/

    private int mSelectBgd;                         /*选中背景*/

    private int mCurrentBgd;                        /*当前日期背景颜色*/

    private float mBgdRadius;                       /*选中背景半径*/

    private float mLineSpac;                        /*行间距*/

    private Paint mPaint;                           /*日期的画笔*/

    private Paint bgdPaint;                         /*圆圈背景的画笔*/

    private float dayHeight, oneHeight;

    private int columnWidth;                        /*每列宽度*/

    private boolean isCurrentMonth;                 /*月份是否是当前月*/

    private int isCurrentDay;                       /*当前日期*/

    private int selectDay;

    private boolean isSelect;

    private int firstDayIndex;                      /*当月第一天位置索引*/

    private int firstLineDaysNum, lastLineDaysNum;  /*第一行、最后一行能显示多少日期*/

    private int lineNum;                            /*日期行数*/

    private int dayOfMonth;

    private float mMinSlop;

    private float density;

    public CustomDateView(Context context, int position) {
        this(context, null, 0);
        init();
        setDate(position);
    }

    public CustomDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density = context.getApplicationContext().getResources().getDisplayMetrics().density;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomDateView, defStyleAttr, 0);
        mNormalTextColor = a.getColor(R.styleable.CustomDateView_mTextColorDay, Color.BLACK);
        mSelectTextColor = a.getColor(R.styleable.CustomDateView_mSelectTextColor, Color.BLACK);
        mCurrentTextColor = a.getColor(R.styleable.CustomDateView_mCurrentTextColor, getResources().getColor(R.color.colorCurrentText));
        mTextSize = a.getDimension(R.styleable.CustomDateView_mTextSizeDay, density * 14);
        mCurrentBgd = a.getColor(R.styleable.CustomDateView_mCurrentBg, getResources().getColor(R.color.colorCurrentBackground));
        mSelectBgd = a.getColor(R.styleable.CustomDateView_mSelectBg, getResources().getColor(R.color.colorSelectBgd));
        mBgdRadius = a.getDimension(R.styleable.CustomDateView_mSelectRadius, density * 16);
        mLineSpac = a.getDimension(R.styleable.CustomDateView_mLineSpac, density * 8);

        a.recycle();  //回收
    }

    private void init(){
        //初始化画笔
        mPaint = new Paint();
        bgdPaint = new Paint();

        mPaint.setAntiAlias(true); //抗锯齿
        mPaint.setStrokeWidth(1f);
        mPaint.setTextSize(mTextSize);

        bgdPaint.setAntiAlias(true); //抗锯齿

        dayHeight = FontUtil.getFontHeight(mPaint);
        //每行高度 = 背景圆圈直径 + 间距
        oneHeight = mBgdRadius * 2 + mLineSpac;
        //viewpage识别最小滑动距离
        mMinSlop = Math.min(ViewConfiguration.get(getContext()).getScaledTouchSlop() * density, mBgdRadius);
    }

    /*设置月份及计算一些参数*/
    private void setDate(int position){
        //设置的月份
        Calendar selectedMonth = Calendar.getInstance();// 临时
        selectedMonth.add(Calendar.MONTH, position - 250);
        selectedMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //获取今天日期
        isCurrentDay = calendar.get(Calendar.DAY_OF_MONTH);
        //判断是否为当月当前日期
        if ((selectedMonth.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) &&
                selectedMonth.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
            isCurrentMonth = true;
        }

        calendar.setTime(selectedMonth.getTime());
        //月份天数
        dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        //本月第一天显示在第一行的位置
        firstDayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        lineNum = 1;
        //日历中第一行显示的天数
        firstLineDaysNum = 7 - firstDayIndex;
        lastLineDaysNum = 0;
        int remainDays = dayOfMonth - firstLineDaysNum;
        while (remainDays > 7) {
            lineNum++;
            remainDays -= 7;
        }
        //日历中最后一行天数
        if(remainDays > 0){
            lineNum++;
            lastLineDaysNum = remainDays;
        }
        mSelectDateStr = Date2str(selectedMonth.getTime());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //控件宽度
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        columnWidth = widthSize / 7;
        //高度 = 标题高度 + 星期高度 + 日期行数 * 每行高度
        float height = 6 * oneHeight;
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), (int)height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float dayTextLeading = FontUtil.getFontLeading(mPaint);
        int dayIndex = firstDayIndex;
        float top = 0;

        for (int i = 0; i < dayOfMonth; i++) {
            int left = (dayIndex) * columnWidth;
            int day = i + 1;

            if(isCurrentMonth && (isCurrentDay == day)){
                //无论是否选中当前日期
                //设置当前日期背景,设置当前日期字体颜色
                mPaint.setColor(mCurrentTextColor);//字体颜色
                bgdPaint.setColor(mCurrentBgd);//背景圈颜色
                bgdPaint.setStyle(Paint.Style.FILL);  //实心
                canvas.drawCircle(left + (float)columnWidth / 2, top + mLineSpac + dayHeight / 2, mBgdRadius, bgdPaint);
            } else if (day == selectDay && isPressed) {
                //选中日期不是当前日期，根据isPressed确定是否需要按下效果，设置选中日期字体颜色
                mPaint.setColor(mSelectTextColor);//字体颜色
                bgdPaint.setStyle(Paint.Style.FILL);  //背景圆圈为实心
                bgdPaint.setColor(mSelectBgd);//背景圆圈颜色
                //绘制按下时的背景圆圈
                canvas.drawCircle(left + (float)columnWidth / 2, top + mLineSpac + dayHeight / 2, mBgdRadius, bgdPaint);
            } else {
                //设置非选中日期和非当前日期字体颜色，不需要背景圆圈
                mPaint.setColor(mNormalTextColor);//其他字体颜色
            }
            int len = (int)FontUtil.getFontlength(mPaint, day + "");
            int x = left + (columnWidth - len) / 2;
            //绘制日期
            canvas.drawText(day + "", x, top + mLineSpac + dayTextLeading, mPaint);
            //从第一行开始绘制，每行7天，每次循环增加一个行高
            if (++dayIndex == 7) {
                dayIndex = 0;
                top = top + oneHeight;
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String Date2str(Date month){
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月");
        return df.format(month);
    }

    //触摸的焦点坐标
    private PointF focusPoint = new PointF();

    private float lastX;
    private float lastY;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        isSelect = false;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                focusPoint.set(event.getX(), event.getY());
                lastX = event.getX();
                lastY = event.getY();
                touchCompute(focusPoint);
                isPressed = true;
                break;

            case MotionEvent.ACTION_CANCEL://x轴滑动距离大于viewpage判定的最小距离，viewpage会滑动，子view会触发ACTION_CANCEL

            case MotionEvent.ACTION_UP:
                //计算按下时和抬起时的位移差，判断是否认为选中了某一日期
                if (Math.abs(lastX - event.getX()) < mMinSlop && Math.abs(lastY - event.getY()) < mMinSlop) {
                    isSelect = true;
                }
                focusPoint.set(event.getX(), event.getY());
                touchCompute(focusPoint);
                isPressed = false;
                break;
        }
        return true;

    }

    private void touchCompute(final PointF point){

        boolean availability = false;  //事件是否有效
        //日期部分
        float top = oneHeight;
        int foucsLine = 1;
        //根据焦点的Y坐标找到所在行
        while(foucsLine <= lineNum){
            if(top >= point.y){
                availability = true;
                break;
            }
            top += oneHeight;
            foucsLine ++;
        }
        selectDay = 0;
        if (availability) {
            //根据X坐标找到具体的焦点日期
            int xIndex = (int)(point.x / columnWidth) + 1;
            if(foucsLine == 1){
                //第一行
                if (xIndex > firstDayIndex) {
                    setSelectedDay(xIndex - firstDayIndex);
                } else {
                    invalidate();//第一行1号前的位置认为无效
                }
            } else if(foucsLine == lineNum) {
                //最后一行
                if (xIndex <= lastLineDaysNum) {
                    setSelectedDay(firstLineDaysNum + (foucsLine - 2) * 7 + xIndex);
                } else {
                    invalidate();//最后一行最后一天后的位置认为无效
                }
            } else {
                setSelectedDay(firstLineDaysNum + (foucsLine - 2) * 7 + xIndex);
            }
        } else {
            invalidate();
        }
    }

    /*选中的日期*/
    private void setSelectedDay(int day){
        selectDay = day;
        if (isSelect) {
            Toast.makeText(getContext(), "选中日期: " + mSelectDateStr + selectDay + "日", Toast.LENGTH_SHORT).show();
        }
        invalidate();
    }

}
