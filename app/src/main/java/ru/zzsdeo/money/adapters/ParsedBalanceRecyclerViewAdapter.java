package ru.zzsdeo.money.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.model.Account;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.Category;

public class ParsedBalanceRecyclerViewAdapter extends RecyclerView.Adapter<ParsedBalanceRecyclerViewAdapter.ViewHolder>  {

    private AccountCollection mAccountCollection;
    private SharedPreferences mSharedPreferences;
    private ArrayList<String[]> mItems;
    private Context mContext;

    public ParsedBalanceRecyclerViewAdapter(Context context) {
        mItems = new ArrayList<>();
        mAccountCollection = new AccountCollection(context);
        mSharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mContext = context;
        getData();
    }

    public void refreshDataSet() {
        mAccountCollection = new AccountCollection(mContext);
        mItems.clear();
        getData();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_parsed_balance, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] data = mItems.get(position);
        String[] items = new String[] {
                data[0],
                data[1]
        };
        holder.setItems(items);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView1, mTextView2;
        private ImageButton mDelete;

        public ViewHolder(View view) {
            super(view);
            mTextView1 = (TextView) view.findViewById(R.id.text1);
            mTextView2 = (TextView) view.findViewById(R.id.text2);
            mDelete = (ImageButton) view.findViewById(R.id.btn_clear);
            mDelete.setOnClickListener(this);
        }

        public void setItems(String[] items) {
            mTextView1.setText(items[0]);
            mTextView2.setText(items[1]);
        }

        @Override
        public void onClick(View view) {
            Log.d("my", "onClick " + getPosition() + " " );
        }
    }

    private void getData() {
        for (Account account : mAccountCollection.values()) {
            float balance = mSharedPreferences.getFloat(account.getCardNumber(), 0);
            if (balance != 0) {
                float diff = account.getBalance() - balance;
                String balanceString = String.valueOf(balance) + " (" + String.valueOf(diff) + ")";
                mItems.add(new String[] {account.getName(), balanceString});
            }
        }
    }

}
