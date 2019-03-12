package com.example.directme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@SuppressLint("All")
public class routes extends Activity {

    public  TextView response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routes);

        response = (findViewById(R.id.response));
        Button click = (findViewById(R.id.click));

        try
        {
            String jsonFileContent = readFile("json_in_assets.json");
            JSONArray jsonArray = new JSONArray(jsonFileContent);

            JSONObject jsonObj = jsonArray.getJSONObject(0);


        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"JSON Not Found",Toast.LENGTH_LONG).show();
        }


        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    public String readFile(String fileName) throws IOException
    {
        BufferedReader reader  = new BufferedReader(new InputStreamReader(getAssets().open(fileName), StandardCharsets.UTF_8));

        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
        {
            content.append(line);
        }

        return content.toString();

    }
}
