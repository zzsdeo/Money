package ru.zzsdeo.money.activities;

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

import ru.zzsdeo.money.R;
import ru.zzsdeo.money.adapters.ManageAccountsRecyclerViewAdapter;
import ru.zzsdeo.money.model.AccountCollection;

public class AddAccountActivity extends ActionBarActivity implements View.OnClickListener{

    private EditText name, cardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        Button addBtn = (Button) findViewById(R.id.addBtn);
        name = (EditText) findViewById(R.id.cardName);
        cardNumber = (EditText) findViewById(R.id.cardNumber);

        addBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBtn:
                String nameString = name.getText().toString();
                String cardNumberString = cardNumber.getText().toString();
                if (nameString.isEmpty()) {
                    Toast.makeText(this, "Необходимо ввести название", Toast.LENGTH_LONG).show();
                    return;
                }
                new AccountCollection(this).addAccount(nameString, cardNumberString, 0);
                name.setText("");
                cardNumber.setText("");
                Toast.makeText(this, "Счет успешно добавлен", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                break;
        }
    }
}