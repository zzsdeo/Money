package ru.zzsdeo.money.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import ru.zzsdeo.money.R;
import ru.zzsdeo.money.model.Account;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.Transaction;
import ru.zzsdeo.money.model.TransactionCollection;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {

    private ArrayList<Transaction> mTransactions;
    private AccountCollection mAccounts;
    private final static String DATE_FORMAT = "dd.MM.yy, HH:mm";

    public RecyclerViewAdapter(TransactionCollection transactions, AccountCollection accounts) {
        mTransactions = new ArrayList<>(transactions.values());
        mAccounts = accounts;
    }

    /*public void add(String item) {
        mDataset.add(item);
        notifyItemInserted(mDataset.size());
    }

    public void add(String item, int position) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(String item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction transaction = mTransactions.get(position);
        String [] items = new String[] {
                transaction.getComment(),
                mAccounts.get(transaction.getAccountId()).getName(),
                new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date(transaction.getDateInMill())),
                String.valueOf(transaction.getAmount())
        };

        int sign = 1;
        if (transaction.getAmount() < 0) sign = -1;
        holder.setItems(items, sign);
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView1, mTextView2;
        private Toolbar mToolbar;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mTextView1 = (TextView) view.findViewById(R.id.recycler_card_text1);
            mTextView2 = (TextView) view.findViewById(R.id.recycler_card_text2);

            mToolbar = (Toolbar) view.findViewById(R.id.card_toolbar);
            mToolbar.inflateMenu(R.menu.card_menu);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    return true;
                }
            });
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
        public void onClick(View view) {
            //Log.d("my", "onClick " + getPosition() + " " + mItem);

        }
    }

}
