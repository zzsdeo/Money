package ru.zzsdeo.money.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.zzsdeo.money.activities.MainActivity;
import ru.zzsdeo.money.model.ScheduledTransactionCollection;

public class ServiceReceiver extends BroadcastReceiver {

    public static final String BROADCAST_ACTION = "ru.zzsdeo.money.services.broadcast_action";

    public static final String ACTION = "action";

    public static final int REFRESH_SCHEDULED_TRANSACTIONS = 10;
    public static final int REFRESH_ALL = 20;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getIntExtra(ACTION, 0)) {
            case REFRESH_SCHEDULED_TRANSACTIONS:
                ((MainActivity) context).schedulerRecyclerViewAdapter.refreshDataSet(
                        new ScheduledTransactionCollection(context).getTransactionsHolderCollection());
                break;
            case REFRESH_ALL:
                ((MainActivity) context).setTitleAsBalance();
                ((MainActivity) context).schedulerRecyclerViewAdapter.refreshDataSet(
                        new ScheduledTransactionCollection(context).getTransactionsHolderCollection());
                break;
        }
    }
}