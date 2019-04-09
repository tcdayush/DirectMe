package com.example.directme;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class SendUserLoginRequestToServer extends AsyncTask<String, Void, String> {

    private String postData = "";
    private ProgressDialog pDialog;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    SendUserLoginRequestToServer(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Please wait..");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public String doInBackground(String... params) {

        HttpURLConnection httpConnection = null;

        try {

            httpConnection = (HttpURLConnection) new URL(params[0]).openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            httpConnection.setRequestProperty("Accept", "application/json");

            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);

            OutputStream os = httpConnection.getOutputStream();
            os.write(params[1].getBytes());
            os.flush();

            // handle error response code it occurs
            int responseCode = httpConnection.getResponseCode();
            InputStream inputStream;
            if (200 <= responseCode && responseCode <= 299) {
                inputStream = httpConnection.getInputStream();
            } else {
                inputStream = httpConnection.getErrorStream();
            }

            StringBuilder response;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            inputStream))) {

                response = new StringBuilder();
                String currentLine;

                while ((currentLine = in.readLine()) != null)
                    response.append(currentLine);

            }
            postData = response.toString();
            httpConnection.disconnect();
        } catch (IOException e) {
            Log.d("Exception", e.toString());
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return postData;
    }

    @Override
    public void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null) {
            try {
                Toast.makeText(mContext,postData,Toast.LENGTH_LONG).show();
                Log.d("onPostExecute: Success ",result);

            } catch (Exception e) {
                Log.d("onPostExecute: Failed", e.toString());
            }
        }
        pDialog.dismiss();
    }

}


