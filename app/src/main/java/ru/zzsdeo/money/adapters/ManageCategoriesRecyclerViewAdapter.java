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

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.activities.EditCategoryActivity;
import ru.zzsdeo.money.activities.ManageCategoriesActivity;
import ru.zzsdeo.money.dialogs.Dialogs;
import ru.zzsdeo.money.model.Account;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.Category;
import ru.zzsdeo.money.model.CategoryCollection;

public class ManageCategoriesRecyclerViewAdapter extends RecyclerView.Adapter<ManageCategoriesRecyclerViewAdapter.ViewHolder>  {

    private ArrayList<Category> mCategories;
    private CategoryCollection mCategoryCollection;
    private ManageCategoriesActivity mContext;
    private FragmentManager mFragmentManager;
    public static final String CATEGORY_ID = "category_id";

    public ManageCategoriesRecyclerViewAdapter(ManageCategoriesActivity context) {
        mCategoryCollection = new CategoryCollection(context);
        mCategories = new ArrayList<>(mCategoryCollection.values());
        mContext = context;
        mFragmentManager = context.getFragmentManager();
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_account_category, parent, false);
        return new ViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = mCategories.get(position);
        String [] items = new String[] {
                category.getName(),
                String.valueOf(category.getBudget())
        };
        holder.setItems(items);
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    @Override
    public long getItemId(int position) {
        return mCategories.get(position).getCategoryId();
    }

    public void refreshDataSet() {
        mCategoryCollection = new CategoryCollection(mContext);
        mCategories.clear();
        mCategories.addAll(mCategoryCollection.values());
        notifyDataSetChanged();
    }

    public void removeItem(long id) {
        mCategoryCollection.removeCategory(id);
        mCategories.clear();
        mCategories.addAll(mCategoryCollection.values());
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Toolbar.OnMenuItemClickListener {
        private TextView mTextView;
        private Toolbar mToolbar;
        private ManageCategoriesRecyclerViewAdapter mAdapter;

        public ViewHolder(final View view, final ManageCategoriesRecyclerViewAdapter manageCategoriesRecyclerViewAdapter) {
            super(view);
            view.setOnClickListener(this);

            mAdapter = manageCategoriesRecyclerViewAdapter;

            mTextView = (TextView) view.findViewById(R.id.recycler_card_account_text);

            mToolbar = (Toolbar) view.findViewById(R.id.card_toolbar_account);
            mToolbar.inflateMenu(R.menu.recucler_card_account_toolbar);
            mToolbar.setOnMenuItemClickListener(this);
        }

        public void setItems(String[] items) {
            mToolbar.setTitle(items[0]);
            mTextView.setText(items[1]);
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
                    bundle.putInt(Dialogs.DIALOG_TYPE, Dialogs.DELETE_CATEGORY);
                    bundle.putLong(Dialogs.ID, mAdapter.mCategories.get(getPosition()).getCategoryId());
                    dialog.setArguments(bundle);
                    dialog.show(mAdapter.mFragmentManager, Dialogs.DIALOGS_TAG);
                    return true;
                case R.id.edit_account:
                    Intent i = new Intent(mAdapter.mContext, EditCategoryActivity.class);
                    Bundle b = new Bundle();
                    b.putLong(CATEGORY_ID, mAdapter.mCategories.get(getPosition()).getCategoryId());
                    i.putExtras(b);
                    mAdapter.mContext.startActivityForResult(i, Constants.EDIT_CATEGORY_REQUEST_CODE);
                    return true;
                default:
                    return false;
            }
        }
    }
}
