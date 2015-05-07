package ru.zzsdeo.money.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.astuetz.PagerSlidingTabStrip;

import ru.zzsdeo.money.fragments.FragmentCollection;

public class MainPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    private FragmentCollection fragmentCollection;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentCollection = new FragmentCollection();
    }

    @Override
    public Fragment getItem(int position) {
        return (Fragment) fragmentCollection.get(position);
    }

    @Override
    public int getCount() {
        return fragmentCollection.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getPageIconResId(int i) {
        return fragmentCollection.get(i).getIconResId();
    }
}
