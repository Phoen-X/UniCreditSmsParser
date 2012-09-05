package com.vvygulyarniy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.vvygulyarniy.R;
import com.vvygulyarniy.sms.SmsHandler;
import com.vvygulyarniy.sms.SmsReciever;
import com.vvygulyarniy.to.CreditTO;
import com.vvygulyarniy.to.SmsTO;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 20.07.12
 * Time: 15:26
 */
public class DBhelper extends SQLiteOpenHelper
{
    public static final String TYPE_COLUMN = "type";
    public static final String DATE_COLUMN = "date";
    public static final String COMMENT_COLUMN = "comment";
    public static final String AMOUNT_COLUMN = "amount";
    public static final String TOTAL_AMOUNT_COLUMN = "amount_total";
    public static final String CREDIT_TABLE = "credits";
    public static final String DATABASE_NAME = "usb_db";
    public static final String ID_COLUMN = "id";
    public static final String VALUE_CHECKED = "checked";

    Context context;

    public DBhelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       Log.d(context.getString(R.string.appName), "Creating table");
       sqLiteDatabase.execSQL("CREATE TABLE " + CREDIT_TABLE + " (\n" +
               ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
               TYPE_COLUMN        + " TEXT not null,\n" +
               COMMENT_COLUMN + " TEXT,\n" +
               DATE_COLUMN        + " INTEGER not null,\n" +
               AMOUNT_COLUMN      + " FLOAT not null,\n" +
               TOTAL_AMOUNT_COLUMN + " FLOAT not null,\n" +
//               VALUE_CHECKED + " BOOLEAN not null default false" +
               ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        // there is no upgrade
        Log.d(context.getString(R.string.appName), "Upgrading database");
    }

    public ArrayList<CreditTO> getCreditsFromDB()
    {
        ArrayList<CreditTO> credits = new ArrayList<CreditTO>();

        Log.d(context.getString(R.string.appName), "getting credits list from database");

        SQLiteDatabase db = getReadableDatabase();

        Cursor cr = db.query(DBhelper.CREDIT_TABLE,
                new String[] {DBhelper.TYPE_COLUMN, DBhelper.AMOUNT_COLUMN, DBhelper.TOTAL_AMOUNT_COLUMN,
                              DBhelper.DATE_COLUMN, DBhelper.COMMENT_COLUMN, DBhelper.ID_COLUMN}, null, null,
                null,null,"date ASC");

        Log.d(context.getString(R.string.appName), "query succefully executed");

        while (cr != null && cr.moveToNext())
        {
            long date = cr.getLong(3);
            Date dt = new Date(date);
            CreditTO credit = new CreditTO()
                    .setType(CreditTO.CreditType.getTypeBySign(cr.getString(0)))
                    .setAmount(cr.getFloat(1))
                    .setAmountTotal(cr.getFloat(2))
                    .setDate(dt)
                    .setComment(cr.getString(4))
                    .setId(cr.getLong(5));
            credits.add(credit);
        }

        Log.d(context.getString(R.string.appName), String.format("got %s rows", credits.size()));

        return credits;
    }

    public CreditTO getCredit(long id)
    {
        Log.d(context.getString(R.string.appName), "Getting transaction info from DB");

        SQLiteDatabase db = getReadableDatabase();

        Cursor cr = db.query(DBhelper.CREDIT_TABLE,
                new String[] {DBhelper.TYPE_COLUMN, DBhelper.AMOUNT_COLUMN, DBhelper.TOTAL_AMOUNT_COLUMN,
                        DBhelper.DATE_COLUMN, DBhelper.COMMENT_COLUMN, DBhelper.ID_COLUMN},
                DBhelper.ID_COLUMN + " = ?",
                new String[] {String.valueOf(id)},
                null,null,null);

        Log.d(context.getString(R.string.appName), "Query succefully executed");

        if(cr != null && cr.moveToFirst())
        {
            long date = cr.getLong(3);
            Date dt = new Date(date);
            return new CreditTO()
                    .setType(CreditTO.CreditType.getTypeBySign(cr.getString(0)))
                    .setAmount(cr.getFloat(1))
                    .setAmountTotal(cr.getFloat(2))
                    .setDate(dt)
                    .setComment(cr.getString(4))
                    .setId(cr.getLong(5));
        }
        return new CreditTO();
    }

    public void updateFromMessages()
    {
        Toast.makeText(context, "Updating credit history from SMS messages", Toast.LENGTH_LONG).show();

        ArrayList<SmsTO> messages = readMessagesForNumber("332");

        try {
            ArrayList<CreditTO> credits = parseMessages(messages);
            SQLiteDatabase db = getWritableDatabase();

            db.delete(DBhelper.CREDIT_TABLE, null, null); // чистим таблицу
            for (CreditTO credit : credits)
            {
                addCredit(db, credit);
            }

            db.close();

        } catch (ParseException e)
        {
            Toast.makeText(context, "An error has occured: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<SmsTO> readMessagesForNumber(String msisdn)
    {
        Log.d(context.getString(R.string.appName), "Reading incoming SMS messages for number " + msisdn);
        ArrayList<SmsTO> messages = new ArrayList<SmsTO>();
        String[] columnsToSelect = new String[] {"address", "body", "date"};
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"),
                columnsToSelect,
                "address = ?",
                new String[] {msisdn},
                "date asc");

        if(cursor != null)
        {
            while(cursor.moveToNext())
            {
                int i = 0;
                SmsTO sms = new SmsTO().setFrom(cursor.getString(i++))
                                       .setText(cursor.getString(i++));
                messages.add(sms);
            }
        }

        Log.d(context.getString(R.string.appName), String.format("Success. Got %d messages", messages.size()));
        return messages;
    }

    public void addCredit(SQLiteDatabase db, CreditTO credit)
    {
        ContentValues row = new ContentValues();

        row.put(DBhelper.TYPE_COLUMN, String.valueOf(credit.getType().getSign()));
        row.put(DBhelper.AMOUNT_COLUMN, credit.getAmount());
        row.put(DBhelper.TOTAL_AMOUNT_COLUMN, credit.getAmountTotal());
        row.put(DBhelper.DATE_COLUMN, credit.getDate().getTime());
        row.put(DBhelper.COMMENT_COLUMN, credit.getComment());

        db.insert(DBhelper.CREDIT_TABLE, null, row);
    }

    public void addCredit(CreditTO credit)
    {
        SQLiteDatabase db = getWritableDatabase();
        addCredit(db, credit);
        db.close();
    }

    private ArrayList<CreditTO> parseMessages(ArrayList<SmsTO> messages) throws ParseException
    {
        ArrayList<CreditTO> credits = new ArrayList<CreditTO>();

        for (SmsTO message : messages)
        {
            CreditTO credit = SmsReciever.parseMessage(context, message);
            if(credit != null)
                credits.add(credit);
        }

        Collections.sort(credits);
        return credits;
    }

    public long getSmsThreadId(String msisdn)
    {
        return  SmsHandler.Threads.getOrCreateThreadId(context, msisdn);
    }

    public void addIncomingSms(SmsTO sms)
    {
        ContentValues row = new ContentValues();
        row.put("thread_id", getSmsThreadId(sms.getFrom()));
        row.put("address", sms.getFrom());
        row.put("date_sent", sms.getDate());
        row.put("date", System.currentTimeMillis());
        row.put("protocol", 0);
        row.put("read", 1);
        row.put("status", -1);
        row.put("type", 1);
        row.put("body", sms.getText());
        row.put("service_center", sms.getServiceCenterAddress());
        row.put("locked", 0);
        row.put("error_code", 0);
        row.put("seen", 1);

        Uri uri = context.getContentResolver().insert(Uri.parse("content://sms/inbox"),row);

        //Log.d("URI", uri.toString());

    }
}
