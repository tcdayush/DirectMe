package com.example.directme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class Preferences extends Activity {

    TextView jsonFromServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        jsonFromServer = (findViewById(R.id.json_from_server));
        Button savePreferences = (findViewById(R.id.save_preferences));
         savePreferences.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(Preferences.this, MainActivity.class);
                 startActivity(intent);
             }
         });


    }
}