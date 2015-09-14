package ru.zzsdeo.money.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.activities.EditScheduledTransactionActivity;
import ru.zzsdeo.money.activities.MainActivity;
import ru.zzsdeo.money.dialogs.Dialogs;
import ru.zzsdeo.money.model.ScheduledTransaction;
import ru.zzsdeo.money.model.ScheduledTransactionCollection;
import ru.zzsdeo.money.services.NotificationIntentService;

public class SchedulerRecyclerViewAdapter extends RecyclerView.Adapter<SchedulerRecyclerViewAdapter.ViewHolder>  {

    public static final String SCHEDULED_TRANSACTION_ID = "scheduled_transaction_id";
    public static final String DATE_FORMAT = "dd.MM.yy, HH:mm";

    public ArrayList<ScheduledTransactionCollection.TransactionsHolder> mTransactions;
    private final MainActivity mContext;
    private final FragmentManager mFragmentManager;
    private final long nowInMill;
    private final Calendar calendar = Calendar.getInstance();
    private final Calendar itemCal = Calendar.getInstance();

    public SchedulerRecyclerViewAdapter(MainActivity context, ArrayList<ScheduledTransactionCollection.TransactionsHolder> mTransactions) {
        this.mTransactions = mTransactions;
        mContext = context;
        mFragmentManager = context.getFragmentManager();
        nowInMill = calendar.getTimeInMillis();
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
        long dateTime = mTransactions.get(position).dateTime;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        String[] items = new String[] {
                transaction.getComment(),
                String.valueOf(transaction.getAmount()),
                getFormattedDateTime(dateTime, sharedPreferences.getBoolean(Constants.DISPLAY_DATE_TIME, true)),
                String.valueOf(balance)
        };

        int sign = 1;
        if (balance < 0) sign = -1;

        boolean overdue = false;
        if (transaction.getNeedApprove() && transaction.getRepeatingTypeId() == 0 && dateTime <= nowInMill) overdue = true;

        holder.setItems(items, sign, overdue, transaction.getNeedApprove(), transaction.getRepeatingTypeId());
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    public void refreshDataSet(ArrayList<ScheduledTransactionCollection.TransactionsHolder> mTransactions) {
        this.mTransactions = mTransactions;
        notifyDataSetChanged();
        mContext.startService(new Intent(mContext, NotificationIntentService.class));
    }

    private String getFormattedDateTime (long dateInMill, boolean displayDateTime) {
        if (displayDateTime) {
            return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date(dateInMill));
        } else {
            itemCal.setTimeInMillis(dateInMill);
            int difference = itemCal.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
            switch (difference) {
                case 1:
                    return mContext.getString(R.string.next_year);
                default:
                    difference = itemCal.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
                    if (difference < 0) {
                        return mContext.getString(R.string.days_overdue) + ": " + (-difference);
                    } else {
                        switch (difference) {
                            case 0:
                                return mContext.getString(R.string.today);
                            case 1:
                                return mContext.getString(R.string.tomorrow);
                            case 2:
                                return mContext.getString(R.string.day_after_tomorrow);
                            case 3:
                                return mContext.getString(R.string.three_days_later);
                            default:
                                difference = itemCal.get(Calendar.WEEK_OF_YEAR) - calendar.get(Calendar.WEEK_OF_YEAR);
                                switch (difference) {
                                    case 0:
                                        return mContext.getString(R.string.this_week);
                                    case 1:
                                        return mContext.getString(R.string.next_week);
                                    default:
                                        difference = itemCal.get(Calendar.MONTH) - calendar.get(Calendar.MONTH);
                                        switch (difference) {
                                            case 0:
                                                return mContext.getString(R.string.this_month);
                                            case 1:
                                                return mContext.getString(R.string.next_month);
                                            default:
                                                return mContext.getString(R.string.not_soon);
                                        }
                                }
                        }
                    }
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
        private final TextView mTextView1;
        private final TextView mTextView2;
        private final Toolbar mToolbar;
        private final SchedulerRecyclerViewAdapter mAdapter;
        private final ImageView mImageView;

        public ViewHolder(View view, SchedulerRecyclerViewAdapter historyRecyclerViewAdapter) {
            super(view);

            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mToolbar.inflateMenu(R.menu.card_menu);
            mToolbar.setOnMenuItemClickListener(this);
            mToolbar.setOnClickListener(this);

            mAdapter = historyRecyclerViewAdapter;

            mTextView1 = (TextView) view.findViewById(R.id.text1);
            mTextView2 = (TextView) view.findViewById(R.id.text2);

            mImageView = (ImageView) view.findViewById(R.id.image);
        }

        public void setItems(String[] items, int sign, boolean overdue, boolean needConfirm, int repeatingType) {
            mToolbar.setTitle(items[0]);
            mToolbar.setSubtitle(items[1]);
            mTextView1.setText(items[2]);
            if (needConfirm) {
                mTextView1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_stat_action_alarm_on, 0);
            } else {
                mTextView1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            if (sign < 0) {
                mTextView2.setTextColor(Color.RED);
            } else {
                mTextView2.setTextColor(Color.GREEN);
            }
            mTextView2.setText(items[3]);
            if (repeatingType == 0) {
                mTextView2.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_stat_av_repeat_one, 0, 0, 0);
            } else {
                mTextView2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            if (overdue) {
                mImageView.setVisibility(View.VISIBLE);
            } else {
                mImageView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.delete_item:
                    Dialogs dialog = new Dialogs();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.DELETE_SCHEDULED_TRANSACTION);
                    bundle.putLong(Dialogs.ID, mAdapter.mTransactions.get(getAdapterPosition()).scheduledTransaction.getScheduledTransactionId());
                    dialog.setArguments(bundle);
                    dialog.show(mAdapter.mFragmentManager, Dialogs.DIALOGS_TAG);
                    return true;
                case R.id.edit_item:
                    Intent i = new Intent(mAdapter.mContext, EditScheduledTransactionActivity.class);
                    Bundle b = new Bundle();
                    b.putLong(SCHEDULED_TRANSACTION_ID, mAdapter.mTransactions.get(getAdapterPosition()).scheduledTransaction.getScheduledTransactionId());
                    i.putExtras(b);
                    mAdapter.mContext.startActivityForResult(i, Constants.EDIT_SCHEDULED_TRANSACTION_REQUEST_CODE);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onClick(View v) {
            Dialogs dialog = new Dialogs();
            Bundle bundle = new Bundle();
            bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.DETAILS);
            bundle.putInt(Dialogs.ID, getAdapterPosition());
            dialog.setArguments(bundle);
            dialog.show(mAdapter.mFragmentManager, Dialogs.DIALOGS_TAG);
        }
    }
}
