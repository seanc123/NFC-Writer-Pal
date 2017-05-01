package com.apps.seanc.nfcwriterpal.ActivityHandlers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.apps.seanc.nfcwriterpal.R;
import com.apps.seanc.nfcwriterpal.Tools.DialogBoxHandler;

public class FormatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private NfcAdapter nfcAdpt;
    private PendingIntent nfcPendingIntent;

    private DialogBoxHandler dialogBoxHandler;
    private String TAG = FormatActivity.class.getName();

    private Button formatButton;
    private AlertDialog formatDialog, unformattableDialog, wipeDialog;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_format);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nfcAdpt = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        dialogBoxHandler = new DialogBoxHandler(FormatActivity.this, this);

        formatButton = (Button) findViewById(R.id.frmt_btn_format);
        formatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultText.setText("");
                formatDialog = dialogBoxHandler.tapTagDialog();
                formatDialog.show();
            }
        });

        resultText = (TextView) findViewById(R.id.frmt_tv_result);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        if(formatDialog!= null) {
            if (formatDialog.isShowing()) {
                if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    NdefFormatable formatable = NdefFormatable.get(tag);
                    NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{
                            new NdefRecord(NdefRecord.TNF_EMPTY, null, null, null)});

                    if (formatable != null) {
                        try {
                            formatable.connect();

                            try {
                                formatable.format(ndefMessage);
                                resultText.setText("Tag formatted as NDEF");
                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                                Log.d(TAG, "Unable to format tag");
                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                            Log.d(TAG, "Unable to connect to tag");
                        } finally {
                            try {
                                formatable.close();
                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                                Log.d(TAG, "Unable to close tag");
                            }
                        }
                    } else {
                        Log.d(TAG, "Tag not formattable");
                        formatDialog.dismiss();
                        formatDialog = null;
                        unformattableDialog = dialogBoxHandler.unformattableDialog();
                        unformattableDialog.show();
                    }
                }
            }
        } else if(wipeDialog!= null){
            if(wipeDialog.isShowing()){
                Log.d(TAG, "WIPE DIALOG SHOWING");
                if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    Ndef nfcTag = Ndef.get(tag);
                    Log.d(TAG, "TAG RETRIEVED");

                    try {
                        if (!nfcTag.isWritable()) {
                            resultText.setText("Tag is not writable");
                            wipeDialog.dismiss();
                            wipeDialog = null;

                            Log.d(TAG, "Tag is not writeable");
                        } else {

                            Log.d(TAG, "WRITING TO TAG");
                            nfcTag.connect();
                            nfcTag.writeNdefMessage(new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY, null, null, null)));
                            nfcTag.close();
                            wipeDialog.dismiss();
                            wipeDialog = null;
                            resultText.setText("Tag wiped");
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }
            }
        }
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
        //getMenuInflater().inflate(R.menu.format, menu);
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

    public void showWipeDialog(){
        wipeDialog = dialogBoxHandler.tapTagDialog();
        wipeDialog.show();
        Log.d(TAG, "WIPE DIALOG SHOWING");
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

    private void vibrate() {
        Log.d(TAG, "vibrate");
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }
}
