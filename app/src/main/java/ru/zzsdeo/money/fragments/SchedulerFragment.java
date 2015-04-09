package ru.zzsdeo.money.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.activities.AddScheduledTransactionActivity;
import ru.zzsdeo.money.activities.AddTransactionActivity;
import ru.zzsdeo.money.activities.MainActivity;

public class SchedulerFragment extends Fragment implements IFragment, View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_history);
        recyclerView.setAdapter(((MainActivity)getActivity()).schedulerRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(this);

        return v;
    }

    @Override
    public int getIconResId() {
        return android.R.drawable.ic_menu_camera;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                startActivityForResult(new Intent(getActivity(), AddScheduledTransactionActivity.class), Constants.ADD_SCHEDULED_TRANSACTION_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.ADD_SCHEDULED_TRANSACTION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    ((MainActivity)getActivity()).schedulerRecyclerViewAdapter.refreshDataSet();
                }
                break;
        }
    }
}