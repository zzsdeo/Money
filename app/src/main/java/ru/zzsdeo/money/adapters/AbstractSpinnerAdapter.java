package ru.zzsdeo.money.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import ru.zzsdeo.money.R;
import ru.zzsdeo.money.model.Account;

public abstract class AbstractSpinnerAdapter extends BaseAdapter {

    private ArrayList<Object> objects;
    private Context context;

    public AbstractSpinnerAdapter(Context context, LinkedHashMap collection) {
        objects = new ArrayList<Object>(collection.values());
        this.context = context;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getObjectId(objects.get(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.spinner_item, null, true);
            holder = new ViewHolder();
            holder.textView = (TextView) rowView.findViewById(R.id.textView);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.textView.setText(getTitle(objects.get(position)));
        return rowView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class ViewHolder {
        public TextView textView;
    }

    public abstract CharSequence getTitle(Object object);

    public abstract long getObjectId(Object object);
}
