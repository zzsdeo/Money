package ru.zzsdeo.money.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.zzsdeo.money.R;
import ru.zzsdeo.money.adapters.ManageAccountsRecyclerViewAdapter;
import ru.zzsdeo.money.model.Account;
import ru.zzsdeo.money.model.AccountCollection;

public class EditAccountActivity extends ActionBarActivity implements View.OnClickListener{

    private EditText name, cardNumber;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) finish();

        Button addBtn = (Button) findViewById(R.id.addBtn);
        name = (EditText) findViewById(R.id.cardName);
        cardNumber = (EditText) findViewById(R.id.cardNumber);

        assert bundle != null;
        account = new AccountCollection(this).get(bundle.getLong(ManageAccountsRecyclerViewAdapter.ACCOUNT_ID));
        name.setText(account.getName());
        cardNumber.setText(account.getCardNumber());

        addBtn.setText("Сохранить");
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
                account.setName(nameString);
                account.setCardNumber(cardNumberString);
                Toast.makeText(this, "Сохранено", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                break;
        }
    }
}