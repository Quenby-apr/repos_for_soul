package services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.example.lab_1.MyObject;
import com.example.lab_1.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import database_implement.DbContext;

public class DbService extends Service {
    DbBinder dbBinder;
    private DbContext dbContext;
    private SQLiteDatabase db;
    ExecutorService es;
    @Override
    public IBinder onBind(Intent intent) {
        return dbBinder;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        dbBinder = new DbBinder();
        dbContext = new DbContext(getApplicationContext(), getResources().getInteger(R.integer.DB_VERSION));
        db = dbContext.getReadableDatabase();
        es = Executors.newFixedThreadPool(2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class DbBinder extends Binder {
        public DbService getService() {
            return DbService.this;
        }
    }

    public long insert(String tableName, ContentValues cv) {
        if (TextUtils.isEmpty(tableName) || cv == null) return -1;
        if (!db.isOpen()) db = dbContext.getWritableDatabase();
        else if (db.isReadOnly()) {
            db = dbContext.getWritableDatabase();
        }
        return db.insert(tableName, null, cv);
    }

    public Cursor read(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        if (TextUtils.isEmpty(tableName)) return null;
        if (!db.isOpen()) db = dbContext.getReadableDatabase();
        return db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public int update(String tableName, ContentValues cv, String selection, String[] selectionArgs) {
        if (TextUtils.isEmpty(tableName) || cv == null) return -1;
        if (!db.isOpen()) db = dbContext.getWritableDatabase();
        else if (db.isReadOnly()) {
            db = dbContext.getWritableDatabase();
        }
        return db.update(tableName, cv, selection, selectionArgs);
    }

    public int delete(String tableName, String selection, String[] selectionArgs) {
        if (TextUtils.isEmpty(tableName)) return -1;

        if (!db.isOpen()) db = dbContext.getWritableDatabase();
        else if (db.isReadOnly()) {
            db = dbContext.getWritableDatabase();
        }
        return db.delete(tableName, selection, selectionArgs);
    }

    public void loadDataFromJSON(String tableName,ArrayList<MyObject> myObjects) {
        db.beginTransaction();
        try {
            delete(tableName, null, null);
            for (MyObject obj : myObjects) {
                if (obj.get_id() > 0 && !TextUtils.isEmpty(obj.getName())) {
                    ContentValues cv = new ContentValues();
                    cv.put("_id", obj.get_id());
                    cv.put("logic", obj.isLogic());
                    cv.put("name", obj.getName());
                    cv.put("number", obj.getNumber());
                    insert(tableName, cv);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
