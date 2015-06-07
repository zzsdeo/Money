package ru.zzsdeo.money.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;

import java.math.BigDecimal;
import java.util.Calendar;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.activities.MainActivity;
import ru.zzsdeo.money.db.TableScheduledTransactions;
import ru.zzsdeo.money.model.ScheduledTransaction;
import ru.zzsdeo.money.model.ScheduledTransactionCollection;

public class NotificationIntentService extends IntentService {

    private static final String NOTIFICATION_INTENT_SERVICE_NAME = "notification_service";

    public NotificationIntentService() {
        super(NOTIFICATION_INTENT_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        long nowInMill = Calendar.getInstance().getTimeInMillis();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        ScheduledTransactionCollection scheduledTransactionCollection = new ScheduledTransactionCollection(getApplicationContext(), // просроченные транзакции, требующие подтверждения
                new String[] {
                        TableScheduledTransactions.COLUMN_DATE_IN_MILL + " <= " + nowInMill +
                                " AND " + TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID + " = " + 0 +
                                " AND " + TableScheduledTransactions.COLUMN_NEED_APPROVE + " = " + 1,
                        null
                });

        if (scheduledTransactionCollection.isEmpty()) {
            notificationManager.cancelAll();
            return;
        }

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

        String text = "";
        String comment;
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        for (ScheduledTransaction scheduledTransaction : scheduledTransactionCollection) {
            comment = scheduledTransaction.getComment();
            text = text + ", " + comment;
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(comment + "   " +
                    String.valueOf(
                            BigDecimal.valueOf(scheduledTransaction.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue()
                    ));
            stringBuilder.setSpan(
                    new TextAppearanceSpan(getApplicationContext(), R.style.InboxStyleNotifComment),
                    0,
                    comment.length(),
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.setSpan(
                    new TextAppearanceSpan(getApplicationContext(), R.style.InboxStyleNotifAmount),
                    comment.length(),
                    stringBuilder.length(),
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            inboxStyle.addLine(stringBuilder);
        }
        inboxStyle.setSummaryText(getString(R.string.total) + ": " + scheduledTransactionCollection.size());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_action_action_account_balance_wallet2)
                .setContentTitle(getString(R.string.need_confirm))
                .setContentText(text.replaceFirst(",", "").trim())
                .setGroup(Constants.NOTIFICATION_GROUP_KEY)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle);

        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
    }
}