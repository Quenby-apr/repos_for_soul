package database_implement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.lab_1.MyObject;
import java.util.ArrayList;
import business_logic.binding_models.MyObjectBindingModel;
import business_logic.interfaces.IObjectStorage;
import business_logic.view_models.MyObjectViewModel;

public class MyObjectStorage implements IObjectStorage {
    private final DbContext dbContext;
    private SQLiteDatabase db;

    public MyObjectStorage(Context context, int version) {
        dbContext = new DbContext(context, version);
        db = dbContext.getReadableDatabase();
    }

    @Override
    public ArrayList<MyObjectViewModel> getFullList() {
        if (!db.isOpen()) db = dbContext.getReadableDatabase();
        ArrayList<MyObjectViewModel> listObjects;
        Cursor c = db.query("MyObjects", null, null, null, null, null, null);
        listObjects = new ArrayList<>();
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("_id");
            int nameColIndex = c.getColumnIndex("name");
            int numberColIndex = c.getColumnIndex("number");
            int logicColIndex = c.getColumnIndex("logic");

            do {
                listObjects.add(new MyObjectViewModel() {{
                    setId(c.getInt(idColIndex));
                    setName(c.getString(nameColIndex));
                    setNumber(c.getInt(numberColIndex));
                    setLogic(c.getInt(logicColIndex) == 1);
                }});
            } while (c.moveToNext());
        }
        c.close();
        return listObjects;
    }

    @Override
    public ArrayList<MyObjectViewModel> getFilteredList(MyObjectBindingModel model) {
        if (!db.isOpen()) db = dbContext.getReadableDatabase();
        ArrayList<MyObjectViewModel> listObjects;
        Cursor c = db.query("MyObjects", null, null, null, null, null, null);
        listObjects = new ArrayList<>();
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("_id");
            int nameColIndex = c.getColumnIndex("name");
            int numberColIndex = c.getColumnIndex("number");
            int logicColIndex = c.getColumnIndex("logic");

            do {
                String name = c.getString(nameColIndex);
                String searchTerm = model.getName();
                if (searchTerm != null && name.contains(searchTerm)) {
                    listObjects.add(new MyObjectViewModel() {{
                        setId(c.getInt(idColIndex));
                        setName(c.getString(nameColIndex));
                        setNumber(c.getInt(numberColIndex));
                        setLogic(c.getInt(logicColIndex) == 1);
                    }});
                }
            } while (c.moveToNext());
        }
        c.close();
        return listObjects;
    }

    @Override
    public MyObjectViewModel getElement(MyObjectBindingModel model) {
        if (model == null) {
            return null;
        }
        if (!db.isOpen()) db = dbContext.getReadableDatabase();
        MyObjectViewModel element = null;
        String selection = "";
        if (model.getId() != null) selection = "_id = " + model.getId();
        else if (model.getName() != null) selection = "name = '" + model.getName() + "'";
        Cursor c = db.query("MyObjects", null, selection, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("_id");
            int nameColIndex = c.getColumnIndex("name");
            int numberColIndex = c.getColumnIndex("number");
            int logicColIndex = c.getColumnIndex("logic");

            element = new MyObjectViewModel() {{
                setId(c.getInt(idColIndex));
                setName(c.getString(nameColIndex));
                setNumber(c.getInt(numberColIndex));
                setLogic(c.getInt(logicColIndex) == 1);
            }};
        }
        c.close();
        return element;
    }

    @Override
    public void insert(MyObjectBindingModel model) {
        if (!db.isOpen()) db = dbContext.getWritableDatabase();
        else if (db.isReadOnly()) {
            db = dbContext.getWritableDatabase();
        }
        db.insert("MyObjects", null, createModel(model));
    }

    @Override
    public void update(MyObjectBindingModel model) throws Exception {
        MyObjectViewModel obj = getElement(model);
        if (obj == null) {
            throw new Exception("Элемент не найден");
        }
        if (!db.isOpen()) db = dbContext.getWritableDatabase();
        else if (db.isReadOnly()) {
            db = dbContext.getWritableDatabase();
        }
        db.update("MyObjects", createModel(model), "_id = ?", new String[]{String.valueOf(model.getId())});
    }

    @Override
    public void delete(MyObjectBindingModel model) throws Exception {
        MyObjectViewModel obj = getElement(model);
        if (obj == null) {
            throw new Exception("Элемент не найден");
        }
        if (!db.isOpen()) db = dbContext.getWritableDatabase();
        else if (db.isReadOnly()) {
            db = dbContext.getWritableDatabase();
        }
        db.delete("MyObjects", "_id = ?", new String[]{String.valueOf(model.getId())});
    }

    @Override
    public void deleteAll() {
        if (!db.isOpen()) db = dbContext.getWritableDatabase();
        else if (db.isReadOnly()) {
            db = dbContext.getWritableDatabase();
        }
        db.delete("MyObjects", null, null);
    }

    @Override
    public void loadDataFromJSON(ArrayList<MyObject> myObjects) {
        if (myObjects == null) return;
        if (!db.isOpen()) db = dbContext.getWritableDatabase();
        else if (db.isReadOnly()) {
            db = dbContext.getWritableDatabase();
        }
        db.beginTransaction();
        try {
            for (MyObject obj : myObjects) {
                if (obj.get_id() > 0 && !TextUtils.isEmpty(obj.getName())) {
                    ContentValues cv = new ContentValues();
                    cv.put("_id", obj.get_id());
                    cv.put("logic", obj.isLogic());
                    cv.put("name", obj.getName());
                    cv.put("number", obj.getNumber());
                    db.insert("MyObjects", null, cv);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private ContentValues createModel(MyObjectBindingModel model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("logic", model.isLogic());
        contentValues.put("name", model.getName());
        contentValues.put("number", model.getNumber());
        return contentValues;
    }

    @Override
    public void closure() {
        dbContext.close();
    }
}
