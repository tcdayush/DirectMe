package com.example.directme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Launcher Activity: This activity will launch whenever the app starts
 */

public class MainActivity extends AppCompatActivity {

    ImageView appLogo;
    TextView appName;
    Button sign_in;
    TextView sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appLogo = (findViewById(R.id.app_logo));
        appName = (findViewById(R.id.app_name));
        sign_in = (findViewById(R.id.sign_in));
        sign_up = (findViewById(R.id.sign_up_text));

        //listener to the sign up text

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Listener Working", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, Registration_form.class);
                startActivity(intent);
            }
        });


        //listener to the sign in button

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Preferences.class);
                startActivity(intent);
            }
        });
    }
}
