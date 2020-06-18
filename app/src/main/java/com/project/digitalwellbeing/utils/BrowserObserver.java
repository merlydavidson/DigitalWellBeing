package com.project.digitalwellbeing.utils;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Browser;
import android.util.Log;

import static androidx.constraintlayout.widget.Constraints.TAG;

 public class BrowserObserver extends ContentObserver {
    private static String lastVisitedURL = "";
    private static String lastVisitedWebsite = "";
    Context context;

    //Query values:
    final Uri BOOKMARKS_URI = Uri.parse("content://com.android.chrome.browser/bookmarks");
     final String[] HISTORY_PROJECTION = new String[]
             {
                     "_id", // 0
                     "url", // 1
                     "visits", // 2
                     "date", // 3
                     "bookmark", // 4
                     "title", // 5
                     "favicon", // 6
                     "thumbnail", // 7
                     "touch_icon", // 8
                     "user_entered", // 9
             };

     final String selection = "bookmark" + " = 0";  // history item
    final String sortOrder = "date";  // the date the item was last visited


    public BrowserObserver(Handler handler) {
        super(handler);
    }

     public BrowserObserver(Handler handler, Context context) {
         super(handler);
         this.context = context;
         getURl();
     }


     @Override
    public void onChange(boolean selfChange) {
        onChange(selfChange, null);
    }


    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange);

        //Retrieve all the visited URLs:
        final Cursor cursor = context.getContentResolver().query(BOOKMARKS_URI, HISTORY_PROJECTION, selection, null, sortOrder);

        //Retrieve the last URL:
        cursor.moveToLast();
       final String url = cursor.getString(cursor.getColumnIndex(HISTORY_PROJECTION[1]));

        //Close the cursor:
        cursor.close();

        if ( !url.equals(lastVisitedURL) ) {  // to avoid information retrieval and/or refreshing...
            lastVisitedURL = url;

            //Debug:
            Log.d(TAG, "URL Visited: " + url + "\n");
        }
    }

    public void getURl()
    {
        final Cursor cursor = context.getContentResolver().query(BOOKMARKS_URI, HISTORY_PROJECTION, selection, null, sortOrder);

        //Retrieve the last URL:
        cursor.moveToLast();
        final String url = cursor.getString(cursor.getColumnIndex(HISTORY_PROJECTION[1]));

        //Close the cursor:
        cursor.close();

        if ( !url.equals(lastVisitedURL) ) {  // to avoid information retrieval and/or refreshing...
            lastVisitedURL = url;

            //Debug:
            Log.d(TAG, "URL Visited: " + url + "\n");
        }
    }
}