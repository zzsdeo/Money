package ru.zzsdeo.money.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.zzsdeo.money.fragments.FragmentCollection;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private FragmentCollection fragmentCollection;
    public static final String[] TITLES = {"111", "222", "333"};

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentCollection = new FragmentCollection();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragmentClass = fragmentCollection.get(position);
        try {
            return fragmentClass.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getCount() {
        return fragmentCollection.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}
