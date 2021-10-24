package database_implement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbContext extends SQLiteOpenHelper {
    public DbContext(Context context, int dbVersion) {
        super(context, "MobileLabs", null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("Create table MyObjects ( " +
                "_id integer primary key autoincrement, " +
                "name varchar(255), " +
                "number real, " +
                "logic boolean);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}