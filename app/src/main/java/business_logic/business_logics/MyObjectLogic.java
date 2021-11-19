package business_logic.business_logics;

import com.example.lab_1.MyObject;

import java.util.ArrayList;

import business_logic.binding_models.MyObjectBindingModel;
import business_logic.interfaces.IObjectStorage;
import business_logic.view_models.MyObjectViewModel;

public class MyObjectLogic {
    private final IObjectStorage iObjectStorage;

    public MyObjectLogic(IObjectStorage iObjectStorage) {
        this.iObjectStorage = iObjectStorage;
    }

    public ArrayList<MyObjectViewModel> read(MyObjectBindingModel model) {
        if (model == null) {
            return iObjectStorage.getFullList();
        }
        if (model.getId() != null) {
            return new ArrayList<MyObjectViewModel>() {{
                add(iObjectStorage.getElement(model));
            }};
        }
        return iObjectStorage.getFilteredList(model);
    }

    public void createOrUpdate(MyObjectBindingModel model) throws Exception {
        MyObjectViewModel obj = iObjectStorage.getElement(new MyObjectBindingModel() {
            {
                setId(model.getId());
            }
        });
        if ((obj != null && model.getId() == null) || (obj != null && model.getId() != null && obj.getId() != model.getId())) {
            throw new Exception("Уже есть элемент с таким названием");
        }
        if (model.getId() != null) {
            iObjectStorage.update(model);
        } else {
            iObjectStorage.insert(model);
        }
    }

    public void delete(MyObjectBindingModel model) throws Exception {
        MyObjectViewModel computer = iObjectStorage.getElement(new MyObjectBindingModel() {{
            setId(model.getId());
        }});
        if (computer == null) {
            throw new Exception("Элемент не найден");
        }
        iObjectStorage.delete(model);
    }

    public void loadDataFromJson(ArrayList<MyObject> objs) {
        if (objs == null) return;
        iObjectStorage.loadDataFromJSON(objs);
    }
}
