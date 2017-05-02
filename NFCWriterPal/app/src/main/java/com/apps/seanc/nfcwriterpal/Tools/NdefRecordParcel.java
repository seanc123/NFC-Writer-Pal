package com.apps.seanc.nfcwriterpal.Tools;

import android.nfc.NdefRecord;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by seanc on 26/04/2017.
 */

public class NdefRecordParcel implements Parcelable {

    public static final Parcelable.Creator<NdefRecordParcel> CREATOR = new Parcelable.Creator<NdefRecordParcel>() {
        public NdefRecordParcel createFromParcel(Parcel source) {
            final NdefRecordParcel recordParcel = new NdefRecordParcel();
            recordParcel.record = (NdefRecord) source.readValue(NdefRecordParcel.class.getClassLoader());
            return recordParcel;
        }

        public NdefRecordParcel[] newArray(int size) {
            throw new UnsupportedOperationException();
        }

    };


    public NdefRecord getRecord() {
        return record;
    }

    public NdefRecord record;

    private NdefRecordParcel() {}

    public NdefRecordParcel( NdefRecord record ) {
        this.record = record;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int ignored) {
        dest.writeValue(record);
    }
}
