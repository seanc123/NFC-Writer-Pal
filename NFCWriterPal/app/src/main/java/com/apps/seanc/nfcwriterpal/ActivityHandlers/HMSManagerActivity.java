package com.apps.seanc.nfcwriterpal.ActivityHandlers;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.apps.seanc.nfcwriterpal.R;
import com.apps.seanc.nfcwriterpal.Tools.Controller;
import com.apps.seanc.nfcwriterpal.Tools.NdefMessageHandler;

public class HMSManagerActivity extends Activity {


    private final String TAG = HMSManagerActivity.class.getName();
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hms_manager);

        controller = new Controller();
        nfcIntent(getIntent());
    }


    protected void nfcIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if (rawMsgs != null) {
            NdefMessage[] msgs = new NdefMessage[rawMsgs.length];

            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
            }

            try{
                byte[] payload = msgs[0].getRecords()[0].getPayload();

                String payloadString = new String(payload);
                Log.d(TAG, "Payload = " + payloadString);


                Toast.makeText(HMSManagerActivity.this, payloadString, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Running Apache Http");
                controller.apacheHttp(payloadString);
                /*Log.d(TAG, "Running Ok Http");
                controller.okHttp(payloadString);
                Log.d(TAG, "Running timeout task");
                controller.timeoutHttp(payloadString);*/
                this.finishAffinity();
            } catch(Exception e){
                Log.d(TAG, e.toString());
            }
        }

    }

    //ForeGround mode gives the current active application priority for reading scanned tags
    /*public void enableForeGroundMode(){
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
    }*/

    /*//Get the Text Encoding
                String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";

                //Get the Language Code
                int languageCodeLength = payload[0] & 0077;
                String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

                //Get the Text
                String text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

                TextRecord textR = new TextRecord(text, languageCode);
                Log.d(TAG, "TextR string = " + textR.getText() + " Locale = " + textR.getLocale() + " text = " + text);*/

}
