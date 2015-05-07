package ru.zzsdeo.money.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import ru.zzsdeo.money.MyLinearLayoutManager;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.activities.MainActivity;
import ru.zzsdeo.money.adapters.MainActivityBalanceRecyclerViewAdapter;

public class MainFragment extends Fragment implements IFragment {

    public TextView needConfirm, needCategory, parsedBalance;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);

        ObservableScrollView observableScrollView = (ObservableScrollView) v.findViewById(R.id.observable_scroll_view);
        observableScrollView.setScrollViewCallbacks((ObservableScrollViewCallbacks) getActivity());

        // несоответствие баланса

        parsedBalance = (TextView) v.findViewById(R.id.parsed_balance_title);

        RecyclerView recyclerViewParsedBalance = (RecyclerView) v.findViewById(R.id.recycler_view_parsed_balance);
        recyclerViewParsedBalance.setAdapter(((MainActivity)getActivity()).parsedBalanceRecyclerViewAdapter);
        recyclerViewParsedBalance.setLayoutManager(new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerViewParsedBalance.setItemAnimator(new DefaultItemAnimator());

        // баланс

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_accounts_main);
        recyclerView.setAdapter(((MainActivity)getActivity()).mainActivityBalanceRecyclerViewAdapter);
        recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // требуется подтверждение

        needConfirm = (TextView) v.findViewById(R.id.need_confirm);

        RecyclerView recyclerViewNeedConfirm = (RecyclerView) v.findViewById(R.id.recycler_view_need_confirm);
        recyclerViewNeedConfirm.setAdapter(((MainActivity)getActivity()).needConfirmRecyclerViewAdapter);
        recyclerViewNeedConfirm.setLayoutManager(new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerViewNeedConfirm.setItemAnimator(new DefaultItemAnimator());

        // требуется категория

        needCategory = (TextView) v.findViewById(R.id.without_category);

        RecyclerView recyclerViewNeedCategory = (RecyclerView) v.findViewById(R.id.recycler_view_need_category);
        recyclerViewNeedCategory.setAdapter(((MainActivity)getActivity()).needCategoryRecyclerViewAdapter);
        recyclerViewNeedCategory.setLayoutManager(new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerViewNeedCategory.setItemAnimator(new DefaultItemAnimator());

        // расходы

        RecyclerView recyclerViewExpenses = (RecyclerView) v.findViewById(R.id.recycler_view_expenses);
        recyclerViewExpenses.setAdapter(((MainActivity)getActivity()).expensesRecyclerViewAdapter);
        recyclerViewExpenses.setLayoutManager(new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerViewExpenses.setItemAnimator(new DefaultItemAnimator());

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (((MainActivity)getActivity()).needConfirmRecyclerViewAdapter.getItemCount() == 0) {
            needConfirm.setVisibility(View.GONE);
        } else {
            needConfirm.setVisibility(View.VISIBLE);
        }

        if (((MainActivity)getActivity()).needCategoryRecyclerViewAdapter.getItemCount() == 0) {
            needCategory.setVisibility(View.GONE);
        } else {
            needCategory.setVisibility(View.VISIBLE);
        }

        if (((MainActivity)getActivity()).parsedBalanceRecyclerViewAdapter.getItemCount() == 0) {
            parsedBalance.setVisibility(View.GONE);
        } else {
            parsedBalance.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getIconResId() {
        return R.mipmap.ic_action_action_account_balance_wallet;
    }
}