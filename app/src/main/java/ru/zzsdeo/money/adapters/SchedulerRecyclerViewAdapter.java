package ru.zzsdeo.money.adapters;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

    public SchedulerRecyclerViewAdapter(MainActivity context, ArrayList<ScheduledTransactionCollection.TransactionsHolder> mTransactions) {
        this.mTransactions = mTransactions;
        mContext = context;
        mFragmentManager = context.getFragmentManager();
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

        String[] items = new String[] {
                transaction.getComment(),
                String.valueOf(transaction.getAmount()),
                new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date(dateTime)),
                String.valueOf(balance)
        };

        int sign = 1;
        if (balance < 0) sign = -1;

        long nowInMill = Calendar.getInstance().getTimeInMillis();
        boolean overdue = false;
        if (transaction.getNeedApprove() && transaction.getRepeatingTypeId() == 0 && dateTime <= nowInMill) overdue = true;

        holder.setItems(items, sign, overdue);
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

        public void setItems(String[] items, int sign, boolean overdue) {
            mToolbar.setTitle(items[0]);
            mToolbar.setSubtitle(items[1]);
            mTextView1.setText(items[2]);
            if (sign < 0) {
                mTextView2.setTextColor(Color.RED);
            } else {
                mTextView2.setTextColor(Color.GREEN);
            }
            mTextView2.setText(items[3]);
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

        @Override
        public void onClick(View v) {
            Dialogs dialog = new Dialogs();
            Bundle bundle = new Bundle();
            bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.DETAILS);
            bundle.putInt(Dialogs.ID, getPosition());
            dialog.setArguments(bundle);
            dialog.show(mAdapter.mFragmentManager, Dialogs.DIALOGS_TAG);
        }
    }
}
