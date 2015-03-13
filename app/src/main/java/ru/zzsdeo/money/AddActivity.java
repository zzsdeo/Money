package ru.zzsdeo.money;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ru.zzsdeo.money.adapters.ManageAccountsRecyclerViewAdapter;
import ru.zzsdeo.money.model.AccountCollection;

public class AddActivity extends ActionBarActivity implements View.OnClickListener{

    private EditText name, cardNumber, balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        Bundle bundle = getIntent().getExtras();

        Button addBtn = (Button) findViewById(R.id.addBtn);
        name = (EditText) findViewById(R.id.cardName);
        cardNumber = (EditText) findViewById(R.id.cardNumber);
        balance = (EditText) findViewById(R.id.startBalance);

        if (bundle != null) {
            name.setText(bundle.getString(ManageAccountsRecyclerViewAdapter.ACCOUNT_NAME));
            cardNumber.setText(bundle.getString(ManageAccountsRecyclerViewAdapter.ACCOUNT_CARD_NUMBER));
            balance.setText(bundle.getString(ManageAccountsRecyclerViewAdapter.ACCOUNT_BALANCE));
        }

        addBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBtn:
                String nameString = name.getText().toString();
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
                setResult(RESULT_OK);
                break;
        }
    }
}