package com.example.directme;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendPostRequest extends AsyncTask<String, Void, String> {
    
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

            InputStream in = httpConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char currentData = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                postData += currentData;
            }
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