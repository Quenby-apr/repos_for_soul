package com.example.lab_1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import business_logic.binding_models.MyObjectBindingModel;
import business_logic.business_logics.MyObjectLogic;
import business_logic.interfaces.IObjectStorage;
import business_logic.view_models.MyObjectViewModel;
import database_implement.MyObjectStorage;


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
    ListView listView;
    ArrayAdapter<MyObjectViewModel> adapter;
    private final String xml_objects_tag = "objects";
    private final String xml_object_tag = "object";
    private final String xml_name_tag = "name";
    private final String xml_number_tag = "number";
    private final String xml_logic_tag = "logic";

    public Intent chooseActivity (Class param) {
        Intent intent = new Intent(this, param);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainFragment = new MainFragment();
        elementFragment = new ElementFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.FragmentContainer, mainFragment);
        ft.commit();
        listView = findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
    }
    public void onResume() {
        super.onResume();
        Bundle arguments = getIntent().getExtras();
        if (arguments !=null) {
            String st = arguments.get("storage").toString();
            myObjectStorage = st.equals("DB") ? new MyObjectStorage(this, DB_VERSION) : new file_implement.MyObjectStorage(this);
            myObjectLogic = new MyObjectLogic(myObjectStorage);
            loadData();
        }
    }

    public void loadData() {
        if (listView == null) return;
        myObjects = myObjectLogic.read(null);

        if (myObjects != null) {
            adapter = new ArrayAdapter<>(this,  R.layout.list_items, myObjects);
            listView.setAdapter(adapter);
            listView.clearChoices();
            for(int i = 0; i < myObjects.size();i++) {
                listView.setItemChecked(i, myObjects.get(i).isLogic());
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void LoadData() throws IOException {
        Intent intent = chooseActivity(StorageActivity.class);
        startActivity(intent);
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

        myObjects.removeAll(removeList);
        for (MyObjectViewModel obj : removeList) {
            try {
                myObjectLogic.delete(new MyObjectBindingModel() {{
                    setId(obj.getId());
                }});
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(int i = 0; i<size; i++)
        {
            listView.setItemChecked(i, false);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void Ok(Commands command, String name, double number,boolean logic) {
        MyObjectViewModel myObject;
        switch (command) {
            case Create:
                Integer id = null;
                try {
                    myObjectLogic.createOrUpdate(new MyObjectBindingModel(id, name, number, logic) {
                    });
                    loadData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                        try {
                            myObjectLogic.createOrUpdate(new MyObjectBindingModel(id, name, number, logic) {
                            });
                            loadData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case Search:
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
                startActivity(intent);
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