package com.wolfmobileapps.myweatherforecast.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wolfmobileapps.myweatherforecast.Fragment5days;
import com.wolfmobileapps.myweatherforecast.FragmentToday;
import com.wolfmobileapps.myweatherforecast.FragmentTomorrow;
import com.wolfmobileapps.myweatherforecast.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }


    private Fragment mCurrentFragment;
    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = FragmentToday.newInstance();
                break;
            case 1:
                fragment = Fragment5days.newInstance();
                break;
//            case 2:
//                fragment = FragmentTomorrow.newInstance();
//                break;
        }
        return fragment;

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

}