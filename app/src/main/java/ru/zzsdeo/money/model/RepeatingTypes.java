package ru.zzsdeo.money.model;

import android.content.Context;

import ru.zzsdeo.money.R;

public class RepeatingTypes {

    public final String[] types;

    public RepeatingTypes(Context context) {
        types = new String[]{
                context.getString(R.string.once),
                "Каждый день",
                "Каждый будний день",
                "Каждое определенное число",
                "Каждый определенный день недели",
                "Каждый последний день месяца"
        };
    }
}
