package ru.zzsdeo.money.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.zzsdeo.money.R;
import ru.zzsdeo.money.activities.MainActivity;
import ru.zzsdeo.money.adapters.MainActivityBalanceRecyclerViewAdapter;

public class MainFragment extends Fragment implements IFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();

        // баланс

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_accounts_main);
        recyclerView.setAdapter(((MainActivity)getActivity()).mainActivityBalanceRecyclerViewAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(defaultItemAnimator);

        // требуется подтверждение

        RecyclerView recyclerViewNeedConfirm = (RecyclerView) v.findViewById(R.id.recycler_view_need_confirm);
        recyclerViewNeedConfirm.setAdapter(((MainActivity)getActivity()).needConfirmRecyclerViewAdapter);
        recyclerViewNeedConfirm.setLayoutManager(linearLayoutManager);
        recyclerViewNeedConfirm.setItemAnimator(defaultItemAnimator);

        return v;
    }

    @Override
    public int getIconResId() {
        return android.R.drawable.star_on;
    }
}