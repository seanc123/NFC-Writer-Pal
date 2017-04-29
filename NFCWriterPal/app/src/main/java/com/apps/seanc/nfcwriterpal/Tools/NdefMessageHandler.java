package com.apps.seanc.nfcwriterpal.Tools;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.annotation.NonNull;
import android.util.Log;

import org.ndeftools.Message;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.wellknown.TextRecord;

import java.io.IOException;

/**
 * Created by seanc on 27/03/2017.
 */

public class NdefMessageHandler {


    private static final String TAG = NdefMessageHandler.class.getName();

    public NdefMessageHandler ndefMessageHandler(){
        return this;
    }

    public void writeApplication(String packageName, Intent intent){
        Message ndefRecord = createMessage(packageName);
        NdefMessage ndefMessage = messageToNdef(ndefRecord);
        if(writeToTag(ndefMessage, intent)){
            //return int x
            //x correlates with a log output (success/failure string) in strings.xml
            Log.d(TAG, "Write Success");
        }

    }


    private Message createMessage(String packageName){
        Log.d(TAG, "createMessage");

        Message message = new Message();

        //Add an android application record
        AndroidApplicationRecord appRecord = new AndroidApplicationRecord(packageName);
        message.add(appRecord);

        //Add a text record
        TextRecord record = new TextRecord(packageName);
        message.add(record);

        return message;
    }

    private NdefMessage messageToNdef(Message ndefRecord){

        Message composedMessage = ndefRecord;

                /*instagramMessage("1");
        if(app.equalsIgnoreCase("Facebook")){
            composedMessage = facebookMessage("1");
        } else if(app.equalsIgnoreCase("Spotify")){
            composedMessage = spotifyMessage("1");
        }*/

        NdefMessage ndefMessage = composedMessage.getNdefMessage();
        return ndefMessage;
    }

    private boolean writeToTag(NdefMessage rawMessage, Intent intent){
        Tag tag = intent.getParcelableExtra((NfcAdapter.EXTRA_TAG));
        Ndef ndef = Ndef.get(tag);
        if(ndef != null){
            try{
                Log.d(TAG, "Tag is not writeable");
                ndef.connect();
                if(!ndef.isWritable()){
                    Log.d(TAG, "Tag is not writeable");
                    return false;
                }

                if(ndef.getMaxSize() < rawMessage.toByteArray().length){
                    Log.d(TAG, "Tag size is too small, have " + ndef.getMaxSize() + ", need " + rawMessage.toByteArray().length);
                    return false;
                }

                ndef.writeNdefMessage(rawMessage);
                return true;
            } catch(Exception e){
                Log.d(TAG, "Problem writing to tag: ", e);
            } finally {
                try{
                    ndef.close();
                } catch (IOException e){
                    Log.d(TAG, "Problem closing tag connection: ", e);
                }
            }
        }
        else {
            Log.d(TAG, "Write to an unformatted tag not implemented");
        }
        return false;
    }

    @NonNull
    private String toHexString(byte[] buffer){
        StringBuilder sb = new StringBuilder();
        for(byte b: buffer){
            sb.append(String.format("%02x", b&0xff));
        }
        return sb.toString().toUpperCase();
    }


}


