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
import services.DbService;

public class MyObjectStorage implements IObjectStorage {
    private final DbService dbService;

    public MyObjectStorage(DbService dbService) {
        this.dbService = dbService;
    }

    @Override
    public ArrayList<MyObjectViewModel> getFullList() {
        ArrayList<MyObjectViewModel> listObjects;
        Cursor c = dbService.read("MyObjects", null, null, null, null, null, null);
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
        ArrayList<MyObjectViewModel> listObjects;
        Cursor c = dbService.read("MyObjects", null, null, null, null, null, null);
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
        MyObjectViewModel element = null;
        String selection = "";
        if (model.getId() != null) selection = "_id = " + model.getId();
        else if (model.getName() != null) selection = "name = '" + model.getName() + "'";
        Cursor c = dbService.read("MyObjects", null, selection, null, null, null, null);
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
        dbService.insert("MyObjects", createModel(model));
    }

    @Override
    public void update(MyObjectBindingModel model) throws Exception {
        MyObjectViewModel obj = getElement(model);
        if (obj == null) {
            throw new Exception("Элемент не найден");
        }
        dbService.update("MyObjects", createModel(model), "_id = ?", new String[]{String.valueOf(model.getId())});
    }

    @Override
    public void delete(MyObjectBindingModel model) throws Exception {
        MyObjectViewModel obj = getElement(model);
        if (obj == null) {
            throw new Exception("Элемент не найден");
        }
        dbService.delete("MyObjects", "_id = ?", new String[]{String.valueOf(model.getId())});
    }

    @Override
    public void loadDataFromJSON(ArrayList<MyObject> myObjects) {
        if (myObjects == null) return;
        dbService.loadDataFromJSON("MyObjects",myObjects);
    }

    private ContentValues createModel(MyObjectBindingModel model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("logic", model.isLogic());
        contentValues.put("name", model.getName());
        contentValues.put("number", model.getNumber());
        return contentValues;
    }

    @Override
    public void closure(){
        dbService.stopSelf();
    }
}
