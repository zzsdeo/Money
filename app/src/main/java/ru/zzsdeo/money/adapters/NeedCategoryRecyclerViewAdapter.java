package ru.zzsdeo.money.adapters;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.widget.ActionMenuPresenter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.activities.EditTransactionActivity;
import ru.zzsdeo.money.activities.MainActivity;
import ru.zzsdeo.money.db.TableTransactions;
import ru.zzsdeo.money.dialogs.Dialogs;
import ru.zzsdeo.money.fragments.MainFragment;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.Category;
import ru.zzsdeo.money.model.CategoryCollection;
import ru.zzsdeo.money.model.Transaction;
import ru.zzsdeo.money.model.TransactionCollection;

public class NeedCategoryRecyclerViewAdapter extends RecyclerView.Adapter<NeedCategoryRecyclerViewAdapter.ViewHolder>  {

    private ArrayList<Transaction> mTransactions;
    private AccountCollection mAccounts;
    private CategoryCollection mCategories;
    private ArrayList<Category> mCategoryList;
    private MainActivity mContext;
    private TransactionCollection mTransactionCollection;
    private final static String DATE_FORMAT = "dd.MM.yy, HH:mm";

    public NeedCategoryRecyclerViewAdapter(MainActivity context) {
        mTransactionCollection = new TransactionCollection(context, TransactionCollection.WITHOUT_CATEGORY);
        mTransactions = new ArrayList<>(mTransactionCollection.values());
        mAccounts = new AccountCollection(context);
        mCategories = new CategoryCollection(context);
        mCategoryList = new ArrayList<>(mCategories.values());
        mContext = context;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_transaction_history, parent, false);
        return new ViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction transaction = mTransactions.get(position);
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
                new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date(transaction.getDateInMill())),
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
        return mTransactions.get(position).getTransactionId();
    }

    public void refreshDataSet() {
        mTransactionCollection = new TransactionCollection(mContext, TransactionCollection.WITHOUT_CATEGORY);
        mAccounts = new AccountCollection(mContext);
        mCategories = new CategoryCollection(mContext);
        mCategoryList.clear();
        mCategoryList.addAll(mCategories.values());
        mTransactions.clear();
        mTransactions.addAll(mTransactionCollection.values());
        notifyDataSetChanged();

        // Скрываем заголовок

        MainFragment mf = ((MainFragment) mContext.mainPagerAdapter.getItem(1));

        if (getItemCount() == 0) {
            if (mf.isVisible()) mf.needCategory.setVisibility(View.GONE);
        } else {
            if (mf.isVisible()) mf.needCategory.setVisibility(View.VISIBLE);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Toolbar.OnMenuItemClickListener {
        private TextView mTextView1, mTextView2, mTextView3, mTextView4;
        private Toolbar mToolbar;
        private NeedCategoryRecyclerViewAdapter mAdapter;

        public ViewHolder(View view, NeedCategoryRecyclerViewAdapter historyRecyclerViewAdapter) {
            super(view);
            view.setOnClickListener(this);

            mAdapter = historyRecyclerViewAdapter;

            mTextView1 = (TextView) view.findViewById(R.id.recycler_card_history_text1);
            mTextView2 = (TextView) view.findViewById(R.id.recycler_card_history_text2);
            mTextView3 = (TextView) view.findViewById(R.id.recycler_card_history_text3);
            mTextView4 = (TextView) view.findViewById(R.id.recycler_card_history_text4);

            mToolbar = (Toolbar) view.findViewById(R.id.recycler_card_history_toolbar);
            mToolbar.inflateMenu(R.menu.recycler_card_history_toolbar);
            Menu menu = mToolbar.getMenu();
            menu.clear();
            int i = 0;
            for (Category category : mAdapter.mCategoryList) {
                menu.add(Menu.NONE, i, Menu.NONE, category.getName());
                i++;
            }
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
            Transaction transaction = mAdapter.mTransactions.get(getPosition());
            long categoryId = mAdapter.mCategoryList.
                                get(menuItem.getItemId()).
                                getCategoryId();
            transaction.setCategoryId(categoryId);
            if (transaction.getDestinationAccountId() != 0) {
                TransactionCollection linkedTransactions = new TransactionCollection(mAdapter.mContext,
                        new String[] {
                                TableTransactions.COLUMN_LINKED_TRANSACTION_ID + " = " + transaction.getTransactionId(),
                                null
                        });
                Iterator<Transaction> it = linkedTransactions.values().iterator();
                Transaction linkedTransaction = it.next();
                linkedTransaction.setCategoryId(categoryId);
            }
            mAdapter.refreshDataSet();
            mAdapter.mContext.historyRecyclerViewAdapter.refreshDataSet();
            mAdapter.mContext.expensesRecyclerViewAdapter.refreshDataSet();
            return true;
        }
    }
}
