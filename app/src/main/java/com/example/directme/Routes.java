package com.example.directme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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

        try {
            JSONObject obj = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                obj = new JSONObject(readJSONFromDirectory());
            }

            assert obj != null;
            JSONArray routesArray = obj.getJSONArray("Routes");
            for (int i = 0; i < routesArray.length(); i++) {
                JSONObject routesJSONObject = routesArray.getJSONObject(i);
                String rank = routesJSONObject.getString("rank");
                String time = routesJSONObject.getString("time");
                String distance = routesJSONObject.getString("distance");
                JSONArray modesArray = routesJSONObject.getJSONArray("modes");
                JSONObject preferencesArray = routesJSONObject.getJSONObject("preferences");
                String pollutionAvoidanceValue = preferencesArray.getString("pollution avoidance");
                String weatherValue = preferencesArray.getString("weather");

                String type = "SOURCE -->  ";

                for (int j = 0; j < modesArray.length(); j++) {
                    JSONObject modesJSONObject = modesArray.getJSONObject(j);
                    type += modesJSONObject.getString("type") + "  -->  ";
                }

                type += "DESTINATION";

                String displayString = type + "\n"
                        + "Time: " + Integer.parseInt(time) / 60 + " Minutes \n"
                        + "Distance: " + Float.parseFloat(distance) / 1000 + "Kms \n";

                if(Integer.parseInt(pollutionAvoidanceValue) != 0 )
                {
                    displayString += "Air Quality Index: " + Integer.parseInt(pollutionAvoidanceValue) + "\n";
                }

                if(!weatherValue.equals("")){
                    displayString += "Weather Type: " + weatherValue;
                }

                stringArrayList.add("\n" + rank + ".  " + displayString);
                stringArrayAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ApplySharedPref")
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
                // Then you start a new Activity via Intent
                Intent intent = new Intent();
                intent.setClass(Routes.this, MapsActivity.class);

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("position", position);
                editor.commit();

                ActivityCompat.startActivityForResult(Routes.this, new Intent(Routes.this, MapsActivity.class), 0, null);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String readJSONFromDirectory() {
        String json = null;
        File oriFile = new File(this.getFilesDir(), "sampleCombinedRoutes.json");

        try {
            byte[] buffer;
            try (InputStream is = new FileInputStream(oriFile)) {
                int size = is.available();
                buffer = new byte[size];
                int readSizeInputStream = is.read(buffer);
                Log.d("readSizeInputStream:", String.valueOf(readSizeInputStream));
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
