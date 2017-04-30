package com.apps.seanc.nfcwriterpal.Tools;

import android.os.AsyncTask;
import android.util.Log;


import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Created by Sean on 18/03/2016.
 * This controller class is used as a means of running and handling
 * all httpRequests. Each method is linked to a specific button press
 * on the MainAppActivity page.
 */
public class HttpController {

    private String TAG = HttpController.class.getName();
    private apacheTask task = null;
    private okTask okTask = null;
    private timeoutTask timeoutTask = null;

    // Sets request url to the arm php script
    // Creates and executes task with arm script request
    public void apacheHttp(String httpURL){
        task = new apacheTask(httpURL);
        task.execute((Void) null);
    }

    public void okHttp(String url) throws IOException{

        okTask = new okTask(url);
        okTask.execute((Void) null);
    }

    public void timeoutHttp(String url){
        timeoutTask = new timeoutTask(url);
        timeoutTask.execute((Void) null);
    }

    //Class used for executing all httpRequests, extends Asynctask
    private class apacheTask extends AsyncTask<Void, Void, Boolean> {

        String URL;

        //Default constructor for piTask class
        public apacheTask(String URL){
            //Sets request URL
            this.URL = URL;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HttpClient httpclient = new DefaultHttpClient();
            try {
                //Executes request URL
                HttpResponse response = httpclient.execute(new HttpGet(URL));
            }
            catch (Exception e){
                Log.d(TAG, "Error in http connection " + e.toString());
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //Clears task after request has been executed
            task = null;
        }

    }

    //Class used for executing all httpRequests, extends Asynctask
    private class okTask extends AsyncTask<Void, Void, Boolean> {

        String URL;

        //Default constructor for piTask class
        public okTask(String URL){
            //Sets request URL
            this.URL = URL;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(URL)
                    .build();

            try  {
                client.newCall(request).execute();
            } catch (Exception e){
                Log.d(TAG, e.toString());
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //Clears task after request has been executed
            task = null;
        }

    }

    //Class used for executing all httpRequests, extends Asynctask
    private class timeoutTask extends AsyncTask<Void, Void, Boolean> {

        String URL;

        //Default constructor for piTask class
        public timeoutTask(String URL){
            //Sets request URL
            this.URL = URL;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HttpClient client = new DefaultHttpClient();

            int TIMEOUT_MILLISEC = 30000;
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpClient httpclient = new DefaultHttpClient(httpParams);
            HttpPost httppost = new HttpPost(URL);

            try  {
                client.execute(httppost);
            } catch (Exception e){
                Log.d(TAG, e.toString());
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //Clears task after request has been executed
            task = null;
        }

    }



}

