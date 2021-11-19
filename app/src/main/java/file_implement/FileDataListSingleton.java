package file_implement;

import android.content.Context;

import com.example.lab_1.MyObject;

import java.util.ArrayList;

public class FileDataListSingleton {
    private static FileDataListSingleton instance;
    private final String filename = "listData.xml";
    private final Context context;
    private ArrayList<MyObject> myObjects;

    private FileDataListSingleton(Context context) {
        this.context = context;
        this.myObjects = loadObjects();
    }

    public ArrayList<MyObject> getMyObjects() {
        return this.myObjects;
    }

    public static FileDataListSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new FileDataListSingleton(context);
        }
        return instance;
    }

    private ArrayList<MyObject> loadObjects() {
        ArrayList<MyObject> result = XMLSerialization.loadData(context, filename);
        return result == null ? new ArrayList<>() : result;
    }

    public void saveObjects() {
        XMLSerialization.saveData(context, filename, myObjects);
    }

    protected void closure() {
        saveObjects();
    }
}
