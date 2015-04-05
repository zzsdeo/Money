package ru.zzsdeo.money.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import ru.zzsdeo.money.Constants;

public class BootStartUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, UpdateTransactionsIntentService.class);
        PendingIntent pi = PendingIntent.getService(context, Constants.UPDATE_TRANSACTIONS_INTENT_SERVICE_REQUEST_CODE, i, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(), 4*AlarmManager.INTERVAL_HOUR, pi);
    }
}
