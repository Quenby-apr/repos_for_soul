package business_logic.interfaces;

import com.example.lab_1.MyObject;

import java.util.ArrayList;

import business_logic.binding_models.MyObjectBindingModel;
import business_logic.view_models.MyObjectViewModel;

public interface IObjectStorage {
    ArrayList<MyObjectViewModel> getFullList();

    ArrayList<MyObjectViewModel> getFilteredList(MyObjectBindingModel model);

    MyObjectViewModel getElement(MyObjectBindingModel model);

    void insert(MyObjectBindingModel model);

    void update(MyObjectBindingModel model) throws Exception;

    void delete(MyObjectBindingModel model) throws Exception;

    void deleteAll();

    void loadDataFromJSON(ArrayList<MyObject> myObjects);

    void closure();
}
