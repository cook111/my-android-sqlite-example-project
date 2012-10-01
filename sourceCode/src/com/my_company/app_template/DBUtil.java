package com.my_company.app_template;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author preetam.palwe
 *
 */
public class DBUtil
{
    //private static final String TAG = "DBUtil";

    protected static void safeCloseCursor(Cursor cursor)
    {
     if (cursor != null)
     {
         cursor.close();
     }
    }//end safeCloseCursor

    protected static void safeCloseDataBase(SQLiteDatabase database)
    {
     if (database != null)
     {
         database.close();
     }
    }//end safeCloseDataBase
}//end class DBUtil
