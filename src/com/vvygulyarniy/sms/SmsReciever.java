package com.vvygulyarniy.sms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import com.vvygulyarniy.R;
import com.vvygulyarniy.activity.TransactionListActivity;
import com.vvygulyarniy.db.DBhelper;
import com.vvygulyarniy.to.CreditTO;
import com.vvygulyarniy.to.SmsTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 23.07.12
 * Time: 10:25
 */
public class SmsReciever extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(context.getString(R.string.appName), "Got message. Processing");
        Bundle bundle = intent.getExtras();
        if(bundle != null)
        {
            NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Object[] messagesPdus = (Object[]) bundle.get("pdus");

            ArrayList<SmsMessage> messages = new ArrayList<SmsMessage>(messagesPdus.length);

            for (Object messagesPdu : messagesPdus)
            {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) messagesPdu);
                messages.add(message);
            }

            ArrayList<SmsTO> smsList = processMessageList(messages);

            for (SmsTO smsTO : smsList)
            {
                if (smsTO.getFrom().equals("332"))
                {
                    try
                    {
                        CreditTO credit = parseMessage(context, smsTO);

                        //если смску распарсили удачно
                        if(credit != null)
                        {
                            DBhelper helper = new DBhelper(context);
                            helper.addCredit(credit);
                            helper.addIncomingSms(smsTO);
                            Intent notificationIntent = new Intent(context, TransactionListActivity.class);

                            Notification notif = new Notification.Builder(context)
                                    .setContentText(String.valueOf(credit.getType().getSign()) + credit.getAmount())
                                    .setContentTitle(context.getString(R.string.transactionAdded))
                                    .setSmallIcon(android.R.drawable.stat_notify_sync_noanim)
                                    .setAutoCancel(true)
                                    .setContentIntent(PendingIntent.getActivity(context, 0,
                                            notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                                    .build();
                            notifManager.notify(context.getString(R.string.transactionAdded), 1, notif);

                            abortBroadcast();
                        }
                    }
                    catch (Exception e)
                    {
                        Notification notif = new Notification.Builder(context)
                                .setContentInfo("ERROR")
                                .setContentText(e.getLocalizedMessage())
                                .setContentTitle("An error during bank transaction processing")
                                .setSmallIcon(android.R.drawable.stat_notify_error)
                                .build();

                        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify("USB BANK ERROR", 1, notif);
                        e.printStackTrace();
                    }
                }
            }
        }
        Log.d(context.getString(R.string.appName), "Message processed");
    }

    public static CreditTO parseMessage(Context context, SmsTO sms) throws ParseException
    {
        String getRegexp = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}) ([\\d \\.]+) (.{3}) (.+)AM[ _]AVAIL (.+) UAH".intern();
        String addRegexp = " ([\\d \\.]+) (.{3}) (\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}) (.+)AM_AVAIL (.+) UAH".intern();

        String smsBody = sms.getText();

        Matcher getMatch = Pattern.compile(getRegexp).matcher(smsBody);
        Matcher addMatch = Pattern.compile(addRegexp).matcher(smsBody);

        CreditTO credit = null;

        if(getMatch.find())
        {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(getMatch.group(1));
            float amount = getFloatValue(getMatch.group(2));
            float total =  getFloatValue(getMatch.group(5));
            String comment = getMatch.group(4);

            credit = new CreditTO().setDate(date)
                    .setType(CreditTO.CreditType.GET)
                    .setComment(comment)
                    .setAmount(amount)
                    .setAmountTotal(total);
        }
        else
        if(addMatch.find())
        {
            float amount = getFloatValue(addMatch.group(1));
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(addMatch.group(3));
            String comment = addMatch.group(4);
            float total =  getFloatValue(addMatch.group(5));

            credit= new CreditTO().setDate(date)
                    .setType(CreditTO.CreditType.ADD)
                    .setComment(comment)
                    .setAmount(amount)
                    .setAmountTotal(total);
        }

        Log.d(context.getString(R.string.appName), "SMS message parsed. Value: " + credit);
        return credit;
    }

    private static float getFloatValue(String group)
    {
        if(group.isEmpty())
            return 0;

        return Float.valueOf(group.replace(" ", "").replace(",", "."));
    }

    public ArrayList<SmsTO> processMessageList(ArrayList<SmsMessage> smsList)
    {
        ArrayList<SmsTO> processedMessages = new ArrayList<SmsTO>(smsList.size());

        for (SmsMessage smsMessage : smsList)
        {
            if(processedMessages.size() > 0)
            {
                SmsTO lastMessage = processedMessages.get(processedMessages.size()-1);
                String thisSender = smsMessage.getOriginatingAddress();
                String lastSender = lastMessage.getFrom();

                if(thisSender.equalsIgnoreCase(lastSender))
                {
                    lastMessage.setText(lastMessage.getText() + smsMessage.getMessageBody());
                    continue;
                }
            }

            SmsTO newSms = new SmsTO().setFrom(smsMessage.getOriginatingAddress())
                                      .setDate(smsMessage.getTimestampMillis())
                                      .setServiceCenterAddress(smsMessage.getServiceCenterAddress())
                                      .setText(smsMessage.getMessageBody());
            processedMessages.add(newSms);
        }

        return processedMessages;
    }
}
