package ru.zzsdeo.money.adapters;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


import ru.zzsdeo.money.AddAccountActivity;
import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.Dialogs;
import ru.zzsdeo.money.EditAccountActivity;
import ru.zzsdeo.money.ManageAccountsActivity;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.model.Account;
import ru.zzsdeo.money.model.AccountCollection;

public class ManageAccountsRecyclerViewAdapter extends RecyclerView.Adapter<ManageAccountsRecyclerViewAdapter.ViewHolder>  {

    private ArrayList<Account> mAccounts;
    private AccountCollection mAccountCollection;
    private ManageAccountsActivity mContext;
    private FragmentManager mFragmentManager;
    public static final String ACCOUNT_ID = "account_id";

    public ManageAccountsRecyclerViewAdapter(ManageAccountsActivity context) {
        mAccountCollection = new AccountCollection(context);
        mAccounts = new ArrayList<>(mAccountCollection.values());
        mContext = context;
        mFragmentManager = context.getFragmentManager();
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_account, parent, false);
        return new ViewHolder(v, this);
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

    public void removeItem(long id) {
        mAccountCollection.removeAccount(id);
        mAccounts.clear();
        mAccounts.addAll(mAccountCollection.values());
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Toolbar.OnMenuItemClickListener {
        private TextView mTextView;
        private Toolbar mToolbar;
        private ManageAccountsRecyclerViewAdapter mAdapter;

        public ViewHolder(final View view, final ManageAccountsRecyclerViewAdapter manageAccountsRecyclerViewAdapter) {
            super(view);
            view.setOnClickListener(this);

            mAdapter = manageAccountsRecyclerViewAdapter;

            mTextView = (TextView) view.findViewById(R.id.recycler_card_account_text);

            mToolbar = (Toolbar) view.findViewById(R.id.card_toolbar_account);
            mToolbar.inflateMenu(R.menu.recucler_card_account_menu);
            mToolbar.setOnMenuItemClickListener(this);
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

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_account:
                    Dialogs dialog = new Dialogs();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.DELETE_ACCOUNT);
                    bundle.putLong(Dialogs.ID, mAdapter.mAccounts.get(getPosition()).getAccountId());
                    dialog.setArguments(bundle);
                    dialog.show(mAdapter.mFragmentManager, Dialogs.DIALOGS_TAG);
                    return true;
                case R.id.edit_account:
                    Intent i = new Intent(mAdapter.mContext, EditAccountActivity.class);
                    Bundle b = new Bundle();
                    b.putLong(ACCOUNT_ID, mAdapter.mAccounts.get(getPosition()).getAccountId());
                    i.putExtras(b);
                    mAdapter.mContext.startActivityForResult(i, Constants.EDIT_ACCOUNT_REQUEST_CODE);
                    return true;
                default:
                    return false;
            }
        }
    }
}