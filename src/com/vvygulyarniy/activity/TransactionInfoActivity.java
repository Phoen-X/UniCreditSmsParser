package com.vvygulyarniy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.vvygulyarniy.R;
import com.vvygulyarniy.db.DBhelper;
import com.vvygulyarniy.to.CreditTO;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 23.07.12
 * Time: 14:26
 */
public class TransactionInfoActivity extends Activity implements View.OnClickListener {
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //this.setTitle(null);
        setContentView(R.layout.transaction_info);

        Intent intent = getIntent();

        if(intent == null)
        {
            finishActivity(-1);
        }
        else
        {
            id = intent.getLongExtra("ID", -1);
            CreditTO transaction = new DBhelper(this).getCredit(id);

            // fillin data
            TextView amount = (TextView) findViewById(R.id.transAmountText);
            amount.setOnClickListener(this);
            if (transaction.getType() == CreditTO.CreditType.GET)
                amount.setTextColor(Color.parseColor("#aa3333"));
            else
                amount.setTextColor(Color.parseColor("#33aa33"));
            amount.setText(transaction.getType().getSign() + String.format("%.2f", transaction.getAmount()));
            TextView comment = (TextView) findViewById(R.id.transCommentText);
            comment.setText(transaction.getComment());
            TextView date = (TextView) findViewById(R.id.transDate);
            date.setText(transaction.getDateStr());

        }
    }

    public void onClick(View view)
    {
        Intent intent = new Intent(this, SummEditActivity.class);
        TextView amountField = (TextView) findViewById(R.id.transAmountText);

        intent.putExtra(Const.ID_TAG, id);
        intent.putExtra(Const.AMOUNT_TAG, amountField.getText().toString().substring(1));
        startActivityForResult(intent, 100);
    }
}
