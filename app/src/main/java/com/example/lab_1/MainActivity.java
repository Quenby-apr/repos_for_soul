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


public class MainActivity extends AppCompatActivity implements IListFunction{
    MainFragment mainFragment;
    ElementFragment elementFragment;
    ArrayList<MyObject> myObjects= new ArrayList<MyObject>();
    ListView listView;
    ArrayAdapter<MyObject> adapter;
    public static List<String> names = new ArrayList<String>(Arrays.asList("Курс по русскому", "Курс по математике", "Курс по физике", "Курс по английскому", "Курс по химии", "Курс по информатике"));;
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
        loadData();
    }

    private void saveData() {
        if (adapter == null) return;
        try ( BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(openFileOutput("listData.xml", MODE_PRIVATE)))){
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, xml_objects_tag);
            for (int i = 0; i < myObjects.size(); i++) {
                xmlSerializer.startTag(null, xml_object_tag);
                xmlSerializer.startTag(null, xml_name_tag);
                xmlSerializer.text(myObjects.get(i).getName());
                xmlSerializer.endTag(null, xml_name_tag);
                xmlSerializer.startTag(null, xml_number_tag);
                xmlSerializer.text(String.valueOf(myObjects.get(i).getNumber()));
                xmlSerializer.endTag(null, xml_number_tag);
                xmlSerializer.startTag(null, xml_logic_tag);
                xmlSerializer.text(String.valueOf(myObjects.get(i).isLogic()));
                xmlSerializer.endTag(null, xml_logic_tag);
                xmlSerializer.endTag(null, xml_object_tag);
            }
            xmlSerializer.endTag(null, xml_objects_tag);
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            bufferedWriter.write(writer.toString());
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        if (listView == null) return;
        String data = null;
        try {
            FileInputStream fis = getApplicationContext().openFileInput("listData.xml");
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            isr.close();
            fis.close();
        } catch (FileNotFoundException e3) {
            e3.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            // включаем поддержку namespace (по умолчанию выключена)
            factory.setNamespaceAware(true);
            // создаем парсер
            XmlPullParser xpp = factory.newPullParser();
            // даем парсеру на вход Reader
            if (!TextUtils.isEmpty(data)) {
                xpp.setInput(new StringReader(data));

                MyObject object = null;
                String value = null;
                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    switch (xpp.getEventType()) {
                        case XmlPullParser.START_TAG:
                            switch (xpp.getName()) {
                                case xml_objects_tag: {
                                    myObjects = new ArrayList<>();
                                    break;
                                }
                                case xml_object_tag: {
                                    object = new MyObject();
                                    myObjects.add(object);
                                    break;
                                }
                                default:
                                    break;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            switch (xpp.getName()) {
                                case xml_name_tag: {
                                    if (object != null && value != null) object.setName(value);
                                    break;
                                }
                                case xml_number_tag: {
                                    if (object != null && value != null)
                                        object.setNumber(Double.parseDouble(value));
                                    break;
                                }
                                case xml_logic_tag:l: {
                                    if (object != null && value != null)
                                        object.setLogic(Boolean.parseBoolean(value));
                                    break;
                                }
                            }
                            break;
                        case XmlPullParser.TEXT:
                            value = xpp.getText();
                            break;
                        default:
                            break;
                    }
                    // следующий элемент
                    xpp.next();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    public void LoadDataJson() throws IOException {
        myObjects = JsonHelper.importFromJSON(this);
        if (myObjects != null) {
            adapter = new ArrayAdapter<>(this, R.layout.list_items, myObjects);
            listView.setAdapter(adapter);
            listView.clearChoices();
            for(int i = 0; i < myObjects.size();i++) {
                listView.setItemChecked(i, myObjects.get(i).isLogic());
            }
        }
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

        List<MyObject> removeList = IntStream.range(0, size)
                .filter(i -> listView.isItemChecked(i))
                .mapToObj(adapter::getItem)
                .collect(Collectors.toList());

        myObjects.removeAll(removeList);
        for(int i = 0; i<size; i++)
        {
            listView.setItemChecked(i, false);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    @Override
    public void Ok(Commands command, String name, double number,boolean logic) {
        MyObject myObject;
        switch (command) {
            case Create:
                myObjects.add(new MyObject(name,number,logic));
                adapter.notifyDataSetChanged();
                break;
            case Edit:
                myObject = new MyObject();
                myObject.setName(name);
                myObject.setNumber(number);
                myObject.setLogic(logic);
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (listView.isItemChecked(i)){
                        myObjects.set(i,myObject);
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case Search:
                Intent intent = chooseActivity(FiltredListActivity.class);
                ArrayList<String> list = new ArrayList<String>();
                for(int i = 0; i<adapter.getCount(); i++)
                {
                    if (((MyObject) (adapter.getItem(i))).toString().contains(name)) {
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