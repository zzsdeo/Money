package ru.zzsdeo.money.adapters;

import android.content.Context;
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
import java.util.Date;
import java.util.Locale;

import ru.zzsdeo.money.R;
import ru.zzsdeo.money.model.Account;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.Transaction;
import ru.zzsdeo.money.model.TransactionCollection;

public class MainActivityBalanceRecyclerViewAdapter extends RecyclerView.Adapter<MainActivityBalanceRecyclerViewAdapter.ViewHolder>  {

    private ArrayList<Account> mAccounts;
    private AccountCollection mAccountCollection;
    private Context mContext;

    public MainActivityBalanceRecyclerViewAdapter(Context context) {
        mAccountCollection = new AccountCollection(context);
        mAccounts = new ArrayList<>(mAccountCollection.values());
        mContext = context;
    }

    public void refreshDataSet() {
        mAccountCollection = new AccountCollection(mContext);
        mAccounts.clear();
        mAccounts.addAll(mAccountCollection.values());
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_account_main, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Account account = mAccounts.get(position);
        String [] items = new String[] {
                account.getName(),
                String.valueOf(account.getBalance())
        };
        holder.setItems(items);
    }

    @Override
    public int getItemCount() {
        return mAccounts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView1, mTextView2;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mTextView1 = (TextView) view.findViewById(R.id.recycler_card_account_main_text1);
            mTextView2 = (TextView) view.findViewById(R.id.recycler_card_account_main_text2);
        }

        public void setItems(String[] items) {
            mTextView1.setText(items[0]);
            mTextView2.setText(items[1]);
        }

        @Override
        public void onClick(View view) {
            //Log.d("my", "onClick " + getPosition() + " " + mItem);

        }
    }

}
