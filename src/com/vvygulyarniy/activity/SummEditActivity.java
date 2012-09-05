package com.vvygulyarniy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.vvygulyarniy.R;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 30.07.12
 * Time: 12:16
 */
public class SummEditActivity extends Activity
{
    long id;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summ_edit);

        if(getIntent() != null)
        {
            id = getIntent().getLongExtra(Const.ID_TAG, -1);
            EditText summEdit = (EditText) findViewById(R.id.transactionSummValue);
            String currentSumm = getIntent().getStringExtra(Const.AMOUNT_TAG).replaceAll(",",".");
            summEdit.setText(String.format("%.2f", new Double(currentSumm)));
        }

    }

    public void btnOkClick(View view)
    {
        Intent returnValue = new Intent();
        EditText summEdit = (EditText) findViewById(R.id.transactionSummValue);
        if(summEdit.getText().toString().length() < 1)
            return;
        String summ = summEdit.getText().toString();
        returnValue.putExtra("summ", summ);
        returnValue.putExtra("id", id);
        setResult(RESULT_OK, returnValue);
        finish();
    }
}