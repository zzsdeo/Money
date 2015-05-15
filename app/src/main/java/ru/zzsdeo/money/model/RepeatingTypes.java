package ru.zzsdeo.money.model;

import android.content.Context;

import java.util.ArrayList;

import ru.zzsdeo.money.R;

public class RepeatingTypes extends ArrayList<String> {

    public RepeatingTypes(Context context) {
        add(context.getString(R.string.once));
        add("Каждый день");
        add("Каждый будний день");
        add("Каждое определенное число");
        add("Каждый определенный день недели");
        add("Каждый последний день месяца");
    }
}