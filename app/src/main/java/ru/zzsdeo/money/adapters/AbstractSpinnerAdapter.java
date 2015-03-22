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
    private String[] additionalItems;
    private LinkedHashMap collection;

    public AbstractSpinnerAdapter(Context context, LinkedHashMap collection) {
        objects = new ArrayList<Object>(collection.values());
        this.context = context;
        this.collection = collection;
    }

    public AbstractSpinnerAdapter(Context context, LinkedHashMap collection, String[] additionalItems) {
        objects = new ArrayList<Object>(collection.values());
        this.context = context;
        this.additionalItems = additionalItems;
        this.collection = collection;
    }

    @Override
    public int getCount() {
        if (additionalItems != null) {
            return objects.size() + additionalItems.length;
        } else {
            return objects.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (additionalItems != null) {
            if (position >= additionalItems.length) {
                return objects.get(position - additionalItems.length);
            } else {
                return additionalItems[position];
            }
        } else {
            return objects.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (objects.isEmpty()) {
            return 0;
        } else if (additionalItems != null) {
            if (position >= additionalItems.length) {
                return getObjectId(objects.get(position - additionalItems.length));
            } else {
                return 0;
            }
        } else {
            return getObjectId(objects.get(position));
        }
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
        if (additionalItems != null) {
            if (position >= additionalItems.length) {
                holder.textView.setText(getTitle(objects.get(position - additionalItems.length)));
            } else {
                holder.textView.setText(additionalItems[position]);
            }
        } else {
            holder.textView.setText(getTitle(objects.get(position)));
        }
        return rowView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class ViewHolder {
        public TextView textView;
    }

    public ArrayList<Object> getAllItemsIds() {
        ArrayList<Object> ids = new ArrayList<>();
        if (additionalItems != null) {
            int i = 0;
            do {
                ids.add(0l);
                i++;
            } while (i < additionalItems.length);
            ids.addAll(collection.keySet());
        } else {
            ids.addAll(collection.keySet());
        }
        return ids;
    }

    public void removeItem(int position) {
        objects = new ArrayList<Object>(collection.values());
        objects.remove(position);
        notifyDataSetChanged();
    }

    public abstract CharSequence getTitle(Object object);

    public abstract long getObjectId(Object object);
}
