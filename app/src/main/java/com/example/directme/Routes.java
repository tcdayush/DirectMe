package com.example.directme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Routes extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routes);

        TextView textView = findViewById(R.id.response);
        textView.setText(R.string.routes_setTextViewText);
        ListView listView = findViewById(R.id.listView);

        ArrayList<String> stringArrayList = new ArrayList<>();

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);
        listView.setAdapter(stringArrayAdapter);

        //TODO: Delete the file after closing the app

        try
        {
            JSONObject obj = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                obj = new JSONObject(readJSONFromAsset());
            }

            assert obj != null;
            JSONArray routesArray=obj.getJSONArray("Routes");
            for (int i=0;i<routesArray.length();i++){
                JSONObject routesJSONObject=routesArray.getJSONObject(i);
                String rank=routesJSONObject.getString("rank");
                String time=routesJSONObject.getString("time");
                String distance=routesJSONObject.getString("distance");
                JSONArray modesArray = routesJSONObject.getJSONArray("modes");
                String type = "SOURCE -->  ";

                for (int j=0;j<modesArray.length();j++) {
                    JSONObject modesJSONObject = modesArray.getJSONObject(j);
                    type += modesJSONObject.getString("type") + "  -->  ";
                }

                type += "DESTINATION";


                stringArrayList.add(
                        rank + ".  "
                        + type + "\n"
                        + "Time: " + Integer.parseInt(time)/60 + " Minutes \n"
                        + "Distance: " + Float.parseFloat(distance)/1000 + "Kms"

                );
                stringArrayAdapter.notifyDataSetChanged();
            }



    }catch (Exception e){
            Log.d("Exception", e.toString());
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
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
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String readJSONFromAsset() {
        String json = null;
        try {
            byte[] buffer;
            //InputStream is = getApplicationContext().openFileInput("Routes.json");
            try (InputStream is = getAssets().open("sampleCombinedRoutes.json")) {
                int size = is.available();


                buffer = new byte[size];
                int readSizeInputStream = is.read(buffer);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                json = new String(buffer, UTF_8);
            }
        } catch (Exception ex) {
            Log.d("Exception", ex.toString());
            return null;
        }
        return json;
    }
}
