package ru.zzsdeo.money.adapters;

import android.content.Context;
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
import java.util.Date;
import java.util.Locale;

import ru.zzsdeo.money.AddActivity;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.model.Account;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.Transaction;
import ru.zzsdeo.money.model.TransactionCollection;

public class ManageAccountsRecyclerViewAdapter extends RecyclerView.Adapter<ManageAccountsRecyclerViewAdapter.ViewHolder>  {

    private ArrayList<Account> mAccounts;
    private AccountCollection mAccountCollection;
    private Context mContext;
    public static final String ACCOUNT_NAME = "account_name";
    public static final String ACCOUNT_CARD_NUMBER = "account_card_number";
    public static final String ACCOUNT_BALANCE = "account_balance";

    public ManageAccountsRecyclerViewAdapter(Context context, AccountCollection accounts) {
        mAccounts = new ArrayList<>(accounts.values());
        mAccountCollection = accounts;
        mContext = context;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_account, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Account account = mAccounts.get(position);
        String [] items = new String[] {
                account.getName(),
                account.getCardNumber(),
                String.valueOf(account.getBalance())
        };
        holder.setItems(items);
    }

    @Override
    public int getItemCount() {
        return mAccounts.size();
    }

    @Override
    public long getItemId(int position) {
        return mAccounts.get(position).getAccountId();
    }

    public void refreshDataSet() {
        mAccountCollection = new AccountCollection(mContext);
        mAccounts.clear();
        mAccounts.addAll(mAccountCollection.values());
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView;
        private Toolbar mToolbar;

        public ViewHolder(final View view) {
            super(view);
            view.setOnClickListener(this);
            mTextView = (TextView) view.findViewById(R.id.recycler_card_account_text);

            mToolbar = (Toolbar) view.findViewById(R.id.card_toolbar_account);
            mToolbar.inflateMenu(R.menu.recucler_card_account_menu);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.delete_account:
                            mAccountCollection.removeAccount(mAccounts.get(getPosition()).getAccountId());
                            mAccounts.remove(getPosition());
                            notifyItemRemoved(getPosition());
                            return true;
                        case R.id.edit_account:
                            Intent i = new Intent(mContext, AddActivity.class);
                            Bundle b = new Bundle();
                            b.putString(ACCOUNT_NAME, mAccounts.get(getPosition()).getName());
                            b.putString(ACCOUNT_CARD_NUMBER, String.valueOf(mAccounts.get(getPosition()).getCardNumber()));
                            b.putString(ACCOUNT_BALANCE, String.valueOf(mAccounts.get(getPosition()).getBalance()));
                            i.putExtras(b);
                            mContext.startActivity(i);
                            return true;
                        default:
                            return false;
                    }
                }
            });
        }

        public void setItems(String[] items) {
            mToolbar.setTitle(items[0]);
            mToolbar.setSubtitle(items[1]);
            mTextView.setText(items[2]);
        }

        @Override
        public void onClick(View view) {
            //Log.d("my", "onClick " + getPosition() + " " + mItem);

        }
    }

}
