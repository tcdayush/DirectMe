package com.example.directme;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

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

        try
        {
            JSONObject obj = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                obj = new JSONObject(readJSONFromAsset());
            }

            assert obj != null;
            JSONArray countries=obj.getJSONArray("Routes");
            for (int i=0;i<countries.length();i++){
                JSONObject jsonObject=countries.getJSONObject(i);
                String rank=jsonObject.getString("Rank");
                String modes=jsonObject.getString("Modes");
                String time=jsonObject.getString("Time");
                String distance=jsonObject.getString("Distance");
                stringArrayList.add(rank + " \t" + modes + " \t" +time + " \t" +distance);
                stringArrayAdapter.notifyDataSetChanged();
            }


        }catch (Exception e){
            Log.d("Exception", e.toString());
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String readJSONFromAsset() {
        String json = null;
        try {
            byte[] buffer;
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
