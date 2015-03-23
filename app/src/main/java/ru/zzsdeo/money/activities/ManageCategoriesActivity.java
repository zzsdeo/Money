package ru.zzsdeo.money.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.melnykov.fab.FloatingActionButton;

import ru.zzsdeo.money.Constants;
import ru.zzsdeo.money.R;
import ru.zzsdeo.money.adapters.ManageAccountsRecyclerViewAdapter;
import ru.zzsdeo.money.adapters.ManageCategoriesRecyclerViewAdapter;
import ru.zzsdeo.money.dialogs.Dialogs;

public class ManageCategoriesActivity extends ActionBarActivity implements Dialogs.DialogListener, View.OnClickListener {

    private ManageCategoriesRecyclerViewAdapter manageCategoriesRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_fab);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_accounts);
        manageCategoriesRecyclerViewAdapter = new ManageCategoriesRecyclerViewAdapter(this);
        recyclerView.setAdapter(manageCategoriesRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.ADD_CATEGORY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    manageCategoriesRecyclerViewAdapter.refreshDataSet();
                    setResult(RESULT_OK);
                }
                break;
            case Constants.EDIT_CATEGORY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    manageCategoriesRecyclerViewAdapter.refreshDataSet();
                    setResult(RESULT_OK);
                }
                break;
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogType) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogType, long id) {
        switch (dialogType) {
            case Dialogs.DELETE_CATEGORY:
                manageCategoriesRecyclerViewAdapter.removeItem(id);
                setResult(RESULT_OK);
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int dialogType) {
        switch (dialogType) {
            case Dialogs.DELETE_CATEGORY:
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int dialogType, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(TimePicker view, int dialogType, int hourOfDay, int minute) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                startActivityForResult(new Intent(this, AddCategoryActivity.class), Constants.ADD_CATEGORY_REQUEST_CODE);
                break;
        }
    }
}