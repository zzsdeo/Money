package ru.zzsdeo.money.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.zzsdeo.money.R;
import ru.zzsdeo.money.model.AccountCollection;
import ru.zzsdeo.money.model.CategoryCollection;

public class AddCategoryActivity extends ActionBarActivity implements View.OnClickListener{

    private EditText name, budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        Button addBtn = (Button) findViewById(R.id.addBtn);
        name = (EditText) findViewById(R.id.cardName);
        budget = (EditText) findViewById(R.id.startBalance);

        addBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBtn:
                String nameString = name.getText().toString();
                String budgetString = budget.getText().toString();
                float budgetFloat;
                if (nameString.isEmpty()) {
                    Toast.makeText(this, "Необходимо ввести название", Toast.LENGTH_LONG).show();
                    return;
                }
                if (budgetString.isEmpty()) {
                    budgetFloat = 0;
                } else {
                    budgetFloat = Float.parseFloat(budgetString);
                }
                new CategoryCollection(this).addCategory(nameString, budgetFloat);
                name.setText("");
                budget.setText("");
                Toast.makeText(this, "Категория успешно добавлена", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                break;
        }
    }
}