package varanegar.com.discountcalculatorlib.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.io.StreamCorruptedException;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountDbV3;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;

public class DiscountDbHelper {
    private static SQLiteDatabase db;
    private static DiscountDbV3 discountDbV3;
    private static boolean isDbAttached = false;

    private static SQLiteDatabase.CursorFactory factory;

     public static synchronized SQLiteDatabase getDb() {
        if (open (true))
            return db;
        else
            return null;
    }

    public static synchronized SQLiteDatabase getRealOnlyDb() {
        if (open (false))
            return db;
        else
            return null;
    }

    public static synchronized boolean open(boolean isWritable) {

        boolean result = false;

        if (discountDbV3 == null) {
            discountDbV3 = new DiscountDbV3(GlobalVariables.getDiscountInitializeHandler().getContext() );
        }

        if (isWritable) {
            if (db == null || !db.isOpen() || db.isReadOnly()) {
                db = discountDbV3.getWritableDatabase();
                /*
                Cursor c = db.rawQuery("pragma integrity_check", null);
                if (c.moveToFirst())
                    Log.e("db",c.getString(0));
                */

                result = true;
            }
        } else if (db == null || !db.isOpen()) {
            db = discountDbV3.getReadableDatabase();
            result = true;
        }
        if (db != null)
            result = true;
        return result;
    }

    public static boolean isOpen() {
        return db.isOpen();
    }

    public static void close() {
        isDbAttached = false;
        if (discountDbV3 != null)
            discountDbV3.close();
        if (db != null && db.isOpen())
            db.close();
    }

    public static void dbAttach(String attachdbquery) {
        if (isOpen() && !isDbAttached) {
            String[] attachdbs = attachdbquery.split(";");

            for (String attachitem:attachdbs) {
                db.execSQL(attachitem);
            }
            isDbAttached = true;
        }
    }

    public static void dbDetach(String attachdbquery) {
        if (isOpen() && isDbAttached) {
            String[] attachdbs = attachdbquery.split(";");

            for (String attachitem:attachdbs) {
                attachitem = attachitem.replace("ATTACH", "DETACH");
                String dbst = attachitem.substring(attachitem.indexOf("'"), attachitem.lastIndexOf("as")+ 2);
                attachitem = attachitem.replace(dbst, "");
                db.execSQL(attachitem);
            }
            isDbAttached = false;
        }
    }
}
