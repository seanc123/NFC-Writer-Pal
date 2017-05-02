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
import android.widget.ListView;
import android.widget.Toast;

import com.apps.seanc.nfcwriterpal.ArrayAdapters.WriteListAdapter;
import com.apps.seanc.nfcwriterpal.R;
import com.apps.seanc.nfcwriterpal.Tools.DialogBoxHandler;
import com.apps.seanc.nfcwriterpal.Tools.NdefMessageHandler;
import com.apps.seanc.nfcwriterpal.Tools.NdefRecordParcel;

import java.util.ArrayList;
import java.util.List;

public class WriteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private NfcAdapter nfcAdpt;
    private PendingIntent nfcPendingIntent;

    private ListView recordsToWriteList;
    private Button addMessageBtn, removeBtn, writeBtn;
    private String TAG = MessageSelection.class.getName();
    private List<NdefRecord> recordList;
    private AlertDialog writeDialog;
    private WriteListAdapter writeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
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

        recordsToWriteList = (ListView) findViewById(R.id.write_listv_records);
        addMessageBtn = (Button) findViewById(R.id.write_btn_new_msg);
        writeBtn = (Button) findViewById(R.id.write_btn_write);
        removeBtn = (Button) findViewById(R.id.write_btn_remove);
        recordList = new ArrayList<NdefRecord>();

        setOnClickListeners();
    }

    @Override
    protected void onNewIntent(Intent intent){
        Log.d(TAG, "onNewIntent");
        if(writeDialog!=null) {
            if (writeDialog.isShowing()) {
                if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    Ndef nfcTag = Ndef.get(tag);
                    NdefRecord[] ndefRecords = recordList.toArray(new NdefRecord[0]);
                    NdefMessage ndefMessage = new NdefMessage(ndefRecords);
                    try {
                        if (!nfcTag.isWritable()) {

                            Log.d(TAG, "Tag is not writeable");

                        } else if (nfcTag.getMaxSize() < ndefMessage.toByteArray().length) {

                            Log.d(TAG, "Tag size is too small, have " + nfcTag.getMaxSize() + ", need " + ndefMessage.toByteArray().length);

                        } else {

                            nfcTag.connect();
                            nfcTag.writeNdefMessage(ndefMessage);
                            nfcTag.close();
                            writeDialog.dismiss();
                            Toast.makeText(WriteActivity.this, "Message written!", Toast.LENGTH_LONG).show();

                        }


                    } catch (Exception e) {
                        Toast.makeText(WriteActivity.this, "Message write failed!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, e.toString());
                    }
                }
            }
        }
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
            Intent writeActivity = new Intent(WriteActivity.this, WriteActivity.class);
            startActivity(writeActivity);
        } else if (id == R.id.nav_read) {
            Intent readActivityIntent = new Intent(WriteActivity.this, ReadActivity.class);
            startActivity(readActivityIntent);
        } else if (id == R.id.nav_format) {
            Intent formatActivity = new Intent(WriteActivity.this, FormatActivity.class);
            startActivity(formatActivity);
        } else if (id == R.id.nav_share) {
            Intent shareActivity = new Intent(WriteActivity.this, ShareActivity.class);
            startActivity(shareActivity);
        } else if (id == R.id.nav_contact) {
            Intent contactActivity = new Intent(WriteActivity.this, ContactActivity.class);
            startActivity(contactActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setOnClickListeners() {

        addMessageBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent messageSelectionIntent = new Intent(WriteActivity.this, MessageSelection.class);
                startActivityForResult(messageSelectionIntent, 1);
            }
        });

        writeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DialogBoxHandler dialogHandler = new DialogBoxHandler(WriteActivity.this);
                writeDialog = dialogHandler.tapTagDialog();
                writeDialog.show();
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordList = new ArrayList<NdefRecord>();
                writeListAdapter = new WriteListAdapter(WriteActivity.this, R.layout.snippet_write_list, recordList);
                recordsToWriteList.setAdapter(writeListAdapter);
            }
        });
    }

    @Override //collects any data sent from the settings screen and applies it accordingly
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent){
        super.onActivityResult(requestCode, resultCode, resultIntent);

        if(resultCode != 0) {

            switch (requestCode) {
                case (1): {
                    if (resultIntent.hasExtra("ndefRecord")) {
                        Log.d(TAG, "NdefRecord parcel Found");
                        NdefRecordParcel ndefRecordParcel = resultIntent.getParcelableExtra("ndefRecord");
                        NdefRecord record = ndefRecordParcel.getRecord();
                        recordList.add(record);

                        writeListAdapter = new WriteListAdapter(WriteActivity.this, R.layout.snippet_write_list, recordList);
                        recordsToWriteList.setAdapter(writeListAdapter);

                        Log.d(TAG, "Write status not 0");
                    }
                    break;
                }
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

    private void vibrate() {
        Log.d(TAG, "vibrate");
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }


}
