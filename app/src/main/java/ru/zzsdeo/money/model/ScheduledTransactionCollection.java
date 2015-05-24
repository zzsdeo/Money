package ru.zzsdeo.money.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.db.DatabaseContentProvider;
import ru.zzsdeo.money.db.TableScheduledTransactions;

public class ScheduledTransactionCollection extends ArrayList<ScheduledTransaction> {

    private final ContentResolver contentResolver;
    private final long endOfTimeSetting;
    private final SharedPreferences mSharedPreferences;

    public ScheduledTransactionCollection(Context context) {
        mSharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        endOfTimeSetting = mSharedPreferences.getInt(Constants.NUMBER_OF_MONTHS, Constants.DEFAULT_NUM_OF_MONTHS) * 2592000000l;
        contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS,
                new String[]{
                        TableScheduledTransactions.COLUMN_ID
                },
                null,
                null,
                null
        );
        long id;
        if (c.moveToFirst()) {
            do {
                id = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_ID));
                add(new ScheduledTransaction(context, id));
            } while (c.moveToNext());
        }
        c.close();
    }

    public ScheduledTransactionCollection(Context context, String[] params) {
        mSharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        endOfTimeSetting = mSharedPreferences.getInt(Constants.NUMBER_OF_MONTHS, Constants.DEFAULT_NUM_OF_MONTHS) * 2592000000l;
        String[] args;
        if (params.length > 2) {
            ArrayList<String> argsList = new ArrayList<>();
            argsList.addAll(Arrays.asList(params).subList(2, params.length));
            args = (String[]) argsList.toArray();
        } else {
            args = null;
        }
        contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(
                DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS,
                new String[]{
                        TableScheduledTransactions.COLUMN_ID
                },
                params[0],
                args,
                params[1]
        );
        long id;
        if (c.moveToFirst()) {
            do {
                id = c.getLong(c.getColumnIndex(TableScheduledTransactions.COLUMN_ID));
                add(new ScheduledTransaction(context, id));
            } while (c.moveToNext());
        }
        c.close();
    }

    public void addScheduledTransaction(
            long dateInMill,
            float amount,
            String comment,
            boolean needApprove,
            int repeatingTypeId) {

        ContentValues cv = new ContentValues();
        cv.put(TableScheduledTransactions.COLUMN_DATE_IN_MILL, dateInMill);
        cv.put(TableScheduledTransactions.COLUMN_AMOUNT, amount);
        cv.put(TableScheduledTransactions.COLUMN_COMMENT, comment);
        if (needApprove) {
            cv.put(TableScheduledTransactions.COLUMN_NEED_APPROVE, 1);
        } else {
            cv.put(TableScheduledTransactions.COLUMN_NEED_APPROVE, 0);
        }
        cv.put(TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID, repeatingTypeId);
        contentResolver.insert(DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS, cv);
    }

    public void removeScheduledTransaction(long id) {
        int deletedRows = contentResolver.delete(
                DatabaseContentProvider.CONTENT_URI_SCHEDULED_TRANSACTIONS,
                TableScheduledTransactions.COLUMN_ID + " = " + id,
                null
        );
        if (deletedRows > 0) remove(id);
    }

    public ArrayList<TransactionsHolder> getTransactionsHolderCollection() {
        ArrayList<TransactionsHolder> mTransactions = new ArrayList<>();
        for (ScheduledTransaction st : this) {
            Calendar now = Calendar.getInstance();
            long endOfTime = now.getTimeInMillis() + endOfTimeSetting;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(st.getDateInMill());
            switch (st.getRepeatingTypeId()) {
                case 0: // один раз
                    mTransactions.add(new TransactionsHolder(st.getDateInMill(), st));
                    break;
                case 1: // каждый день
                    do {
                        mTransactions.add(new TransactionsHolder(calendar.getTimeInMillis(), st));
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    } while (calendar.getTimeInMillis() < endOfTime);
                    break;
                case 2: // каждый будний день
                    do {
                        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                            mTransactions.add(new TransactionsHolder(calendar.getTimeInMillis(), st));
                        }
                        calendar.add(Calendar.DAY_OF_WEEK, 1);
                    } while (calendar.getTimeInMillis() < endOfTime);
                    break;
                case 3: // каждое определенное число
                    do {
                        mTransactions.add(new TransactionsHolder(calendar.getTimeInMillis(), st));
                        calendar.add(Calendar.MONTH, 1);
                    } while (calendar.getTimeInMillis() < endOfTime);
                    break;
                case 4: // каждый определенный день недели
                    do {
                        mTransactions.add(new TransactionsHolder(calendar.getTimeInMillis(), st));
                        calendar.add(Calendar.DAY_OF_MONTH, 7);
                    } while (calendar.getTimeInMillis() < endOfTime);
                    break;
                case 5: // каждый последний день месяца
                    do {
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                        mTransactions.add(new TransactionsHolder(calendar.getTimeInMillis(), st));
                        calendar.add(Calendar.MONTH, 1);
                    } while (calendar.getTimeInMillis() < endOfTime);
                    break;
            }
        }

        // сортируем по дате

        Collections.sort(mTransactions, new Comparator<TransactionsHolder>() {
            @Override
            public int compare(TransactionsHolder lhs, TransactionsHolder rhs) {
                if (lhs.dateTime > rhs.dateTime) {
                    return 1;
                } else if (lhs.dateTime < rhs.dateTime) {
                    return -1;
                } else {
                    if (lhs.scheduledTransaction.getScheduledTransactionId() > rhs.scheduledTransaction.getScheduledTransactionId()) {
                        return 1;
                    } else if (lhs.scheduledTransaction.getScheduledTransactionId() < rhs.scheduledTransaction.getScheduledTransactionId()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        });

        // рассчитываем балансы

        String stringBalance = mSharedPreferences.getString(Constants.BALANCE, "");
        float balance = 0;
        if (stringBalance != null) {
            if (!stringBalance.isEmpty()) balance = Float.parseFloat(stringBalance);
        }
        for (TransactionsHolder transactionsHolder : mTransactions) {
            balance = balance + transactionsHolder.scheduledTransaction.getAmount();
            transactionsHolder.setBalance(balance);
        }

        return mTransactions;
    }

    public static class TransactionsHolder {
        public final ScheduledTransaction scheduledTransaction;
        public final long dateTime;
        private float balance;

        public TransactionsHolder(long dateTime, ScheduledTransaction scheduledTransaction) {
            this.dateTime = dateTime;
            this.scheduledTransaction = scheduledTransaction;
        }

        public void setBalance(float balance) {
            this.balance = balance;
        }

        public float getBalance() {
            return BigDecimal.valueOf(balance).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        }
    }
}
