package com.apps.seanc.nfcwriterpal.ActivityHandlers;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;

import com.apps.seanc.nfcwriterpal.R;
import com.apps.seanc.nfcwriterpal.Tools.DialogBoxHandler;
import com.apps.seanc.nfcwriterpal.Tools.NdefRecordParcel;

import java.nio.charset.Charset;

public class RecordSelection extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = RecordSelection.class.getName();

    private Button webpage, text, email, phone, location, app, hms;
    private static String uriString;
    private static String mimeString;
    private ApplicationInfo appInfo;

    private DialogBoxHandler dialogBoxHandler;

/*
 * Created by seanc on 20/04/2017.
 *
 *  This class is used for providing the user with the various NdefRecord that can be create by this app
 *  When a user selects an option, a dialog is created and the data passed to this activity from the
 *  dialog determines what the Record will do
 *
 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_selection);
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
        hms = (Button) findViewById(R.id.ms_btn_hms);

        dialogBoxHandler = new DialogBoxHandler(RecordSelection.this, this);

        setOnClickListeners();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_write) {
            Intent writeActivity = new Intent(RecordSelection.this, WriteActivity.class);
            startActivity(writeActivity);
        } else if (id == R.id.nav_read) {
            Intent readActivityIntent = new Intent(RecordSelection.this, ReadActivity.class);
            startActivity(readActivityIntent);
        } else if (id == R.id.nav_format) {
            Intent formatActivity = new Intent(RecordSelection.this, FormatActivity.class);
            startActivity(formatActivity);
        } else if (id == R.id.nav_share) {
            Intent shareActivity = new Intent(RecordSelection.this, ShareActivity.class);
            startActivity(shareActivity);
        } else if (id == R.id.nav_contact) {
            Intent contactActivity = new Intent(RecordSelection.this, ContactActivity.class);
            startActivity(contactActivity);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setOnClickListeners(){

        webpage.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                dialogBoxHandler.webpageDialog();
            }
        });

        phone.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                dialogBoxHandler.phoneCallDialog();
            }
        });

        text.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                dialogBoxHandler.smsDialog();
            }
        });

        email.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                dialogBoxHandler.emailDialog();
            }
        });

        location.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                dialogBoxHandler.locationDialog();
            }
        });

        app.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                dialogBoxHandler.appDialog();
            }
        });

        hms.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                dialogBoxHandler.hmsDialog();
            }
        });

    }

    // Called form a dialog when the option selected will be a UriRecord
    public void setUriString(String uri){
        uriString = uri;
    }

    // Called form a dialog when the option selected will be a MimeRecord
    public void setMimeString(String mime){
        mimeString = mime;
    }

    public void setAppInfo(ApplicationInfo appInfo){
        this.appInfo = appInfo;
    }

    // Called by a dialog when the user is finished inputting data regarding the record.
    // The argument passed into this method notifies this class what type of record is to be created
    public void returnWithResult(int recordType){
        switch (recordType) {
            case 1:
                try {
                    Uri uri = Uri.parse(uriString);
                    NdefRecord ndefRecord = NdefRecord.createUri(uri);
                    NdefRecordParcel ndefRecordParcel = new NdefRecordParcel(ndefRecord);

                    Intent returnToWrite = new Intent(this, MainActivity.class);
                    returnToWrite.putExtra("ndefRecord", ndefRecordParcel);
                    setResult(RESULT_OK, returnToWrite);

                    finish();

                } catch (Exception e) {
                    Log.d(TAG, "Exception caught: ", e);
                }
                break;
            case 2:
                try{
                    NdefRecord ndefRecord = NdefRecord.createApplicationRecord(appInfo.packageName);
                    NdefRecordParcel ndefRecordParcel = new NdefRecordParcel(ndefRecord);
                    Intent returnToWrite = new Intent(this, MainActivity.class);
                    returnToWrite.putExtra("ndefRecord", ndefRecordParcel);
                    setResult(RESULT_OK, returnToWrite);
                    finish();
                } catch (Exception e){
                    Log.d(TAG, e.toString());
                }
                break;
            case 3:
                try{
                    NdefRecord mimeRecord = new NdefRecord(
                            NdefRecord.TNF_MIME_MEDIA ,
                            "application/com.apps.seanc.nfcwriterpal".getBytes(Charset.forName("US-ASCII")),
                            new byte[0], mimeString.getBytes(Charset.forName("US-ASCII")));

                    NdefRecordParcel ndefRecordParcel = new NdefRecordParcel(mimeRecord);

                    Intent returnToWrite = new Intent(this, MainActivity.class);
                    returnToWrite.putExtra("ndefRecord", ndefRecordParcel);
                    setResult(RESULT_OK, returnToWrite);

                    finish();
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
