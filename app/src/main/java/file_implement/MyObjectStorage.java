package file_implement;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.lab_1.MyObject;

import java.util.ArrayList;

import business_logic.binding_models.MyObjectBindingModel;
import business_logic.interfaces.IObjectStorage;
import business_logic.view_models.MyObjectViewModel;

public class MyObjectStorage implements IObjectStorage {
    private final FileDataListSingleton source;
    private final Context context;

    public MyObjectStorage(Context context) {
        source = FileDataListSingleton.getInstance(context);
        this.context = context;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ArrayList<MyObjectViewModel> getFullList() {
        ArrayList<MyObjectViewModel> list = new ArrayList<>();
        if (source.getMyObjects() != null) {
            source.getMyObjects().forEach((x) -> list.add(createModel(x)));
        }
        return list.size() > 0 ? list : null;
    }

    @Override
    public ArrayList<MyObjectViewModel> getFilteredList(MyObjectBindingModel model) {
        if (source.getMyObjects() == null || model == null) return null;
        ArrayList<MyObjectViewModel> list = new ArrayList<>();
        for (MyObject obj : source.getMyObjects()) {
            if (obj.getName().contains(model.getName())) {
                list.add(createModel(obj));
            }
        }
        return list.size() > 0 ? list : null;
    }

    @Override
    public MyObjectViewModel getElement(MyObjectBindingModel model) {
        if (source.getMyObjects() == null || model == null) return null;
        for (MyObject obj : source.getMyObjects()) {
            if (model.getId() != null && obj.get_id() == model.getId()) {
                return createModel(obj);
            } else if (obj.getName() != null && obj.getName().equals(model.getName())) {
                return createModel(obj);
            }
        }
        return null;
    }

    @Override
    public void insert(MyObjectBindingModel model) {
        if (source.getMyObjects() == null || model == null) return;
        int maxId = source.getMyObjects().size() > 0 ? -1 : 1;
        if (maxId == -1) {
            for (MyObject obj : source.getMyObjects()) {
                if (obj.get_id() > maxId) maxId = obj.get_id();
            }
            maxId++;
        }
        MyObject obj = new MyObject();
        obj.set_id(maxId);
        source.getMyObjects().add(createModel(model, obj));
        source.saveObjects();
    }

    @Override
    public void update(MyObjectBindingModel model) throws Exception {
        if (source.getMyObjects() == null || model == null) return;
        for (MyObject obj : source.getMyObjects()) {
            if (obj.get_id() == model.getId()) {
                createModel(model, obj);
                source.saveObjects();
                return;
            }
        }
        throw new Exception("Элемент не найден");
    }

    @Override
    public void delete(MyObjectBindingModel model) throws Exception {
        if (source.getMyObjects() == null || model == null) return;
        for (MyObject obj : source.getMyObjects()) {
            if (obj.get_id() == model.getId()) {
                source.getMyObjects().remove(obj);
                source.saveObjects();
                return;
            }
        }
        throw new Exception("Элемент не найден");
    }

    @Override
    public void loadDataFromJSON(ArrayList<MyObject> myObjects) {
        if (source.getMyObjects() == null || myObjects == null) return;
        source.getMyObjects().addAll(myObjects);
    }

    @Override
    public void closure() {
        FileDataListSingleton.getInstance(context).closure();
    }
    private MyObject createModel(MyObjectBindingModel model, MyObject myObject) {
        myObject.setName(model.getName());
        myObject.setNumber(model.getNumber());
        myObject.setLogic(model.isLogic());
        return myObject;
    }

    private MyObjectViewModel createModel(MyObject myObject) {
        return new MyObjectViewModel(myObject.get_id(), myObject.getName(), myObject.getNumber(), myObject.isLogic());
    }
}
