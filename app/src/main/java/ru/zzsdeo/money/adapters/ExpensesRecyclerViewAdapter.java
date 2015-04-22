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
import android.widget.ProgressBar;
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
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.Category;
import ru.zzsdeo.money.model.CategoryCollection;
import ru.zzsdeo.money.model.Transaction;
import ru.zzsdeo.money.model.TransactionCollection;

public class ExpensesRecyclerViewAdapter extends RecyclerView.Adapter<ExpensesRecyclerViewAdapter.ViewHolder>  {

    private ArrayList<Transaction> mTransactions;
    private ArrayList<Category> mCategoryList;
    private AccountCollection mAccounts;
    private CategoryCollection mCategories;
    private MainActivity mContext;
    private TransactionCollection mTransactionCollection;

    public ExpensesRecyclerViewAdapter(MainActivity context) {
        mTransactionCollection = new TransactionCollection(context, TransactionCollection.SORTED_BY_DATE_DESC);
        mTransactions = new ArrayList<>(mTransactionCollection.values());
        mAccounts = new AccountCollection(context);
        mCategories = new CategoryCollection(context, CategoryCollection.WITH_BUDGET);
        mCategoryList = new ArrayList<>(mCategories.values());
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_expenses, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = mCategoryList.get(position);
        holder.setItems(category.getName(),
                Math.round(category.getBudget()),
                50);
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    @Override
    public long getItemId(int position) {
        return mCategoryList.get(position).getCategoryId();
    }

    public void refreshDataSet() {
        mTransactionCollection = new TransactionCollection(mContext, TransactionCollection.SORTED_BY_DATE_DESC);
        mAccounts = new AccountCollection(mContext);
        mCategories = new CategoryCollection(mContext, CategoryCollection.WITH_BUDGET);
        mCategoryList.clear();
        mCategoryList.addAll(mCategories.values());
        mTransactions.clear();
        mTransactions.addAll(mTransactionCollection.values());
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView;
        private ProgressBar mProgressBar;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            mTextView = (TextView) view.findViewById(R.id.text);

            mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        }

        public void setItems(String name, int max, int progress) {
            mTextView.setText(name);
            mProgressBar.setMax(max);
            mProgressBar.setProgress(progress);
        }

        @Override
        public void onClick(View view) {
            //Log.d("my", "onClick " + getPosition() + " " + mItem);

        }
    }
}
