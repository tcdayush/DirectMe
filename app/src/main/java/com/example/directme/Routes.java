package com.example.directme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class Routes extends Activity {

    private ArrayList<String> stringArrayList;
    private EditText editText;
    private  ArrayAdapter<String> stringArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routes);

        Button button = findViewById(R.id.button);
        ListView listView = findViewById(R.id.listView);
        editText = findViewById(R.id.editText);

        stringArrayList= new ArrayList<>();
        
        stringArrayAdapter =new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,stringArrayList);
        listView.setAdapter(stringArrayAdapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringArrayList.add(editText.getText().toString());
                stringArrayAdapter.notifyDataSetChanged();
            }
        });


    }
}
