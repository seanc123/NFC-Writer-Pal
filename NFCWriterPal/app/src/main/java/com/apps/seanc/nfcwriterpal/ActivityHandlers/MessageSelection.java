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

import java.nio.charset.Charset;

public class MessageSelection extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MessageSelection.class.getName();

    private Button webpage, text, email, phone, location, app;
    private static AlertDialog dialogBox;
    private static String uriString;
    private static String mimeString;
    private ApplicationInfo appInfo;

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

        setOnClickListeners();
    }

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
        uriString = uri;
    }

    public void setMimeString(String mime){
        mimeString = mime;
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
                break;
            case 3:
                try{
                    String mimeString = "http://192.168.0.241/functions/disarm.php";
                    NdefRecord appRecord = new NdefRecord(
                            NdefRecord.TNF_MIME_MEDIA ,
                            "application/com.apps.seanc.nfcwriterpal".getBytes(Charset.forName("US-ASCII")),
                            new byte[0], mimeString.getBytes(Charset.forName("US-ASCII")));
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
        }
    }

    @Override
    public void onBackPressed(){

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

            Intent returnToMain = new Intent(this, MainActivity.class);
            setResult(RESULT_CANCELED, returnToMain);
        }

    }


}
