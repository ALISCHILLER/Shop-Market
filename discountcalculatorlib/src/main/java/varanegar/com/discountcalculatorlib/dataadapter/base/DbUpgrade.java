package varanegar.com.discountcalculatorlib.dataadapter.base;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by atp on 5/21/2017.
 */

public interface DbUpgrade {
    int getVersion();
    void onUpgrade(SQLiteDatabase db);
}
