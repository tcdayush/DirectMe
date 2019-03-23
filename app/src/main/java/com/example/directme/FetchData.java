package com.example.directme;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

class FetchData extends AsyncTask<Void, Void, Void> {

    private String data;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(Void... voids) {

        try {
            URL url = new URL("https://ase-server.herokuapp.com/gateway/sendHello");

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            AtomicReference<String> line = null;
            StringBuilder sb = new StringBuilder();
            while (TextUtils.isEmpty(Objects.requireNonNull(line).get())){
                line.set(bufferedReader.readLine());
                sb.append(line.get());
            }
            data = sb.toString();
        } catch (MalformedURLException e) {
            Log.d("MalformedURLException", e.toString());
        } catch (IOException e) {
            Log.d("IOException", e.toString());
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Test1.response.setText(this.data);
    }
}
