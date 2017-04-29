/*
 MIT License
 Copyright (c) 2016 Henry Chladil
 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:
 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package com.apps.seanc.nfcwriterpal.Tools;

import android.net.wifi.WifiManager;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Parcelable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Henry on 8/02/2016.
 */
public class NFCHelper {

    private final String TAG = NFCHelper.class.getName();

    public static final int HEX_RADIX = 16;
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static final String PASSWORD_FORMAT = "102700%s%s";
    public static final Map<Byte, String> URI_PREFIX_MAP;
    private WifiManager wifiManager;

    public static final byte[] VERSION = {0x10, 0x4A};
    public static final byte[] CREDENTIAL = {0x10, 0x0e};
    public static final byte[] AUTH_TYPE    = {0x10, 0x03};
    public static final byte[] CRYPT_TYPE   = {0x10, 0x0F};
    public static final byte[] MAC_ADDRESS  = {0x10, 0x20};
    public static final byte[] NETWORK_IDX  = {0x10, 0x26};
    public static final byte[] NETWORK_KEY  = {0x10, 0x27};
    public static final byte[] NETWORK_NAME = {0x10, 0x45};
    public static final byte[] OOB_PASSWORD = {0x10, 0x2C};
    public static final byte[] VENDOR_EXT   = {0x10, 0x49};
    public static final byte[] VENDOR_WFA   = {0x00, 0x37, 0x2A};
    public static final byte[] VERSION2     = {0x00};
    public static final byte[] KEY_SHAREABLE = {0x02};

    public static final byte[] AUTH_OPEN = {0x00, 0x01};
    public static final byte[] AUTH_WPA_PERSONAL = {0x00, 0x02};
    public static final byte[] AUTH_SHARED = {0x00, 0x04};
    public static final byte[] AUTH_WPA_ENTERPRISE = {0x00, 0x08};
    public static final byte[] AUTH_WPA2_ENTERPRISE = {0x00, 0x10};
    public static final byte[] AUTH_WPA2_PERSONAL = {0x00, 0x20};
    public static final byte[] AUTH_WPA_WPA2_PERSONAL = {0x00, 0x22};


    public static final byte[] CRYPT_NONE = {0x00, 0x01};
    public static final byte[] CRYPT_WEP = {0x00, 0x02};
    public static final byte[] CRYPT_TKIP = {0x00, 0x04};
    public static final byte[] CRYPT_AES = {0x00, 0x08};
    public static final byte[] CRYPT_AES_TKIP = {0x00, 0x0C};

    public NFCHelper(){

    }

    static {
        HashMap<Byte, String> map = new HashMap<>();
        map.put((byte) 0x00, "");
        map.put((byte) 0x01, "http://www.");
        map.put((byte) 0x02, "https://www.");
        map.put((byte) 0x03, "http://");
        map.put((byte) 0x04, "https://");
        map.put((byte) 0x05, "tel:");
        map.put((byte) 0x06, "mailto:");
        map.put((byte) 0x07, "ftp://anonymous:anonymous@");
        map.put((byte) 0x08, "ftp://ftp.");
        map.put((byte) 0x09, "ftps://");
        map.put((byte) 0x0A, "sftp://");
        map.put((byte) 0x0B, "smb://");
        map.put((byte) 0x0C, "nfs://");
        map.put((byte) 0x0D, "ftp://");
        map.put((byte) 0x0E, "dav://");
        map.put((byte) 0x0F, "news:");
        map.put((byte) 0x10, "telnet://");
        map.put((byte) 0x11, "imap:");
        map.put((byte) 0x12, "rtsp://");
        map.put((byte) 0x13, "urn:");
        map.put((byte) 0x14, "pop:");
        map.put((byte) 0x15, "sip:");
        map.put((byte) 0x16, "sips:");
        map.put((byte) 0x17, "tftp:");
        map.put((byte) 0x18, "btspp://");
        map.put((byte) 0x19, "btl2cap://");
        map.put((byte) 0x1A, "btgoep://");
        map.put((byte) 0x1B, "tcpobex://");
        map.put((byte) 0x1C, "irdaobex://");
        map.put((byte) 0x1D, "file://");
        map.put((byte) 0x1E, "urn:epc:id:");
        map.put((byte) 0x1F, "urn:epc:tag:");
        map.put((byte) 0x20, "urn:epc:pat:");
        map.put((byte) 0x21, "urn:epc:raw:");
        map.put((byte) 0x22, "urn:epc:");
        map.put((byte) 0x23, "urn:nfc:");
        URI_PREFIX_MAP = Collections.unmodifiableMap(map);
    }

    public static NdefRecord createTextRecord(String language, String text) {
        byte[] languageBytes = language.getBytes(Charset.forName("US-ASCII"));
        byte[] textBytes = text.getBytes(Charset.forName("UTF-8"));

        byte[] recordPayload = new byte[1 + (languageBytes.length & 0x03F) + textBytes.length];

        recordPayload[0] = (byte)(languageBytes.length & 0x03F);
        System.arraycopy(languageBytes, 0, recordPayload, 1, languageBytes.length & 0x03F);
        System.arraycopy(textBytes, 0, recordPayload, 1 + (languageBytes.length & 0x03F), textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, null, recordPayload);
    }

    public NdefRecord createVcardRecord(String name, String org, String tel, String email)
            throws UnsupportedEncodingException {

        String nameVcard = "BEGIN:VCARD" +"\n"+
                // "VERSION:2.1" +"\n" + // Note, reenable this for some devices. It just uses valuable memory.
                "N:;" + name + "\n" +
                "ORG:" + org + "\n" +
                "TEL;TYPE=CELL:" + tel + "\n" +
                "EMAIL:" + email + "\n" +
                "END:VCARD";
        byte[] uriField = nameVcard.getBytes(Charset.forName("US-ASCII"));
        byte[] payload = new byte[uriField.length + 1];              //add 1 for the URI Prefix
        //payload[0] = 0x01;                                      //prefixes http://www. to the URI
        System.arraycopy(uriField, 0, payload, 1, uriField.length);  //appends URI to payload

        NdefRecord nfcRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, "text/vcard".getBytes(), new byte[0], payload);

        return nfcRecord;
    }

    public NdefRecord createWifiRecord(String[] data) {
        String NFC_TOKEN_MIME_TYPE = "application/vnd.wfa.wsc";
        String ssid = data[0];
        String password = data[1];
        String auth = data[2];
        String crypt = data[3];
        byte[] authByte = auth.getBytes();
        byte[] cryptByte = crypt.getBytes();
        byte[] ssidByte = ssid.getBytes();
        byte[] passwordByte = password.getBytes();
        byte[] ssidLength = {(byte)((int)Math.floor(ssid.length()/256)), (byte)(ssid.length()%256)};
        byte[] passwordLength = {(byte)((int)Math.floor(password.length()/256)), (byte)(password.length()%256)};
        byte[] cred = {0x00, 0x36};
        byte[] idx = {0x00, 0x01, 0x01};
        byte[] mac = {0x00, 0x06};
        byte[] keypad = {0x00, 0x0B};

        byte[] payload = null;

        try{
            payload = concat(NFCHelper.CREDENTIAL, cred,
                    NFCHelper.NETWORK_IDX, idx,
                    NFCHelper.NETWORK_NAME, ssidLength, ssidByte,
                    NFCHelper.AUTH_TYPE, NFCHelper.AUTH_WPA_PERSONAL, authByte,
                    NFCHelper.CRYPT_TYPE, NFCHelper.CRYPT_WEP, NFCHelper.CRYPT_AES_TKIP,
                    NFCHelper.NETWORK_KEY, passwordLength, passwordByte);
        } catch ( Exception e ){
            Log.d(TAG, e.toString());
        }
        // NFCHelper.MAC_ADDRESS, mac);
        return myCreateMime(NFC_TOKEN_MIME_TYPE, payload);
    }

    private byte[] concat(byte[] credential, byte[] cred,
                          byte[] network_idx, byte[] idx,
                          byte[] networkName, byte[] ssidLength, byte[] ssidByte,
                          byte[] auth_type, byte[] auth_wpa_personal , byte[] authByte,
                          byte[] crypt_type, byte[] crypt_web, byte[] crypt_aes_tkip,
                          byte[] network_key, byte[] passwordLength, byte[] passwordByte) throws IOException{

        byte[] payload;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( credential );
        outputStream.write( cred );
        outputStream.write( network_idx );
        outputStream.write(idx  );
        outputStream.write( networkName );
        outputStream.write( ssidLength );
        outputStream.write( ssidByte );
        outputStream.write( auth_type );
        outputStream.write( auth_wpa_personal );
        outputStream.write( authByte );
        outputStream.write( crypt_type );
        outputStream.write( crypt_web );
        outputStream.write( crypt_aes_tkip );
        outputStream.write( network_key );
        outputStream.write( passwordLength );
        outputStream.write( passwordByte );

        payload = outputStream.toByteArray( );

        return payload;

    }

    public NdefRecord myCreateMime (String mimeType, byte[] mimeData) {
        return new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                mimeType.getBytes(Charset.forName("US-ASCII")),
                null,
                mimeData);
    }
}