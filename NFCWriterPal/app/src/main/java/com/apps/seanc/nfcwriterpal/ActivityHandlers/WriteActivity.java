package com.apps.seanc.nfcwriterpal.ActivityHandlers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.seanc.nfcwriterpal.ArrayAdapters.WriteListAdapter;
import com.apps.seanc.nfcwriterpal.R;
import com.apps.seanc.nfcwriterpal.Tools.DialogBoxHandler;
import com.apps.seanc.nfcwriterpal.Tools.NdefRecordParcel;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by seanc on 25/04/2017.
 *
 *  This class is used for for launching the record selection activity and passing all the records
 *  retrieved back from it into an NdefMessage to be written to a tag
 *
 */

public class WriteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private NfcAdapter nfcAdpt;
    private PendingIntent nfcPendingIntent;

    private ListView recordsToWriteList;
    private Button addMessageBtn, removeBtn, writeBtn;
    private TextView msgSize, tagSize;
    private String TAG = RecordSelection.class.getName();
    private List<NdefRecord> recordList;
    private AlertDialog writeDialog;
    private WriteListAdapter writeListAdapter;

    private int msgSizeVal;

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

        msgSize = (TextView) findViewById(R.id.write_tv_msg_size);
        tagSize = (TextView) findViewById(R.id.write_tv_tag_size);
        recordList = new ArrayList<NdefRecord>();

        msgSizeVal = 0;
        msgSize.setText(msgSizeVal + " " + getString(R.string.bytes));
        tagSize.setText(getString(R.string.scan_tag));

        setOnClickListeners();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef nfcTag = Ndef.get(tag);
            if (writeDialog != null) {
                if (writeDialog.isShowing()) {
                    NdefRecord[] ndefRecords = recordList.toArray(new NdefRecord[0]);
                    NdefMessage ndefMessage = new NdefMessage(ndefRecords);
                    try {
                        if (!nfcTag.isWritable()) {
                            writeDialog.dismiss();
                            Toast.makeText(WriteActivity.this, getString(R.string.format_not_writable), Toast.LENGTH_LONG).show();

                        } else if (nfcTag.getMaxSize() < ndefMessage.toByteArray().length) {
                            writeDialog.dismiss();
                            Toast.makeText(WriteActivity.this, getString(R.string.write_too_small) + "\n" + getString(R.string.write_available) + " " + nfcTag.getMaxSize() + " " + getString(R.string.write_need) + ndefMessage.toByteArray().length, Toast.LENGTH_LONG).show();

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
                } else {

                    try {
                        tagSize.setText(nfcTag.getMaxSize() + " " + getString(R.string.bytes));
                    } catch (Exception e) {
                        tagSize.setText(getString(R.string.write_not_writable));
                    }

                }
            }else {

                try {
                    tagSize.setText(nfcTag.getMaxSize() + " " + getString(R.string.bytes));
                } catch (Exception e) {
                    tagSize.setText(getString(R.string.write_not_writable));
                }

            }
        }
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

    // Sets the onClickListeners for the buttons on the UI
    private void setOnClickListeners() {

        addMessageBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent messageSelectionIntent = new Intent(WriteActivity.this, RecordSelection.class);
                startActivityForResult(messageSelectionIntent, 1);
            }
        });

        writeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(recordList.size() == 0){
                    final AlertDialog.Builder noRecordDialog = new AlertDialog.Builder(WriteActivity.this);
                    noRecordDialog.setTitle(getString(R.string.write_dialog_no_records))
                            .setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    noRecordDialog.create().show();
                } else{
                    DialogBoxHandler dialogHandler = new DialogBoxHandler(WriteActivity.this);
                    writeDialog = dialogHandler.tapTagDialog();
                    writeDialog.show();
                }

            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordList = new ArrayList<NdefRecord>();
                writeListAdapter = new WriteListAdapter(WriteActivity.this, R.layout.snippet_write_list, recordList);
                recordsToWriteList.setAdapter(writeListAdapter);
                msgSizeVal = 0;
                msgSize.setText(msgSizeVal + " " + getString(R.string.bytes));
            }
        });
    }

    @Override //collects any data sent from the record selection screen and adds it to the record list
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent){
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if(resultCode != 0) {
            switch (requestCode) {
                case (1): {
                    if (resultIntent.hasExtra("ndefRecord")) {
                        Log.d(TAG, "NdefRecord parcel Found");

                        NdefRecordParcel ndefRecordParcel = resultIntent.getParcelableExtra("ndefRecord");

                        NdefRecord record = ndefRecordParcel.getRecord();
                        msgSizeVal+=record.toByteArray().length;
                        msgSize.setText(msgSizeVal + " " + getString(R.string.bytes));
                        recordList.add(record);

                        writeListAdapter = new WriteListAdapter(WriteActivity.this, R.layout.snippet_write_list, recordList);
                        recordsToWriteList.setAdapter(writeListAdapter);

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
