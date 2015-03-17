package ru.zzsdeo.money.fragments;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

public class FragmentCollection extends ArrayList<Fragment> {

    public FragmentCollection() {
        add(0, new MainFragment());
        add(1, new MainFragment());
        add(2, new MainFragment());
    }
}
