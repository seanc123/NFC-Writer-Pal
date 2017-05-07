package com.apps.seanc.nfcwriterpal.ActivityHandlers;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.apps.seanc.nfcwriterpal.R;
import com.apps.seanc.nfcwriterpal.Tools.HttpController;

/**
 * Created by seanc on 20/04/2017.
 *
 * This class is used when a mime type application/com.apps.seanc.nfcwriterpal is detected.
 * When  this activity is started via an NFC intent of this type it retrieves the payload and passes it to the
 * controller to create a http request.
 *
 */

public class HMSManagerActivity extends Activity {


    private final String TAG = HMSManagerActivity.class.getName();
    private HttpController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hms_manager);

        controller = new HttpController();
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
                msgs[0].getRecords()[0].getPayload();
                byte[] payload = msgs[0].getRecords()[0].getPayload();

                String payloadString = new String(payload);
                Log.d(TAG, "Payload = " + payloadString);

                Toast.makeText(HMSManagerActivity.this, "Sending Request " + payloadString, Toast.LENGTH_LONG).show();

                Log.d(TAG, "Running Ok Http");
                controller.okHttp(payloadString);

                this.finishAffinity();
            } catch(Exception e){
                Log.d(TAG, e.toString());
            }
        }

    }

}
