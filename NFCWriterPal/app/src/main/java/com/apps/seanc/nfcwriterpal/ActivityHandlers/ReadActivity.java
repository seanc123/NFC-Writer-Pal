package com.apps.seanc.nfcwriterpal.ActivityHandlers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.apps.seanc.nfcwriterpal.ArrayAdapters.RecordAdapter;
import com.apps.seanc.nfcwriterpal.CutomViews.NonScrollListView;
import com.apps.seanc.nfcwriterpal.R;
import com.apps.seanc.nfcwriterpal.Tools.DialogBoxHandler;

import org.ndeftools.Message;
import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.wellknown.TextRecord;

import java.util.List;
import java.util.Locale;

public class ReadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private NfcAdapter nfcAdpt;
    private PendingIntent nfcPendingIntent;
    private static final String TAG = ReadActivity.class.getName();
    private TextView tagID, tagType, tagMessages, tagRecords, maxSize, currentSize, isWritable;
    private NonScrollListView techListNS, recordListNS;
    private RecordAdapter recordAdapter;
    private AlertDialog tapDialog;

    private int ndefMessageSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_read);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nfcAdpt = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        tagID = (TextView) findViewById(R.id.tagID);
        tagType = (TextView) findViewById(R.id.read_tv_type);
        tagMessages  = (TextView) findViewById(R.id.tagMessages);
        tagRecords = (TextView) findViewById(R.id.tagRecords);
        maxSize = (TextView) findViewById(R.id.read_tv_maxSize);
        currentSize = (TextView) findViewById(R.id.read_tv_currentSize);
        isWritable = (TextView) findViewById(R.id.read_tv_writable);
        techListNS = (NonScrollListView) findViewById(R.id.tech_nonscroll_list);
        recordListNS = (NonScrollListView) findViewById(R.id.record_nonscroll_list);
        ndefMessageSize = 0;

        DialogBoxHandler dialogBoxHandler = new DialogBoxHandler(this);
        tapDialog = dialogBoxHandler.tapTagDialog();
        tapDialog.show();
    }

    //Write bool expression for if in writeMode
    @Override
    protected void onNewIntent(Intent intent){
        Log.d(TAG, "onNewIntent");
        tapDialog.dismiss();
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            ndefMessageSize = 0;
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef nfcTag = Ndef.get(tag);
            try{
                nfcTag.connect();
                maxSize.setText(String.format(Locale.getDefault(), "%d %s", nfcTag.getMaxSize() , getString(R.string.bytes)));
                tagType.setText(nfcTag.getType());
                if(nfcTag.isWritable()){
                    isWritable.setText(getString(R.string.yes));
                }
                nfcTag.close();

            } catch (Exception e){
                Log.d(TAG, e.toString());
            }

            ArrayAdapter<String> techAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, techConcat(tag.getTechList()));
            techListNS.setAdapter(techAdapter);

            tagID.setText(printTagId(intent));



            vibrate();

            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (messages != null) {
                Log.d(TAG, "Found " + messages.length + " NDEF messages");
                tagMessages.setText(String.format(Locale.getDefault(), "%d", messages.length));
                parseMessage(messages);

            } else {
                //If tag cannot be recognised, reset all fields to 0/null
                ndefMessageSize = 0;
                tagMessages.setText("0");
                tagRecords.setText("0");
                tagType.setText(getString(R.string.tag_unknown));
                isWritable.setText(getString(R.string.no));
                maxSize.setText(String.format(Locale.getDefault(), "%d %s", 0 , getString(R.string.bytes)));
                recordAdapter = new RecordAdapter(ReadActivity.this, R.layout.snippet_record_list, null);
                recordListNS.setAdapter(recordAdapter);
            }

            currentSize.setText(String.format(Locale.getDefault(), "%d %s", ndefMessageSize, getString(R.string.bytes)));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.read, menu);
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

    //Parses the Parcelable message to an NdefMessage
    private void parseMessage(Parcelable[] messages){
        NdefMessage ndefMessage = (NdefMessage)messages[0];
        ndefMessageSize += ndefMessage.toByteArray().length;
        try{
            List<Record> records = new Message(ndefMessage);

            recordAdapter = new RecordAdapter(ReadActivity.this, R.layout.snippet_record_list, records);
            recordListNS.setAdapter(recordAdapter);
            tagRecords.setText(String.format(Locale.getDefault(), "%d", records.size()));
            iterateNdefMessages(records);
        } catch (Exception e){
            Log.d(TAG, "Problem parsing message ", e);
        }
    }

    //Iterates through each Ndef record and prints out the type of record that it is
    private void iterateNdefMessages(List<Record> records){
        for(int k = 0; k < records.size(); k++){
            Record record = records.get(k);
//            Log.d(TAG, "Record # " + k + " is of class " + record.getClass().getName());
//            Log.d(TAG, "Record Size: "+record.toByteArray().length+" bytes");
//            Log.d(TAG, "Record Type: "+record.getClass().getSimpleName());

            if(record instanceof AndroidApplicationRecord){
                Log.d(TAG, "APPLICATION RECORD FOUND");
                AndroidApplicationRecord aar = (AndroidApplicationRecord) record;
                Log.d(TAG, "Package name: "+aar.getPackageName());
            }
            else if (record instanceof TextRecord){
                Log.d(TAG, "TEXT RECORD FOUND");
                TextRecord tr = (TextRecord) record;
                Log.d(TAG, "Text: "+tr.getText());
            }

        }
    }

    protected String printTagId(Intent intent){
        if(intent.hasExtra(NfcAdapter.EXTRA_ID)){
            byte[] byteArrayExtra = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

            Log.d(TAG, "Tag id is " + toHexString(byteArrayExtra));
            return toHexString(byteArrayExtra);
        }
        else
            return  "0";
    }

    @NonNull
    private String toHexString(byte[] buffer){
        StringBuilder sb = new StringBuilder();
        for(byte b: buffer){
            sb.append(String.format("%02x", b&0xff));
        }
        return sb.toString().toUpperCase();
    }

    private void vibrate() {
        Log.d(TAG, "vibrate");

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            disableForegroundMode();
            super.onBackPressed();
        }
    }


    public String[] techConcat(String[] techList){
        String techPrefix = "\\bandroid\\b.\\bnfc\\b.\\btech\\b.";
        for(int i = 0; i < techList.length; i++){
            techList[i] = techList[i].replaceAll(techPrefix,"");
        }
        return techList;
    }
}
