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
import ru.zzsdeo.money.db.TableScheduledTransactions;
import ru.zzsdeo.money.db.TableTransactions;
import ru.zzsdeo.money.dialogs.Dialogs;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.CategoryCollection;
import ru.zzsdeo.money.model.ScheduledTransaction;
import ru.zzsdeo.money.model.ScheduledTransactionCollection;

public class NeedConfirmRecyclerViewAdapter extends RecyclerView.Adapter<NeedConfirmRecyclerViewAdapter.ViewHolder>  {

    public static final String SCHEDULED_TRANSACTION_ID = "scheduled_transaction_id";

    private ArrayList<ScheduledTransaction> mTransactions;
    private AccountCollection mAccounts;
    private CategoryCollection mCategories;
    private MainActivity mContext;
    private ScheduledTransactionCollection mTransactionCollection;
    private FragmentManager mFragmentManager;
    private final static String DATE_FORMAT = "dd.MM.yy, HH:mm";

    public NeedConfirmRecyclerViewAdapter(MainActivity context) {
        mTransactionCollection = new ScheduledTransactionCollection(context, new String[] {
                TableScheduledTransactions.COLUMN_DATE_IN_MILL + " <= " + Calendar.getInstance().getTimeInMillis() +
                        " AND " + TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID + " = " + 0,
                TableScheduledTransactions.COLUMN_DATE_IN_MILL + " DESC, " + TableScheduledTransactions.COLUMN_ID + " DESC"
        });
        mTransactions = new ArrayList<>(mTransactionCollection.values());
        mAccounts = new AccountCollection(context);
        mCategories = new CategoryCollection(context);
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
        ScheduledTransaction transaction = mTransactions.get(position);
        String category, destinationAccount;
        long categoryId = transaction.getCategoryId();
        long destinationAccountId = transaction.getDestinationAccountId();
        if (categoryId == 0) {
            category = "Без категории";
        } else {
            category = mCategories.get(categoryId).getName();
        }
        if (destinationAccountId == 0) {
            destinationAccount = "";
        } else {
            destinationAccount = "Перевод на: " + mAccounts.get(destinationAccountId).getName();
        }
        String[] items = new String[] {
                category,
                transaction.getComment(),
                mAccounts.get(transaction.getAccountId()).getName(),
                destinationAccount,
                new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date(mTransactions.get(position).getDateInMill())),
                String.valueOf(transaction.getAmount())
        };

        int sign = 1;
        boolean disableMenu = false;
        if (transaction.getAmount() < 0) sign = -1;
        if (transaction.getLinkedTransactionId() != 0) disableMenu = true;
        holder.setItems(items, sign, disableMenu);
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    @Override
    public long getItemId(int position) {
        return mTransactions.get(position).getScheduledTransactionId();
    }

    public void refreshDataSet() {
        mTransactionCollection = new ScheduledTransactionCollection(mContext, new String[] {
                TableScheduledTransactions.COLUMN_DATE_IN_MILL + " <= " + Calendar.getInstance().getTimeInMillis() +
                        " AND " + TableScheduledTransactions.COLUMN_REPEATING_TYPE_ID + " = " + 0,
                TableScheduledTransactions.COLUMN_DATE_IN_MILL + " DESC, " + TableScheduledTransactions.COLUMN_ID + " DESC"
        });
        mAccounts = new AccountCollection(mContext);
        mCategories = new CategoryCollection(mContext);
        mTransactions.clear();
        mTransactions.addAll(mTransactionCollection.values());
        notifyDataSetChanged();
    }

    public void removeItem(long id) {
        ScheduledTransaction transaction = mTransactionCollection.get(id);
        if (transaction.getDestinationAccountId() != 0) {
            ScheduledTransactionCollection linkedTransactions = new ScheduledTransactionCollection(mContext,
                    new String[] {
                            TableTransactions.COLUMN_LINKED_TRANSACTION_ID + "=" + transaction.getScheduledTransactionId(),
                            null
                    });
            Iterator<ScheduledTransaction> it = linkedTransactions.values().iterator();
            ScheduledTransaction linkedTransaction = it.next();
            long linkedId = linkedTransaction.getScheduledTransactionId();
            mTransactionCollection.removeScheduledTransaction(linkedId);
        }

        mTransactionCollection.removeScheduledTransaction(id);
        mTransactions.clear();
        mTransactions.addAll(mTransactionCollection.values());
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Toolbar.OnMenuItemClickListener {
        private TextView mTextView1, mTextView2, mTextView3, mTextView4;
        private Toolbar mToolbar;
        private NeedConfirmRecyclerViewAdapter mAdapter;

        public ViewHolder(View view, NeedConfirmRecyclerViewAdapter historyRecyclerViewAdapter) {
            super(view);
            view.setOnClickListener(this);

            mAdapter = historyRecyclerViewAdapter;

            mTextView1 = (TextView) view.findViewById(R.id.recycler_card_history_text1);
            mTextView2 = (TextView) view.findViewById(R.id.recycler_card_history_text2);
            mTextView3 = (TextView) view.findViewById(R.id.recycler_card_history_text3);
            mTextView4 = (TextView) view.findViewById(R.id.recycler_card_history_text4);

            mToolbar = (Toolbar) view.findViewById(R.id.recycler_card_history_toolbar);
            mToolbar.inflateMenu(R.menu.recycler_card_history_toolbar);
            mToolbar.setOnMenuItemClickListener(this);
        }

        public void setItems(String[] items, int sign, boolean disableMenu) {
            mToolbar.setTitle(items[0]);
            mToolbar.setSubtitle(items[1]);
            mTextView1.setText(items[2]);
            mTextView2.setText(items[3]);
            mTextView3.setText(items[4]);
            if (sign < 0) {
                mTextView4.setTextColor(Color.RED);
            } else {
                mTextView4.setTextColor(Color.GREEN);
            }
            mTextView4.setText(items[5]);
            if (disableMenu) {
                for (int i = 0; i < mToolbar.getMenu().size(); i++) {
                    mToolbar.getMenu().getItem(i).setEnabled(false);
                }
            }
        }

        @Override
        public void onClick(View view) {
            //Log.d("my", "onClick " + getPosition() + " " + mItem);

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.delete_item:
                    Dialogs dialog = new Dialogs();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.DELETE_SCHEDULED_TRANSACTION);
                    bundle.putLong(Dialogs.ID, mAdapter.mTransactions.get(getPosition()).getScheduledTransactionId());
                    dialog.setArguments(bundle);
                    dialog.show(mAdapter.mFragmentManager, Dialogs.DIALOGS_TAG);
                    return true;
                /*case R.id.edit_item:
                    Intent i = new Intent(mAdapter.mContext, EditScheduledTransactionActivity.class);
                    Bundle b = new Bundle();
                    b.putLong(SCHEDULED_TRANSACTION_ID, mAdapter.mTransactions.get(getPosition()).getScheduledTransactionId());
                    i.putExtras(b);
                    mAdapter.mContext.startActivityForResult(i, Constants.EDIT_SCHEDULED_TRANSACTION_REQUEST_CODE);
                    return true;*/
                default:
                    return false;
            }
        }
    }
}