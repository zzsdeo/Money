package ru.zzsdeo.money.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.zzsdeo.money.model.Account;
import ru.zzsdeo.money.model.AccountCollection;

public class AccountsSpinnerAdapter extends BaseAdapter {

    private ArrayList<Account> accounts;
    private Context context;

    public AccountsSpinnerAdapter(Context context, AccountCollection accountCollection) {
        accounts = new ArrayList<>(accountCollection.values());
        this.context = context;
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Object getItem(int position) {
        return accounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return accounts.get(position).getAccountId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(android.R.layout.simple_spinner_item, null, true);
            holder = new ViewHolder();
            holder.textView = (TextView) rowView.findViewById(android.R.id.text1);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.textView.setText(accounts.get(position).getName());
        return rowView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class ViewHolder {
        public TextView textView;
    }
}
