package com.apps.seanc.nfcwriterpal.ActivityHandlers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;

import org.ndeftools.Message;
import org.ndeftools.Record;

import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.seanc.nfcwriterpal.Tools.NFCHelper;
import com.apps.seanc.nfcwriterpal.Tools.NdefMessageHandler;
import com.apps.seanc.nfcwriterpal.R;

import java.nio.charset.Charset;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getName();


    private NfcAdapter nfcAdpt;
    private PendingIntent nfcPendingIntent;

    private Button writeButton, readButton, formatButton, shareButton, contactButton;
    private NdefMessageHandler ndefMessageHandler;

    private NFCHelper nfcHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ndefMessageHandler = new NdefMessageHandler();
        nfcAdpt = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        nfcHelper = new NFCHelper();

        writeButton = (Button) findViewById(R.id.writeButton);
        readButton = (Button) findViewById(R.id.readButton);
        formatButton = (Button) findViewById(R.id.main_btn_format);
        shareButton = (Button) findViewById(R.id.main_btn_share);
        contactButton = (Button) findViewById(R.id.main_btn_contact);
        setOnClickListeners();
        
    }


    @Override
    protected void onNewIntent(Intent intent){
        Log.d(TAG, "onNewIntent");
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef nfcTag = Ndef.get(tag);
            Log.d(TAG, "A tag was scanned");
            vibrate();
            String mimeString = "http://192.168.0.241/functions/disarm.php";
            NdefRecord appRecord = new NdefRecord(
                    NdefRecord.TNF_MIME_MEDIA ,
                    "application/com.apps.seanc.nfcwriterpal".getBytes(Charset.forName("US-ASCII")),
                    new byte[0], mimeString.getBytes(Charset.forName("US-ASCII")));

//            NdefRecord[] ndefRecords = new NdefRecord[2];
//            byte[] version = new byte[] { (0x1 << 4) | (0x2)};
//            ndefRecords[0] = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_HANDOVER_REQUEST, new byte[0], version);
//            // and then obviously add the record you create with the method below.
//            ndefRecords[1] = nfcHelper.createWifiRecord()


            NdefMessage message = new NdefMessage(new NdefRecord[] { appRecord });
            try {
                if(!nfcTag.isWritable()){

                    Log.d(TAG, "Tag is not writeable");

                } else if(nfcTag.getMaxSize() < message.toByteArray().length){

                    Log.d(TAG, "Tag size is too small, have " + nfcTag.getMaxSize() + ", need " + message.toByteArray().length);

                } else{

                    nfcTag.connect();
                    nfcTag.writeNdefMessage(message);
                    nfcTag.close();
                    Toast.makeText(MainActivity.this, "Message written!", Toast.LENGTH_LONG).show();

                }


            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }

        }
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_with_navigation, menu);
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
            Intent writeActivity = new Intent(MainActivity.this, WriteActivity.class);
            startActivity(writeActivity);
        } else if (id == R.id.nav_read) {
            Intent readActivityIntent = new Intent(MainActivity.this, ReadActivity.class);
            startActivity(readActivityIntent);
        } else if (id == R.id.nav_format) {
            Intent formatActivity = new Intent(MainActivity.this, FormatActivity.class);
            startActivity(formatActivity);
        } else if (id == R.id.nav_share) {
            Intent shareActivity = new Intent(MainActivity.this, ShareActivity.class);
            startActivity(shareActivity);
        } else if (id == R.id.nav_contact) {
            Intent contactActivity = new Intent(MainActivity.this, ContactActivity.class);
            startActivity(contactActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void vibrate() {
        Log.d(TAG, "vibrate");

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }

    private void setOnClickListeners(){

        writeButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                Intent writeActivity = new Intent(MainActivity.this, WriteActivity.class);
                startActivity(writeActivity);
                //startActivityForResult(intent, 1);
            }
        });

        readButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                Intent readActivityIntent = new Intent(MainActivity.this, ReadActivity.class);
                startActivity(readActivityIntent);
            }

        });

        formatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent formatActivity = new Intent(MainActivity.this, FormatActivity.class);
                startActivity(formatActivity);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareActivity = new Intent(MainActivity.this, ShareActivity.class);
                startActivity(shareActivity);
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactActivity = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(contactActivity);
            }
        });

    }



}
