package ru.zzsdeo.money.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.math.BigDecimal;
import java.util.Calendar;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.activities.MainActivity;
import ru.zzsdeo.money.db.TableScheduledTransactions;
import ru.zzsdeo.money.model.ScheduledTransaction;
import ru.zzsdeo.money.model.ScheduledTransactionCollection;

public class UpdateTransactionsIntentService extends IntentService {

    private static final String UPDATE_TRANSACTIONS_INTENT_SERVICE_NAME = "update_transactions_service";

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
        Calendar calendar = Calendar.getInstance();
        for (ScheduledTransaction transaction : scheduledTransactionCollection) {
            calendar.setTimeInMillis(transaction.getDateInMill());
            switch (transaction.getRepeatingTypeId()) {
                case 1: // каждый день
                    do {
                        cutTransaction(calendar.getTimeInMillis(), scheduledTransactionCollection, transaction);
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    } while (calendar.getTimeInMillis() <= nowInMill);
                    transaction.setDateInMill(calendar.getTimeInMillis());
                    break;
                case 2: // каждый будний день
                    do {
                        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                            cutTransaction(calendar.getTimeInMillis(), scheduledTransactionCollection, transaction);
                        }
                        calendar.add(Calendar.DAY_OF_WEEK, 1);
                    } while (calendar.getTimeInMillis() <= nowInMill);
                    while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                        calendar.add(Calendar.DAY_OF_WEEK, 1);
                    }
                    transaction.setDateInMill(calendar.getTimeInMillis());
                    break;
                case 3: // каждое определенное число
                    do {
                        cutTransaction(calendar.getTimeInMillis(), scheduledTransactionCollection, transaction);
                        calendar.add(Calendar.MONTH, 1);
                    } while (calendar.getTimeInMillis() <= nowInMill);
                    transaction.setDateInMill(calendar.getTimeInMillis());
                    break;
                case 4: // каждый определенный день недели
                    do {
                        cutTransaction(calendar.getTimeInMillis(), scheduledTransactionCollection, transaction);
                        calendar.add(Calendar.DAY_OF_MONTH, 7);
                    } while (calendar.getTimeInMillis() <= nowInMill);
                    transaction.setDateInMill(calendar.getTimeInMillis());
                    break;
                case 5: // каждый последний день месяца
                    do {
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                        cutTransaction(calendar.getTimeInMillis(), scheduledTransactionCollection, transaction);
                        calendar.add(Calendar.MONTH, 1);
                    } while (calendar.getTimeInMillis() <= nowInMill);
                    transaction.setDateInMill(calendar.getTimeInMillis());
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

        for (ScheduledTransaction transaction : scheduledTransactionCollection) {
            scheduledTransactionCollection.removeScheduledTransaction(transaction.getScheduledTransactionId());
        }

        // посылаем сообщение на обновление списка транзакций

        Intent i = new Intent(ServiceReceiver.BROADCAST_ACTION);
        i.putExtra(ServiceReceiver.ACTION, ServiceReceiver.REFRESH_SCHEDULED_TRANSACTIONS);
        sendBroadcast(i);

        // вешаем нотификацию

        scheduledTransactionCollection = new ScheduledTransactionCollection(getApplicationContext(), // просроченные транзакции, требующие подтверждения
                new String[] {
                        TableScheduledTransactions.COLUMN_DATE_IN_MILL + " <= " + nowInMill +
                                " AND " + TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID + " = " + 0 +
                                " AND " + TableScheduledTransactions.COLUMN_NEED_APPROVE + " = " + 1,
                        null
                });

        /*NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_action_action_account_balance_wallet2)
                .setGroup(Constants.NOTIFICATION_GROUP_KEY)
                .setGroupSummary(true);

        i = new Intent(getApplicationContext(), MainActivity.class);
        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);
        mBuilder.setContentIntent(pendingIntent);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Требуется подтверждение:");

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        for (ScheduledTransaction scheduledTransaction : scheduledTransactionCollection) {
            mBuilder.setContentTitle(scheduledTransaction.getComment())
                    .setContentText(
                            String.valueOf(
                                    BigDecimal.valueOf(scheduledTransaction.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue()
                            ));
            inboxStyle.addLine(scheduledTransaction.getComment());
            mBuilder.setStyle(inboxStyle);
            mNotificationManager.notify(10, mBuilder.build());
        }*/


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        i = new Intent(getApplicationContext(), MainActivity.class);
        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

        for (ScheduledTransaction scheduledTransaction : scheduledTransactionCollection) {
            NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.ic_action_action_account_balance_wallet2)
                    .setGroup(Constants.NOTIFICATION_GROUP_KEY)
                            //.setGroupSummary(true)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(scheduledTransaction.getComment())
                    .setContentText(
                            String.valueOf(
                                    BigDecimal.valueOf(scheduledTransaction.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue()
                            ));
            notificationManager.notify((int)scheduledTransaction.getScheduledTransactionId(), mBuilder.build());
        }
    }

    private void cutTransaction(long dateInMill, ScheduledTransactionCollection scheduledTransactionCollection, ScheduledTransaction transaction) {
        scheduledTransactionCollection.addScheduledTransaction(
                dateInMill,
                transaction.getAmount(),
                transaction.getComment(),
                transaction.getNeedApprove(),
                0
        );
    }
}