package ru.zzsdeo.money.services;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import ru.zzsdeo.money.db.TableScheduledTransactions;
import ru.zzsdeo.money.model.ScheduledTransaction;
import ru.zzsdeo.money.model.ScheduledTransactionCollection;

public class UpdateTransactionsIntentService extends IntentService {

    public static final String UPDATE_TRANSACTIONS_INTENT_SERVICE_NAME = "update_transactions_service";

    public UpdateTransactionsIntentService() {
        super(UPDATE_TRANSACTIONS_INTENT_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ScheduledTransactionCollection scheduledTransactionCollection = new ScheduledTransactionCollection(getApplicationContext(),
                new String[] {
                        TableScheduledTransactions.COLUMN_DATE_IN_MILL + " <= " + Calendar.getInstance().getTimeInMillis() +
                                " AND " + TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID + " != " + 0,
                        null
                });
        Set<Long> ids = scheduledTransactionCollection.keySet();
        ArrayList<Long> longs = new ArrayList<>(ids);
        Calendar calendar = Calendar.getInstance();
        for (long id : longs) {
            scheduledTransactionCollection.addScheduledTransaction(
                    scheduledTransactionCollection.get(id).getAccountId(),
                    scheduledTransactionCollection.get(id).getDateInMill(),
                    scheduledTransactionCollection.get(id).getAmount(),
                    scheduledTransactionCollection.get(id).getCommission(),
                    scheduledTransactionCollection.get(id).getComment(),
                    scheduledTransactionCollection.get(id).getDestinationAccountId(),
                    scheduledTransactionCollection.get(id).getNeedApprove(),
                    0,
                    scheduledTransactionCollection.get(id).getCategoryId(),
                    scheduledTransactionCollection.get(id).getLinkedTransactionId()
            );

            calendar.setTimeInMillis(scheduledTransactionCollection.get(id).getDateInMill());
            switch (scheduledTransactionCollection.get(id).getRepeatingTypeId()) {
                case 1:
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    break;
                case 2:
                    calendar.add(Calendar.DAY_OF_WEEK, 1);
                    while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                        calendar.add(Calendar.DAY_OF_WEEK, 1);
                    }
                    break;
                case 3:
                    calendar.add(Calendar.MONTH, 1);
                    break;
                case 4:
                    calendar.add(Calendar.DAY_OF_MONTH, 7);
                    break;
                case 5:
                    calendar.add(Calendar.MONTH, 1);
                    break;
            }
            scheduledTransactionCollection.get(id).setDateInMill(calendar.getTimeInMillis());
        }
    }
}
