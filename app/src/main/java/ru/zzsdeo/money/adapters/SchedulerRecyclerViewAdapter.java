package ru.zzsdeo.money.adapters;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.activities.EditScheduledTransactionActivity;
import ru.zzsdeo.money.activities.MainActivity;
import ru.zzsdeo.money.dialogs.Dialogs;
import ru.zzsdeo.money.model.ScheduledTransaction;
import ru.zzsdeo.money.model.ScheduledTransactionCollection;

public class SchedulerRecyclerViewAdapter extends RecyclerView.Adapter<SchedulerRecyclerViewAdapter.ViewHolder>  {

    public static final String SCHEDULED_TRANSACTION_ID = "scheduled_transaction_id";

    private ArrayList<TransactionsHolder> mTransactions;
    private MainActivity mContext;
    private ScheduledTransactionCollection mTransactionCollection;
    private final static String DATE_FORMAT = "dd.MM.yy, HH:mm";
    private FragmentManager mFragmentManager;
    private static final Long END_OF_TIME = 31536000000l;

    public SchedulerRecyclerViewAdapter(MainActivity context) {
        mTransactionCollection = new ScheduledTransactionCollection(context, ScheduledTransactionCollection.SORTED_BY_DATE_DESC);
        mTransactions = getSortedTransactions();
        mContext = context;
        mFragmentManager = context.getFragmentManager();
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_transaction_scheduler, parent, false);
        return new ViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScheduledTransaction transaction = mTransactions.get(position).scheduledTransaction;
        float balance = mTransactions.get(position).getBalance();

        String[] items = new String[] {
                transaction.getComment(),
                String.valueOf(transaction.getAmount()),
                new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date(mTransactions.get(position).dateTime)),
                String.valueOf(balance)
        };

        int sign = 1;
        if (balance < 0) sign = -1;
        holder.setItems(items, sign);
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    @Override
    public long getItemId(int position) {
        return mTransactions.get(position).scheduledTransaction.getScheduledTransactionId();
    }

    public void refreshDataSet() {
        mTransactionCollection = new ScheduledTransactionCollection(mContext, ScheduledTransactionCollection.SORTED_BY_DATE_DESC);
        mTransactions = getSortedTransactions();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements Toolbar.OnMenuItemClickListener {
        private TextView mTextView1, mTextView2;
        private Toolbar mToolbar;
        private SchedulerRecyclerViewAdapter mAdapter;

        public ViewHolder(View view, SchedulerRecyclerViewAdapter historyRecyclerViewAdapter) {
            super(view);

            mAdapter = historyRecyclerViewAdapter;

            mTextView1 = (TextView) view.findViewById(R.id.text1);
            mTextView2 = (TextView) view.findViewById(R.id.text2);

            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mToolbar.inflateMenu(R.menu.recycler_card_toolbar);
            mToolbar.setOnMenuItemClickListener(this);
        }

        public void setItems(String[] items, int sign) {
            mToolbar.setTitle(items[0]);
            mToolbar.setSubtitle(items[1]);
            mTextView1.setText(items[2]);
            if (sign < 0) {
                mTextView2.setTextColor(Color.RED);
            } else {
                mTextView2.setTextColor(Color.GREEN);
            }
            mTextView2.setText(items[3]);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.delete_item:
                    Dialogs dialog = new Dialogs();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.DELETE_SCHEDULED_TRANSACTION);
                    bundle.putLong(Dialogs.ID, mAdapter.mTransactions.get(getPosition()).scheduledTransaction.getScheduledTransactionId());
                    dialog.setArguments(bundle);
                    dialog.show(mAdapter.mFragmentManager, Dialogs.DIALOGS_TAG);
                    return true;
                case R.id.edit_item:
                    Intent i = new Intent(mAdapter.mContext, EditScheduledTransactionActivity.class);
                    Bundle b = new Bundle();
                    b.putLong(SCHEDULED_TRANSACTION_ID, mAdapter.mTransactions.get(getPosition()).scheduledTransaction.getScheduledTransactionId());
                    i.putExtras(b);
                    mAdapter.mContext.startActivityForResult(i, Constants.EDIT_SCHEDULED_TRANSACTION_REQUEST_CODE);
                    return true;
                default:
                    return false;
            }
        }
    }

    private ArrayList<TransactionsHolder> getSortedTransactions() {
        mTransactions = new ArrayList<>();
        for (ScheduledTransaction st : mTransactionCollection) {
            Calendar now = Calendar.getInstance();
            long endOfTime = now.getTimeInMillis() + END_OF_TIME;
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

        float balance = 0;
        for (TransactionsHolder transactionsHolder : mTransactions) {
            balance = balance + transactionsHolder.scheduledTransaction.getAmount();
            transactionsHolder.setBalance(balance);
        }

        return mTransactions;
    }

    private static class TransactionsHolder {
        public ScheduledTransaction scheduledTransaction;
        public long dateTime;
        private float balance;

        public TransactionsHolder(long dateTime, ScheduledTransaction scheduledTransaction) {
            this.dateTime = dateTime;
            this.scheduledTransaction = scheduledTransaction;
        }

        public void setBalance(float balance) {
            this.balance = balance;
        }

        public float getBalance() {
            return balance;
        }
    }
}
