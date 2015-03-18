package ru.zzsdeo.money.fragments;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

public class FragmentCollection extends ArrayList<IFragment> {

    public FragmentCollection() {
        add(0, new HistoryFragment());
        add(1, new MainFragment());
    }
}
