package com.example.directme;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class SendPostRequest extends AsyncTask<String, Void, String> {
    
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public String doInBackground(String... params) {

        String postData = "";
        HttpURLConnection httpConnection = null;
        
        try {

            httpConnection= (HttpURLConnection) new URL(params[0]).openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            httpConnection.setDoOutput(true);

            DataOutputStream outputStream= new DataOutputStream(httpConnection.getOutputStream());
            outputStream.writeBytes(params[1]);
            outputStream.flush();
            outputStream.close();

            InputStreamReader inputStreamReader;
            try (InputStream in = httpConnection.getInputStream()) {
                inputStreamReader = new InputStreamReader(in);
            }
            StringBuilder sb = new StringBuilder();
            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char currentData = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                sb.append(currentData);
            }
            postData = sb.toString();
            httpConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpConnection!= null) {
                httpConnection.disconnect();
            }
        }
        return postData;
    }

    @Override
    public void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}