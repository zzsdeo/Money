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

        // отрезаем от повторяющихся транзакций те, которые уже просрочены, и выделяем их в отдельные неповторяющиеся транзакции

        long nowInMill = Calendar.getInstance().getTimeInMillis();
        ScheduledTransactionCollection scheduledTransactionCollection = new ScheduledTransactionCollection(getApplicationContext(),
                new String[] {
                        TableScheduledTransactions.COLUMN_DATE_IN_MILL + " <= " + nowInMill +
                                " AND " + TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID + " != " + 0,
                        null
                });
        Set<Long> ids = scheduledTransactionCollection.keySet();
        ArrayList<Long> longs = new ArrayList<>(ids);
        Calendar calendar = Calendar.getInstance();
        for (long id : longs) {
            calendar.setTimeInMillis(scheduledTransactionCollection.get(id).getDateInMill());
            switch (scheduledTransactionCollection.get(id).getRepeatingTypeId()) {
                case 1: // каждый день
                    do {
                        cutTransaction(calendar.getTimeInMillis(), scheduledTransactionCollection, id);
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    } while (calendar.getTimeInMillis() <= nowInMill);
                    scheduledTransactionCollection.get(id).setDateInMill(calendar.getTimeInMillis());
                    break;
                case 2: // каждый будний день
                    do {
                        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                            cutTransaction(calendar.getTimeInMillis(), scheduledTransactionCollection, id);
                        }
                        calendar.add(Calendar.DAY_OF_WEEK, 1);
                    } while (calendar.getTimeInMillis() <= nowInMill);
                    while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                        calendar.add(Calendar.DAY_OF_WEEK, 1);
                    }
                    scheduledTransactionCollection.get(id).setDateInMill(calendar.getTimeInMillis());
                    break;
                case 3: // каждое определенное число
                    do {
                        cutTransaction(calendar.getTimeInMillis(), scheduledTransactionCollection, id);
                        calendar.add(Calendar.MONTH, 1);
                    } while (calendar.getTimeInMillis() <= nowInMill);
                    scheduledTransactionCollection.get(id).setDateInMill(calendar.getTimeInMillis());
                    break;
                case 4: // каждый определенный день недели
                    do {
                        cutTransaction(calendar.getTimeInMillis(), scheduledTransactionCollection, id);
                        calendar.add(Calendar.DAY_OF_MONTH, 7);
                    } while (calendar.getTimeInMillis() <= nowInMill);
                    scheduledTransactionCollection.get(id).setDateInMill(calendar.getTimeInMillis());
                    break;
                case 5: // каждый последний день месяца
                    do {
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                        cutTransaction(calendar.getTimeInMillis(), scheduledTransactionCollection, id);
                        calendar.add(Calendar.MONTH, 1);
                    } while (calendar.getTimeInMillis() <= nowInMill);
                    scheduledTransactionCollection.get(id).setDateInMill(calendar.getTimeInMillis());
                    break;
            }
        }


        // определяем просроченные транзакции, не требующие подтверждения, и удаляем их

        scheduledTransactionCollection = new ScheduledTransactionCollection(getApplicationContext(),
                new String[] {
                        TableScheduledTransactions.COLUMN_DATE_IN_MILL + " <= " + nowInMill +
                                " AND " + TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID + " = " + 0 +
                                " AND " + TableScheduledTransactions.COLUMN_NEED_APPROVE + " = " + 0,
                        null
                });
        ids = scheduledTransactionCollection.keySet();
        longs = new ArrayList<>(ids);
        for (long id : longs) {
            scheduledTransactionCollection.removeScheduledTransaction(id);
        }

        // посылаем сообщение на обновление списка транзакций

        Intent i = new Intent(ServiceReceiver.BROADCAST_ACTION);
        i.putExtra(ServiceReceiver.ACTION, ServiceReceiver.REFRESH_SCHEDULED_TRANSACTIONS);
        sendBroadcast(i);
    }

    private void cutTransaction(long dateInMill, ScheduledTransactionCollection scheduledTransactionCollection, long id) {
        scheduledTransactionCollection.addScheduledTransaction(
                scheduledTransactionCollection.get(id).getAccountId(),
                dateInMill,
                scheduledTransactionCollection.get(id).getAmount(),
                scheduledTransactionCollection.get(id).getCommission(),
                scheduledTransactionCollection.get(id).getComment(),
                scheduledTransactionCollection.get(id).getDestinationAccountId(),
                scheduledTransactionCollection.get(id).getNeedApprove(),
                0,
                scheduledTransactionCollection.get(id).getCategoryId(),
                scheduledTransactionCollection.get(id).getLinkedTransactionId()
        );
    }
}