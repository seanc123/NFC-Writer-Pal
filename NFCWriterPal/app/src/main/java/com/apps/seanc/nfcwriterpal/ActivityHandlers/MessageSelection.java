package com.apps.seanc.nfcwriterpal.ActivityHandlers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.apps.seanc.nfcwriterpal.R;
import com.apps.seanc.nfcwriterpal.Tools.DialogBoxHandler;
import com.apps.seanc.nfcwriterpal.Tools.NdefRecordParcel;

public class MessageSelection extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MessageSelection.class.getName();

    private Button webpage, text, email, phone, location, app;
    private static AlertDialog dialogBox;
    private static String uriString;
    private ApplicationInfo appInfo;

    private boolean writeMode;

    private DialogBoxHandler dialogBoxHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        webpage = (Button) findViewById(R.id.ms_btn_webpage);
        text = (Button) findViewById(R.id.ms_btn_text);
        phone = (Button) findViewById(R.id.ms_btn_phone);
        email = (Button) findViewById(R.id.ms_btn_email);
        location = (Button) findViewById(R.id.ms_btn_location);
        app = (Button) findViewById(R.id.ms_btn_app);

        dialogBoxHandler = new DialogBoxHandler(MessageSelection.this, this);

        writeMode = false;
        setOnClickListeners();
        //parcel = Parcel.obtain();
    }

    /*@Override
    protected void onNewIntent(Intent intent){
        Log.d(TAG, "onNewIntent");
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            Log.d(TAG, "A tag was scanned");

            if(writeMode){
                try{
                    vibrate();


                    Tag mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    Ndef ndef = Ndef.get(mytag);

                    Uri uri = Uri.parse(uriString);
                    NdefRecord ndefRecord = NdefRecord.createUri(uri);

                    NdefRecordParcel ndefRecordParcel = new NdefRecordParcel(ndefRecord);


                    /*NdefMessage message = new NdefMessage(recordNFC );

                    ndef.connect();
                    ndef.writeNdefMessage(message);
                    ndef.close();


                    Intent returnToMain = new Intent(this, MainActivity.class);
                    intent.putExtra("ndefRecord", ndefRecordParcel);
                    intent.putExtra("recordType", recordType);
                    setResult(RESULT_OK, returnToMain);
                    dialogBox.dismiss();
                    finish();

                } catch (Exception e){
                    Log.d(TAG, "Exception caught: ", e);
                }
            }
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.message_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_write) {
            // Handle the camera action
        } else if (id == R.id.nav_read) {

        } else if (id == R.id.nav_format) {

        } else if (id == R.id.nav_saved_functions) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_contact) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setOnClickListeners(){

        webpage.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                dialogBox = (AlertDialog) dialogBoxHandler.webpageDialog();
                dialogBox.show();
            }
        });

        phone.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                dialogBox = (AlertDialog) dialogBoxHandler.phoneCallDialog();
                dialogBox.show();
            }
        });

        text.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                dialogBox = (AlertDialog) dialogBoxHandler.smsDialog();
                dialogBox.show();
            }
        });

        email.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                dialogBox = (AlertDialog) dialogBoxHandler.emailDialog();
                dialogBox.show();
            }
        });

        location.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                dialogBox = (AlertDialog) dialogBoxHandler.locationDialog();
                dialogBox.show();
            }
        });

        app.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                dialogBox = (AlertDialog) dialogBoxHandler.appDialog();
                dialogBox.show();
            }
        });

    }

    public void setUriString(String uri){
        uriString=uri;
    }

    public void setAppInfo(ApplicationInfo appInfo){
        this.appInfo = appInfo;
    }

    public void returnWithResult(int recordType){
        switch (recordType) {
            case 1:
                try {
                    Uri uri = Uri.parse(uriString);
                    NdefRecord ndefRecord = NdefRecord.createUri(uri);
                    NdefRecordParcel ndefRecordParcel = new NdefRecordParcel(ndefRecord);

                    Intent returnToMain = new Intent(this, MainActivity.class);
                    returnToMain.putExtra("ndefRecord", ndefRecordParcel);
                    setResult(RESULT_OK, returnToMain);

                    dialogBox.dismiss();
                    finish();

                } catch (Exception e) {
                    Log.d(TAG, "Exception caught: ", e);
                }
                break;
            case 2:
                try{
                    NdefRecord ndefRecord = NdefRecord.createApplicationRecord(appInfo.packageName);
                    NdefRecordParcel ndefRecordParcel = new NdefRecordParcel(ndefRecord);Intent returnToMain = new Intent(this, MainActivity.class);
                    returnToMain.putExtra("ndefRecord", ndefRecordParcel);
                    setResult(RESULT_OK, returnToMain);
                    dialogBox.dismiss();
                    finish();
                } catch (Exception e){
                    Log.d(TAG, e.toString());
                }
        }
    }

    @Override
    public void onPause(){
        Log.d(TAG, "onPause");
        super.onPause();
        //disableForegroundMode();
    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();
        //enableForeGroundMode();
    }

    @Override
    public void onBackPressed(){

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //disableForegroundMode();
            super.onBackPressed();

            Intent returnToMain = new Intent(this, MainActivity.class);
            setResult(RESULT_CANCELED, returnToMain);
        }

    }


    /*//ForeGround mode gives the current active application priority for reading scanned tags
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
    }*/


    private void vibrate() {
        Log.d(TAG, "vibrate");

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }

}
