package com.example.mycalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mycalendar.CustomView.DayPagerAdapter;
import com.example.mycalendar.CustomView.DayViewPager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateView extends LinearLayout {

    private float lastPosition;

    private TextView yearTv;

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressLint({"InflateParams","SimpleDateFormat"})
    private void init(Context context){
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM月  yyyy");
        dateFormat.format(calendar.getTime());

        View view = LayoutInflater.from(context).inflate(R.layout.date_view, null);
        addView(view);

        yearTv = view.findViewById(R.id.year_textview);
        yearTv.setText(dateFormat.format(calendar.getTime()));

        DayViewPager dayViewPager = view.findViewById(R.id.day_viewpager);
        dayViewPager.setAdapter(new DayPagerAdapter(context));
        lastPosition = dayViewPager.getCurrentItem();
        dayViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (lastPosition < position) {
                    calendar.add(Calendar.MONDAY,+1);
                    yearTv.setText(dateFormat.format(calendar.getTime()));
                } else if (lastPosition > position) {
                    calendar.add(Calendar.MONDAY,-1);
                    yearTv.setText(dateFormat.format(calendar.getTime()));
                }
                lastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }

        });
    }

}
