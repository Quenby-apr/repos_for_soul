package com.example.lab_1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import business_logic.binding_models.MyObjectBindingModel;
import business_logic.business_logics.MyObjectLogic;
import business_logic.interfaces.IObjectStorage;
import business_logic.view_models.MyObjectViewModel;
import database_implement.MyObjectStorage;
import services.DbService;


public class MainActivity extends AppCompatActivity implements IListFunction{
    MainFragment mainFragment;
    ElementFragment elementFragment;
    ArrayList<MyObjectViewModel> myObjects;
    private int editableItemId = -1;
    private final int DB_VERSION = 1;
    private MyObjectLogic myObjectLogic = null;
    private IObjectStorage myObjectStorage = null;
    private boolean storage;
    private boolean dataFromDatabase;
    private boolean bound;
    ListView listView;
    ArrayAdapter<MyObjectViewModel> adapter;
    private DbService dbService;
    private ServiceConnection sConn;
    private Intent intent;
    Executor ex;
    Handler mainLooperHandler;

    public Intent chooseActivity (Class param) {
        intent = new Intent(this, param);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLooperHandler = new Handler(getMainLooper());
        ex = Executors.newSingleThreadExecutor();
        intent = new Intent(this, DbService.class);
        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                dbService = ((DbService.DbBinder) binder).getService();
                bound = true;
                ex.execute(() -> loadData(mainLooperHandler));
            }

            public void onServiceDisconnected(ComponentName name) {
                bound = false;
            }
        };
        bindService(intent, sConn, BIND_AUTO_CREATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        mainFragment = new MainFragment();
        elementFragment = new ElementFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.FragmentContainer, mainFragment);
        ft.commit();

        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            ex.execute(() -> {
                ArrayList<MyObjectViewModel> myObjectsFromData = myObjectLogic.read(new MyObjectBindingModel() {{
                    setId(myObjects.get(i).getId());
                }});
                if (myObjectsFromData != null) {
                    MyObjectViewModel myObject = myObjectsFromData.get(0);
                    try {
                        myObjectLogic.createOrUpdate(new MyObjectBindingModel(myObject.getId(), myObject.getName(), myObject.getNumber(), !myObject.isLogic()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void onResume() {
        super.onResume();
        Bundle arguments = getIntent().getExtras();
        if (arguments !=null) {
            String st = arguments.get("storage").toString();
            myObjectStorage = st.equals("DB") ? new MyObjectStorage(dbService) : new file_implement.MyObjectStorage(this);
            myObjectLogic = new MyObjectLogic(myObjectStorage);
            ex.execute(() -> loadData(mainLooperHandler));
        }
    }

    public void loadData(Handler handler) {
        if (listView == null) return;
        myObjects = myObjectLogic.read(null);

        if (myObjects != null) {
            adapter = new ArrayAdapter<>(this,  R.layout.list_items, myObjects);
            handler.post(() -> {
                listView.setAdapter(adapter);
                listView.clearChoices();
                for(int i = 0; i < myObjects.size();i++) {
                    listView.setItemChecked(i, myObjects.get(i).isLogic());
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void LoadData() throws IOException {
        Intent intent = chooseActivity(StorageActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void LoadDataJson() {
        ex.execute(() -> {
            try {
                ArrayList<MyObject> myObjects = JsonHelper.importFromJSON(this);
                if (!myObjects.isEmpty()) {
                    myObjectLogic.loadDataFromJson(myObjects);
                    ex.execute(() -> loadData(mainLooperHandler));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    @Override
    public void Create() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.FragmentContainer, elementFragment);
        ft.addToBackStack(null);
        Bundle bundle = new Bundle();
        bundle.putSerializable("command", Commands.Create);
        elementFragment.setArguments(bundle);
        ft.commit();
    }

    @Override
    public void Edit() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.FragmentContainer, elementFragment);
        ft.addToBackStack(null);
        Bundle bundle = new Bundle();
        bundle.putSerializable("command", Commands.Edit);
        elementFragment.setArguments(bundle);
        ft.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void Delete() {
        int size = adapter.getCount();

        List<MyObjectViewModel> removeList = IntStream.range(0, size)
                .filter(i -> listView.isItemChecked(i))
                .mapToObj(adapter::getItem)
                .collect(Collectors.toList());

        ex.execute(() -> {
            myObjects.removeAll(removeList);
            for (MyObjectViewModel obj : removeList) {
                try {
                    myObjectLogic.delete(new MyObjectBindingModel() {{
                        setId(obj.getId());
                    }});
                    loadData(mainLooperHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < size; i++) {
                listView.setItemChecked(i, false);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myObjectStorage.closure();
        if (!bound) return;
        unbindService(sConn);
        bound = false;
    }

    @Override
    public void Ok(Commands command, String name, double number,boolean logic) {
        MyObjectViewModel myObject;
        switch (command) {
            case Create:
                Integer id = null;
                Integer finalId1 = id;
                ex.execute(() -> {
                    try {
                        myObjectLogic.createOrUpdate(new MyObjectBindingModel(finalId1, name, number, logic) {
                        });
                        loadData(mainLooperHandler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                adapter.notifyDataSetChanged();
                break;
            case Edit:
                myObject = new MyObjectViewModel();
                myObject.setName(name);
                myObject.setNumber(number);
                myObject.setLogic(logic);
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (listView.isItemChecked(i)){
                        id = adapter.getItem(i).getId();
                        Integer finalId = id;
                        ex.execute(() -> {
                            try {
                                myObjectLogic.createOrUpdate(new MyObjectBindingModel(finalId, name, number, logic) {
                                });
                                loadData(mainLooperHandler);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case Search:
                ex.execute(() -> {
                    Intent intent = chooseActivity(FiltredListActivity.class);
                    ArrayList<String> list = new ArrayList<String>();
                    for(int i = 0; i<adapter.getCount(); i++)
                    {
                        if (((MyObjectViewModel) (adapter.getItem(i))).toString().contains(name)) {
                        list.add(adapter.getItem(i).toString());
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("elems",list);
                    intent.putExtras(bundle);
                    mainLooperHandler.post(() -> startActivity(intent));
                    });
                break;
            default:
                break;
        }
    }

    @Override
    public void Search() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.FragmentContainer, elementFragment);
        ft.addToBackStack(null);
        Bundle bundle = new Bundle();
        bundle.putSerializable("command", Commands.Search);
        elementFragment.setArguments(bundle);
        ft.commit();

    }
}