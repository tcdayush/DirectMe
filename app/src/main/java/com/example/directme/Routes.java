package com.example.directme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class Routes extends Activity {

    private ArrayList<String> stringArrayList;
    private  ArrayAdapter<String> stringArrayAdapter;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routes);

        textView = findViewById(R.id.response);
        textView.setText("ABC");
        ListView listView = findViewById(R.id.listView);

        stringArrayList= new ArrayList<>();

        stringArrayAdapter =new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,stringArrayList);
        listView.setAdapter(stringArrayAdapter);

        try
        {
            JSONObject obj = new JSONObject(readJSONFromAsset());

            JSONArray countries=obj.getJSONArray("Routes");
            for (int i=0;i<countries.length();i++){
                JSONObject jsonObject=countries.getJSONObject(i);
                String Rank=jsonObject.getString("Rank");
                String Modes=jsonObject.getString("Modes");
                String Time=jsonObject.getString("Time");
                String Distance=jsonObject.getString("Distance");
                stringArrayList.add(Rank + " \t" + Modes + " \t" +Time + " \t" +Distance);
                stringArrayAdapter.notifyDataSetChanged();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
                Toast.makeText(getApplicationContext()," " + position,Toast.LENGTH_LONG).show();
                // Then you start a new Activity via Intent
                Intent intent = new Intent();
                intent.setClass(Routes.this, MapsActivity.class);
                intent.putExtra("position", position);
                // Or / And
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });*/

    }

    public String readJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("sampleCombinedRoutes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
