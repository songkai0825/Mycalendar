package com.example.mycalendar.CustomView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class DayPagerAdapter extends PagerAdapter {

    private Context mcontext;

    public DayPagerAdapter(Context context) {
        mcontext = context;
    }

    @Override
    public int getCount() {
        return 500;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        CustomDateView customDateView = new CustomDateView(mcontext, position);
        container.addView(customDateView);
        return customDateView;
    }

}
