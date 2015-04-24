package ru.zzsdeo.money.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.melnykov.fab.FloatingActionButton;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.activities.AddTransactionActivity;
import ru.zzsdeo.money.activities.MainActivity;
import ru.zzsdeo.money.model.AccountCollection;

public class HistoryFragment extends Fragment implements IFragment, View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        ObservableRecyclerView recyclerView = (ObservableRecyclerView) v.findViewById(R.id.recycler_view_history);
        recyclerView.setAdapter(((MainActivity)getActivity()).historyRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setScrollViewCallbacks((ObservableScrollViewCallbacks) getActivity());

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(this);

        return v;
    }

    @Override
    public int getIconResId() {
        return android.R.drawable.ic_menu_agenda;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                startActivityForResult(new Intent(getActivity(), AddTransactionActivity.class), Constants.ADD_TRANSACTION_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.ADD_TRANSACTION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    ((MainActivity)getActivity()).historyRecyclerViewAdapter.refreshDataSet();
                    ((MainActivity)getActivity()).mainActivityBalanceRecyclerViewAdapter.refreshDataSet();
                    ((MainActivity)getActivity()).needCategoryRecyclerViewAdapter.refreshDataSet();
                    ((MainActivity)getActivity()).expensesRecyclerViewAdapter.refreshDataSet();
                }
                break;
        }
    }
}