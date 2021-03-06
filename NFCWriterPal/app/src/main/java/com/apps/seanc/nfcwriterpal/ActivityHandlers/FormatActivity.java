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
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.apps.seanc.nfcwriterpal.R;
import com.apps.seanc.nfcwriterpal.Tools.DialogBoxHandler;

/**
 * Created by seanc on 28/04/2017.
 *
 * This class provides the logic for the format activity.
 * The format activity provides users with option to format a tag to Ndef format or
 * to wipe the tag
 *
 */

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
                                resultText.setText(getString(R.string.format_tag_formatted));
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
                            resultText.setText(getString(R.string.format_not_writable));
                            wipeDialog.dismiss();
                            wipeDialog = null;

                            Log.d(TAG, "Tag is not writeable");
                        } else {

                            Log.d(TAG, "WRITING TO TAG");
                            nfcTag.connect();
                            nfcTag.writeNdefMessage(new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY, null, null, null)));
                            nfcTag.close();
                            vibrate();
                            wipeDialog.dismiss();
                            wipeDialog = null;
                            resultText.setText(getString(R.string.format_tag_wiped));
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_write) {
            Intent writeActivity = new Intent(FormatActivity.this, WriteActivity.class);
            startActivity(writeActivity);
        } else if (id == R.id.nav_read) {
            Intent readActivityIntent = new Intent(FormatActivity.this, ReadActivity.class);
            startActivity(readActivityIntent);
        } else if (id == R.id.nav_format) {
            Intent formatActivity = new Intent(FormatActivity.this, FormatActivity.class);
            startActivity(formatActivity);
        } else if (id == R.id.nav_share) {
            Intent shareActivity = new Intent(FormatActivity.this, ShareActivity.class);
            startActivity(shareActivity);
        } else if (id == R.id.nav_contact) {
            Intent contactActivity = new Intent(FormatActivity.this, ContactActivity.class);
            startActivity(contactActivity);
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
