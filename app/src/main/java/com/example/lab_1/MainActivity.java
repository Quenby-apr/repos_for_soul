package com.example.lab_1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity implements IListFunction{
    MainFragment mainFragment;
    ElementFragment elementFragment;
    ListView listView;
    ArrayAdapter<String> adapter;
    public static List<String> names = new ArrayList<String>(Arrays.asList("Курс по русскому", "Курс по математике", "Курс по физике", "Курс по английскому", "Курс по химии", "Курс по информатике"));;

    public Intent chooseActivity (Class param) {
        Intent intent = new Intent(this, param);
        return intent;
    }

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

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                names);
        listView.setAdapter(adapter);
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

        List<String> removeList = IntStream.range(0, size)
                .filter(i -> listView.isItemChecked(i))
                .mapToObj(adapter::getItem)
                .collect(Collectors.toList());

        names.removeAll(removeList);
        for(int i = 0; i<size; i++)
        {
            listView.setItemChecked(i, false);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void Ok(Commands command, String text) {
        switch (command) {
            case Create:
                names.add(text);
                adapter.notifyDataSetChanged();
                break;
            case Edit:
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (listView.isItemChecked(i)){
                        names.set(i,text);
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case Search:
                Intent intent = chooseActivity(FiltredListActivity.class);
                ArrayList<String> list = new ArrayList<String>();
                for(int i = 0; i<adapter.getCount(); i++)
                {
                    if (((String) (adapter.getItem(i))).contains(text)) {
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