package ru.zzsdeo.money.model;

import android.content.Context;

import java.util.ArrayList;

import ru.zzsdeo.money.R;

public class RepeatingTypes extends ArrayList<String> {

    public RepeatingTypes(Context context) {
        add(context.getString(R.string.once));
        add(context.getString(R.string.every_day));
        add(context.getString(R.string.every_business_day));
        add(context.getString(R.string.every_day_of_month));
        add(context.getString(R.string.every_day_of_week));
        add(context.getString(R.string.every_last_day_of_month));
    }
}