package com.apps.seanc.nfcwriterpal.ActivityHandlers;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.apps.seanc.nfcwriterpal.ArrayAdapters.ApplicationAdapter;
import com.apps.seanc.nfcwriterpal.Tools.NdefMessageHandler;
import com.apps.seanc.nfcwriterpal.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppSelection extends ListActivity {
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter appAdapter = null;


    private NfcAdapter nfcAdpt;
    private PendingIntent nfcPendingIntent;

    private NdefMessageHandler ndefMessageHandler = null;
    private boolean writeMode = false;
    private static final String TAG = AppSelection.class.getName();
    private String packageName;
    private static AlertDialog writeDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        packageManager = getPackageManager();
        ndefMessageHandler = new NdefMessageHandler();
        nfcAdpt = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        new AppSelection.LoadApplications().execute();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu, menu);

        return true;
    }

    //Write bool expression for if in writeMode
    @Override
    protected void onNewIntent(Intent intent){
        Log.d(TAG, "onNewIntent");
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            Log.d(TAG, "A tag was scanned");

            if(writeMode){
                try{
                    //disableForegroundMode();
                    //dialogBuilder.dismiss();
                    ndefMessageHandler.writeApplication(packageName, intent);
                    vibrate();
                    Intent returnToMain = new Intent(this, MainActivity.class);
                    returnToMain.putExtra("writeStatus",0);
                    writeDialog.dismiss();
                    setResult(RESULT_OK, returnToMain);
                    finish();
                } catch (Exception e){
                    Log.d(TAG, "Exception caught: ", e);
                }
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;

        /*switch (item.getItemId()) {
            case R.id.menu_about: {
                displayAboutDialog();

                break;
            }
            default: {
                result = super.onOptionsItemSelected(item);

                break;
            }
        }*/

        return result;
    }



    private Dialog displayWriteDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.tap_dialog_title));
        dialogBuilder.setMessage(getString(R.string.tap_dialog_text));


        dialogBuilder.setPositiveButton("Cancel!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                writeMode = false;
            }
        });


        return dialogBuilder.create();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ApplicationInfo app = applist.get(position);
        try {
            packageName = app.packageName;
            writeDialog = (AlertDialog) displayWriteDialog();
            writeDialog.show();
            writeMode = true;

        } catch (Exception e) {
            Toast.makeText(AppSelection.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> appList = new ArrayList<>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    appList.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return appList;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));

            Collections.sort(applist, new Comparator<ApplicationInfo>() {
                public int compare(ApplicationInfo appOne, ApplicationInfo appTwo) {
                    String appOneName = "" + appOne.loadLabel(packageManager);
                    String appTwoName = "" + appTwo.loadLabel(packageManager);
                    return appOneName.compareTo(appTwoName);
                }
            });
            appAdapter = new ApplicationAdapter(AppSelection.this,
                    R.layout.snippet_app_list, applist);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            setListAdapter(appAdapter);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(AppSelection.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    public void onPause(){
        Log.d(TAG, "onPause");
        super.onPause();
        disableForegroundMode();
    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();
        enableForeGroundMode();
    }

    @Override
    public void onBackPressed(){
        disableForegroundMode();
        Intent returnToMain = new Intent(this, MainActivity.class);
        setResult(RESULT_OK, returnToMain);
        finish();
    }


    //ForeGround mode gives the current active application priority for reading scanned tags
    public void enableForeGroundMode(){
        Log.d(TAG, "enableForeGroundMode");
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); //Filter for finding tags
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdpt.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters,null);
    }

    //Stops this application from dealing with all tags when paused
    public void disableForegroundMode(){
        Log.d(TAG, "disableForegroundMode");
        nfcAdpt.disableForegroundDispatch(this);
    }


    private void vibrate() {
        Log.d(TAG, "vibrate");

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }
}
