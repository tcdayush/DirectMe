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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

class SendPostRequest extends AsyncTask<String, Void, String> {

    private ProgressDialog pDialog;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    SendPostRequest(Context context)
    {
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

        String postData = "";
        HttpURLConnection httpConnection = null;

        try {

            httpConnection = (HttpURLConnection) new URL(params[0]).openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            httpConnection.setDoOutput(true);

            // handle error response code it occurs
            int responseCode = httpConnection.getResponseCode();
            InputStream inputStream;
            if (200 <= responseCode && responseCode <= 299) {
                inputStream = httpConnection.getInputStream();
            } else {
                inputStream = httpConnection.getErrorStream();
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            inputStream));

            StringBuilder response = new StringBuilder();
            String currentLine;

            while ((currentLine = in.readLine()) != null)
                response.append(currentLine);

            in.close();
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
        if(result!=null)
        {
            try {
                writeToFile(result,mContext);
                String toastMessage = readFromFile(mContext);
                Toast.makeText(mContext,toastMessage,Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.d("onPostExecute: ",e.toString());
            }
        }
        pDialog.dismiss();
    }

    private void writeToFile(String data,Context context) throws IOException {

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("Routes.json", Context.MODE_PRIVATE));
        outputStreamWriter.write(data);
        outputStreamWriter.close();
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("Routes.json");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}


