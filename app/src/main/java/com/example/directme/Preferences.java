package com.example.directme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        assert account != null;
        final String personId = account.getId();

        try {
            loadPreferences(personId);
        } catch (JSONException e) {
            Log.d("JSONException", e.toString());
        }


        Button savePreferences = (findViewById(R.id.save_preferences));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            savePreferences.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     JSONObject  jsonObject = makeJSONObject(pollutionAvoidance.isChecked(),weather.isChecked(),reliability.isChecked(),comfort.isChecked(),trafficAvoidance.isChecked());

                     try {
                         File directory = getFilesDir();
                         File file = new File(directory,"Preferences_" + personId + ".json");
                         try (Writer output = new BufferedWriter(new FileWriter(file))) {
                             output.write(jsonObject.toString());
                         }

                         //TODO: Send Database to server (Based on specification)
                         //new SendPostRequest().execute("http://10.6.57.183:9090/pref", jsonObject.toString());

                         Toast.makeText(getApplicationContext(), "Preferences saved" , Toast.LENGTH_LONG).show();
                         Intent intent = new Intent(Preferences.this, MapsActivity.class);
                         startActivity(intent);

                     } catch (Exception e) {
                         Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                     }
                 }
             });
        }


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
            Log.d("JSONException", e.toString());
        }
        return obj;
    }



    public void loadPreferences(String personId) throws JSONException {

        try
        {
            File directory = getFilesDir();
            File file = new File(directory,"Preferences_" + personId + ".json");

            if(file.exists())
            {
                JSONObject jsonObject = parseJSONFile(file.getAbsolutePath());

                pollutionAvoidance.setChecked(jsonObject.getBoolean("pollutionAvoidance"));
                weather.setChecked(jsonObject.getBoolean("weather"));
                reliability.setChecked(jsonObject.getBoolean("reliability"));
                comfort.setChecked(jsonObject.getBoolean("comfort"));
                trafficAvoidance.setChecked(jsonObject.getBoolean("trafficAvoidance"));

                Toast.makeText(getApplicationContext(), "Preferences Loaded" , Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Please select your preferences" , Toast.LENGTH_SHORT).show();
            }

        }catch (Exception  e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }


    public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {
        String content = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            content = new String(Files.readAllBytes(Paths.get(filename)));
        }
        return new JSONObject(content);
    }
}