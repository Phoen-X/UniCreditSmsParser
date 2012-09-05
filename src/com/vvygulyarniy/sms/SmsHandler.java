package com.vvygulyarniy.sms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 23.07.12
 * Time: 10:07
 */
public class SmsHandler
{
    /**
     * Helper functions for the "threads" table used by MMS and SMS.
     */
    public static final class Threads {
        private static final String[] ID_PROJECTION = { BaseColumns._ID };
        private static final String STANDARD_ENCODING = "UTF-8";
        private static final Uri THREAD_ID_CONTENT_URI = Uri.parse("content://mms-sms/threadID");
        public static final Uri CONTENT_URI = Uri.parse("content://mms-sms/conversations");

        public static final Uri OBSOLETE_THREADS_URI = Uri.withAppendedPath(
                CONTENT_URI, "obsolete");

        public static final int COMMON_THREAD    = 0;
        public static final int BROADCAST_THREAD = 1;
        private static final String TAG = "THREAD_HELPER";


        /**
         * This is a single-recipient version of
         * getOrCreateThreadId.  It's convenient for use with SMS
         * messages.
         */
        public static long getOrCreateThreadId(Context context, String recipient) {
            Set<String> recipients = new HashSet<String>();

            recipients.add(recipient);
            return getOrCreateThreadId(context, recipients);
        }

        /**
         * Given the recipients list and subject of an unsaved message,
         * return its thread ID.  If the message starts a new thread,
         * allocate a new thread ID.  Otherwise, use the appropriate
         * existing thread ID.
         *
         * Find the thread ID of the same set of recipients (in
         * any order, without any additions). If one
         * is found, return it.  Otherwise, return a unique thread ID.
         */
        public static long getOrCreateThreadId(
                Context context, Set<String> recipients) {
            Uri.Builder uriBuilder = THREAD_ID_CONTENT_URI.buildUpon();

            for (String recipient : recipients)
            {
                uriBuilder.appendQueryParameter("recipient", recipient);
            }

            Uri uri = uriBuilder.build();
            Log.v("THREAD_HELPER", "getOrCreateThreadId uri: " + uri);

            Cursor cursor = context.getContentResolver().query(uri, ID_PROJECTION, null, null, null);

            Log.v(TAG, "getOrCreateThreadId cursor cnt: " + cursor.getCount());

            try {
                if (cursor.moveToFirst()) {
                    return cursor.getLong(0);
                } else {
                    Log.e(TAG, "getOrCreateThreadId returned no rows!");
                }
            } finally {
                cursor.close();
            }

            Log.e(TAG, "getOrCreateThreadId failed with uri " + uri.toString());
            throw new IllegalArgumentException("Unable to find or allocate a thread ID.");
        }
    }
}
