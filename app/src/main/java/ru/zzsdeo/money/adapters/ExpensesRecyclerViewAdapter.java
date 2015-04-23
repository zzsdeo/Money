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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

    private ArrayList<Category> mCategoryList;
    private CategoryCollection mCategories;
    private MainActivity mContext;
    private HashMap<Long, Float> mExpenses;
    private final Calendar calendar = Calendar.getInstance();


    public ExpensesRecyclerViewAdapter(MainActivity context) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        mCategories = new CategoryCollection(context, CategoryCollection.WITH_BUDGET);
        mCategoryList = new ArrayList<>(mCategories.values());
        mContext = context;
        mExpenses = getExpenses();
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
                Math.round(mExpenses.get(category.getCategoryId())));
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
        mCategories = new CategoryCollection(mContext, CategoryCollection.WITH_BUDGET);
        mCategoryList.clear();
        mCategoryList.addAll(mCategories.values());
        mExpenses = getExpenses();
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

    private HashMap<Long, Float> getExpenses() {
        TransactionCollection transactionCollection;
        HashMap<Long, Float> expenses = new HashMap<>();
        for (Category category : mCategoryList) {
            transactionCollection = new TransactionCollection(mContext, new String[]{
                    TableTransactions.COLUMN_DATE_IN_MILL + " >= " + calendar.getTimeInMillis() +
                    " AND " + TableTransactions.COLUMN_CATEGORY_ID + " = " + category.getCategoryId(),
                    null
            });
            float amount = 0;
            for (Transaction transaction : transactionCollection.values()) {
                amount = amount + transaction.getAmount() + transaction.getCommission();
            }
            expenses.put(category.getCategoryId(), -amount);
        }
        return expenses;
    }
}
