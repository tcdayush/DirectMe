package com.example.directme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;


public class Preferences extends Activity {

    TextView jsonFromServer;

    CheckBox pollutionAvoidance;
    CheckBox weather;
    CheckBox reliability;
    CheckBox comfort;
    CheckBox trafficAvoidance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        pollutionAvoidance = (findViewById(R.id.checkbox_pollution));
        weather = (findViewById(R.id.checkbox_weather));
        reliability = (findViewById(R.id.checkbox_reliability));
        comfort = (findViewById(R.id.checkbox_comfort));
        trafficAvoidance = (findViewById(R.id.checkbox_traffic_congestion));

        jsonFromServer = (findViewById(R.id.json_from_server));
        Button savePreferences = (findViewById(R.id.save_preferences));
         savePreferences.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 Preferences obj = new Preferences();
                 JSONObject  jsonObject = obj.makeJSONObject(pollutionAvoidance.isChecked(),weather.isChecked(),reliability.isChecked(),comfort.isChecked(),trafficAvoidance.isChecked());

                 GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Preferences.this);
                 String personId = account.getId();

                 try {
                     File directory = getFilesDir();
                     File file = new File(directory,"Preferences_" + personId + ".json");
                     Writer output = new BufferedWriter(new FileWriter(file));
                     output.write(jsonObject.toString());
                     output.close();
                     //Toast.makeText(getApplicationContext(), "Preferences saved" , Toast.LENGTH_LONG).show();
                     Toast.makeText(getApplicationContext(), jsonObject.toString() , Toast.LENGTH_LONG).show();

                 } catch (Exception e) {
                     Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                 }
                 Intent intent = new Intent(Preferences.this, MapsActivity.class);
                 startActivity(intent);
             }
         });


    }

    public JSONObject makeJSONObject(Boolean pollutionAvoidance, Boolean weather, Boolean reliability, Boolean comfort, Boolean trafficAvoidance) {

        JSONObject obj = new JSONObject() ;

        try {
            obj.put("pollutionAvoidance", pollutionAvoidance);
            obj.put("weather", weather);
            obj.put("reliability", reliability);
            obj.put("comfort", comfort);
            obj.put("trafficAvoidance", trafficAvoidance);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }
}