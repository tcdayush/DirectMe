package com.example.directme;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Launcher Activity: This activity will launch whenever the app starts
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "My Tag";
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signInButton = (findViewById(R.id.sign_in));
        TextView signUpTextView = (findViewById(R.id.sign_up_text));

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //listener to the sign up text
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Listener Working", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, RegistrationForm.class);
                startActivity(intent);
            }
        });

        //listener to the sign in button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.sign_in:
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                        break;

                    default:
                        Toast.makeText(getApplicationContext(), "Default Switch Case Reached",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void pushUserDetailsToServer(GoogleSignInAccount account) {
        try {
            String googleId = account.getId();
            String name = account.getGivenName();
            String emailId = account.getEmail();

            MainActivity mainActivity = new MainActivity();
            JSONObject jsonObject = mainActivity.makeJSONObject(googleId, name, emailId);

            new SendUserLoginRequestToServer(MainActivity.this).execute("http://10.6.57.183:8185/addNewUser/"
                    , jsonObject.toString());
            Log.d("User:", jsonObject.toString());
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        // If account returns value, continue with next activity
        if (account != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                pushUserDetailsToServer(account);
            }
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        }
        // If account returns null -> Show Sign in Page
        if (account == null) {
            Toast.makeText(getApplicationContext(), "Not Signed In. Please Login", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private JSONObject makeJSONObject(String googleId, String name, String emailId) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("googleId", googleId);
            obj.put("name", name);
            obj.put("emailId", emailId);
        } catch (JSONException e) {
            Log.d("JSONException", e.toString());
        }
        return obj;
    }

    @Override
    protected void onDestroy() {
        File file = new File(getFilesDir(), "sampleCombinedRoutes.json");
        if (file.exists()) {
            boolean fileDeleteStatus = file.delete();
            Log.d(TAG, "fileDeleteStatus: " + fileDeleteStatus);
        }
        super.onDestroy();
    }
}