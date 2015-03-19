package ru.zzsdeo.money.activities;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.List;

import ru.zzsdeo.money.R;
import ru.zzsdeo.money.adapters.AccountsSpinnerAdapter;
import ru.zzsdeo.money.model.Account;
import ru.zzsdeo.money.model.AccountCollection;

public class AddTransactionActivity extends ActionBarActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EditText amount, commission, comment;
    private Spinner destinationAccountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        AccountCollection accountCollection = new AccountCollection(this);

        Spinner accountId = (Spinner) findViewById(R.id.spinner1);
        CheckBox isDefaultAccount = (CheckBox) findViewById(R.id.checkBox1);
        Spinner dateTime = (Spinner) findViewById(R.id.spinner2);
        amount = (EditText) findViewById(R.id.amount);
        commission = (EditText) findViewById(R.id.commission);
        comment = (EditText) findViewById(R.id.comment);
        CheckBox isTransfer = (CheckBox) findViewById(R.id.checkBox2);
        destinationAccountId = (Spinner) findViewById(R.id.spinner3);
        Spinner categoryId = (Spinner) findViewById(R.id.spinner4);
        Button addBtn = (Button) findViewById(R.id.addBtn);

        AccountsSpinnerAdapter accountsSpinnerAdapter = new AccountsSpinnerAdapter(this, accountCollection);
        accountId.setAdapter(accountsSpinnerAdapter);

        destinationAccountId.setVisibility(View.GONE);
        isTransfer.setOnCheckedChangeListener(this);
        addBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBtn:
                /*String nameString = name.getText().toString();
                String cardNumberString = cardNumber.getText().toString();
                String balanceString = balance.getText().toString();
                float balanceFloat;
                if (nameString.isEmpty()) {
                    Toast.makeText(this, "Необходимо ввести название", Toast.LENGTH_LONG).show();
                    return;
                }
                if (balanceString.isEmpty()) {
                    balanceFloat = 0;
                } else {
                    balanceFloat = Float.parseFloat(balanceString);
                }
                new AccountCollection(this).addAccount(nameString, cardNumberString, balanceFloat);
                name.setText("");
                cardNumber.setText("");
                balance.setText("");
                Toast.makeText(this, "Счет успешно добавлен", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);*/
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBox1:
                break;
            case R.id.checkBox2:
                if (isChecked) {
                    destinationAccountId.setVisibility(View.VISIBLE);
                } else {
                    destinationAccountId.setVisibility(View.GONE);
                }
                break;
        }
    }
}