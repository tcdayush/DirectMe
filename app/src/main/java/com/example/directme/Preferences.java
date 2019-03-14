package com.example.directme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class Preferences extends Activity {

    private CheckBox pollutionAvoidance;
    private CheckBox weather;
    private CheckBox reliability;
    private CheckBox comfort;
    private CheckBox trafficAvoidance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        pollutionAvoidance = (findViewById(R.id.checkbox_pollution));
        weather = (findViewById(R.id.checkbox_weather));
        reliability = (findViewById(R.id.checkbox_reliability));
        comfort = (findViewById(R.id.checkbox_comfort));
        trafficAvoidance = (findViewById(R.id.checkbox_traffic_congestion));

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Preferences.this);
        final String personId = account.getId();

        //Put on hold
        /*try {
            loadPreferences();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        Button savePreferences = (findViewById(R.id.save_preferences));
        savePreferences.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 //Preferences obj = new Preferences();
                 JSONObject  jsonObject = makeJSONObject(pollutionAvoidance.isChecked(),weather.isChecked(),reliability.isChecked(),comfort.isChecked(),trafficAvoidance.isChecked());



                 try {
                     File directory = getFilesDir();
                     File file = new File(directory,"Preferences_" + personId + ".json");
                     Writer output = new BufferedWriter(new FileWriter(file));
                     output.write(jsonObject.toString());
                     output.close();

                     //Toast to Check local save
                     Context ctx = getApplicationContext();
                     FileInputStream fileInputStream = ctx.openFileInput("Preferences_" + personId + ".json");
                     InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                     BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                     String lineData = bufferedReader.readLine();
                     Toast.makeText(getApplicationContext(), lineData , Toast.LENGTH_LONG).show();
                     //Toast.makeText(getApplicationContext(), "Preferences saved" , Toast.LENGTH_LONG).show();

                     new SendPostRequest().execute("http://10.6.57.183:9090/pref", jsonObject.toString());
                     //Toast.makeText(getApplicationContext(), "Preferences saved in Global DB" , Toast.LENGTH_LONG).show();

                     Intent intent = new Intent(Preferences.this, MapsActivity.class);
                     startActivity(intent);

                     /*JSONObject reader = new JSONObject(file.toString());
                     String pollutionAvoidanceValue = reader.getString("pollutionAvoidance");
                     Toast.makeText(getApplicationContext(), pollutionAvoidanceValue , Toast.LENGTH_LONG).show();*/

                 } catch (Exception e) {
                     Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                 }
             }
         });


    }

    private JSONObject makeJSONObject(Boolean pollutionAvoidance, Boolean weather, Boolean reliability, Boolean comfort, Boolean trafficAvoidance)
    {
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



    /*public void loadPreferences() throws JSONException {

        Toast.makeText(getApplicationContext(), "Test 1" , Toast.LENGTH_LONG).show();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Preferences.this);
        final String personId = account.getId();

        File directory = getFilesDir();
        File file = new File(directory,"Preferences_" + personId + ".json");

        JSONObject reader = new JSONObject(file.toString());
        Toast.makeText(getApplicationContext(), "test loop" , Toast.LENGTH_LONG).show();

        Boolean pollutionAvoidanceValue = reader.getBoolean("pollutionAvoidance");
        pollutionAvoidance.setChecked(pollutionAvoidanceValue);
    }*/


}