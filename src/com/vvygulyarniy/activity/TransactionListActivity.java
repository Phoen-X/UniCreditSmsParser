package com.vvygulyarniy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.vvygulyarniy.R;
import com.vvygulyarniy.db.DBhelper;
import com.vvygulyarniy.list.CreditListAdapter;
import com.vvygulyarniy.to.CreditTO;

import java.util.ArrayList;

public class TransactionListActivity extends Activity
{
    public String myApp = "USBBankCreditGetter".intern();
    DBhelper databaseHelper;



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onStart()
    {
        databaseHelper = new DBhelper(this);
        updateList();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        closeOptionsMenu();
        if(item.getItemId() == R.id.updateFromSMS)
        {
            databaseHelper.updateFromMessages();
            updateList();
        }
        if(item.getItemId() == R.id.updateList)
        {
            updateList();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateList()
    {
        ListView creditList = (ListView) findViewById(R.id.creditList);

        ArrayList<CreditTO> credits = databaseHelper.getCreditsFromDB();

        if(credits.isEmpty())
        {
            databaseHelper.updateFromMessages();
            credits = databaseHelper.getCreditsFromDB();
        }

        CreditListAdapter adapter = new CreditListAdapter(this, credits);
        creditList.setAdapter(adapter);
        creditList.setSelection(adapter.getCount() - 1);
    }
}
