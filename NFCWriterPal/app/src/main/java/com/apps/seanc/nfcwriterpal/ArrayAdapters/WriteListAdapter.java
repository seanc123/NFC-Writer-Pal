package com.apps.seanc.nfcwriterpal.ArrayAdapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.nfc.NdefRecord;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.seanc.nfcwriterpal.R;

import org.ndeftools.MimeRecord;
import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.wellknown.SmartPosterRecord;
import org.ndeftools.wellknown.UriRecord;

import java.util.List;

/**
 * Created by seanc on 25/04/2017.
 */

public class WriteListAdapter extends ArrayAdapter<NdefRecord> {

    private List<NdefRecord> recordList = null;

    private Context context;
    private PackageManager packageManager;
    private TextView recordType, recordSize;
    private TextView varOneHeader, varTwoHeader, varThreeHeader;
    private TextView varOne, varTwo, varThree;
    private ImageView writeTypeImage;
    private String TAG = WriteListAdapter.class.getName();

    public WriteListAdapter(Context context, int layoutId, List<NdefRecord> recordList){
        super(context, layoutId, recordList);
        this.context = context;
        this.recordList = recordList;
        this.packageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return ((null != recordList) ? recordList.size() : 0);
    }

    @Override
    public NdefRecord getItem(int position) {
        return ((null != recordList) ? recordList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.snippet_write_list, null);
        }

        NdefRecord record = recordList.get(position);
        Record ndefToolRecord = null;

        try {
             ndefToolRecord = Record.parse(record);
        } catch (Exception e){
            Log.d(TAG, e.toString());
        }

        if (null != record) {
            recordType = (TextView) view.findViewById(R.id.snippet_w_type);
            recordSize = (TextView) view.findViewById(R.id.snippet_w_size);
            varOne = (TextView) view.findViewById(R.id.snippet_w_var_one);
            varTwo = (TextView) view.findViewById(R.id.snippet_w_var_two);
            varThree = (TextView) view.findViewById(R.id.snippet_w_var_three);
            varOneHeader = (TextView) view.findViewById(R.id.snippet_w_var_one_header);
            varTwoHeader = (TextView) view.findViewById(R.id.snippet_w_var_two_header);
            varThreeHeader = (TextView) view.findViewById(R.id.snippet_w_var_three_header);
            writeTypeImage = (ImageView) view.findViewById((R.id.snippet_w_iv_type_image)) ;

            recordSize.setText(Integer.toString(record.toByteArray().length) + " Bytes");
            recordType.setText(ndefToolRecord.getClass().getSimpleName());

            if(ndefToolRecord != null) {
                if (ndefToolRecord instanceof AndroidApplicationRecord) {
                    Log.d(TAG, "APPLICATION RECORD FOUND");
                    recordType.setText(ndefToolRecord.getClass().getSimpleName());
                    AndroidApplicationRecord aar = (AndroidApplicationRecord) ndefToolRecord;
                    Log.d(TAG, "Package name: " + aar.getPackageName());
                    try {
                        ApplicationInfo app = context.getPackageManager().getApplicationInfo(aar.getPackageName(), 0);

                        Drawable icon = packageManager.getApplicationIcon(app);
                        writeTypeImage.setImageDrawable(icon);
                        String name = (String) packageManager.getApplicationLabel(app);
                        varOneHeader.setText(context.getString(R.string.write_header_app_name));
                        varOne.setText(name);

                        varTwo.setVisibility(View.GONE);
                        varThree.setVisibility(View.GONE);
                        varTwoHeader.setVisibility(View.GONE);
                        varThreeHeader.setVisibility(View.GONE);

                    } catch (PackageManager.NameNotFoundException e) {
                        Toast toast = Toast.makeText(context, "error in getting icon", Toast.LENGTH_SHORT);
                        toast.show();
                        e.printStackTrace();
                    }

                } else if (ndefToolRecord instanceof UriRecord){

                    Log.d(TAG, "Uri Record Found");
                    setSnippetFields(ndefToolRecord);

                } else if (ndefToolRecord instanceof MimeRecord){
                    recordType.setText(ndefToolRecord.getClass().getSimpleName());

                    String payloadString = new String(record.getPayload());
                    Log.d(TAG, "Payload = " + record.getPayload().toString());
                    varOneHeader.setText("Payload");
                    varOne.setText(payloadString);


                    varTwo.setVisibility(View.GONE);
                    varThree.setVisibility(View.GONE);
                    varTwoHeader.setVisibility(View.GONE);
                    varThreeHeader.setVisibility(View.GONE);

                }
            }

        }
        return view;
    }


    private void setSnippetFields(Record ndefToolRecord){

        UriRecord uriRecord = (UriRecord) ndefToolRecord;
        String uriString = uriRecord.getUri().toString();

        String uriPrefix = uriString.split("\\:")[0];

        String[] uriSubStrings = uriString.split(":|\\?|\\=|\\&|\\/|\\,");

        if(uriPrefix.equalsIgnoreCase("http")){
            recordType.setText(ndefToolRecord.getClass().getSimpleName() + " - " + uriPrefix + " request");

            varOneHeader.setText(context.getString(R.string.write_header_webpage));
            varOne.setText(uriSubStrings[3]);
            writeTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_web_icon));

            varTwo.setVisibility(View.GONE);
            varThree.setVisibility(View.GONE);
            varTwoHeader.setVisibility(View.GONE);
            varThreeHeader.setVisibility(View.GONE);

        } else if(uriPrefix.equalsIgnoreCase("tel")){
            recordType.setText(ndefToolRecord.getClass().getSimpleName() + " - " + " Phone number");
            varOneHeader.setText(context.getString(R.string.write_header_phone_number));

            Log.d(TAG, "Setting tel var 1");
            varOne.setText(uriSubStrings[1]);

            writeTypeImage.setImageDrawable(context.getResources().getDrawable(android.R.drawable.sym_action_call));

            varTwo.setVisibility(View.GONE);
            varTwoHeader.setVisibility(View.GONE);
            varThree.setVisibility(View.GONE);
            varThreeHeader.setVisibility(View.GONE);

        } else if(uriPrefix.equalsIgnoreCase("sms")){
            recordType.setText(ndefToolRecord.getClass().getSimpleName() + " - " + " SMS");
            varOneHeader.setText(context.getString(R.string.write_header_phone_number));
            varTwoHeader.setText(context.getString(R.string.write_header_body));

            Log.d(TAG, "Setting sms var 1");
            varOne.setText(uriSubStrings[1]);
            Log.d(TAG, "Setting sms var 2");
            varTwo.setText(uriSubStrings[3]);

            writeTypeImage.setImageDrawable(context.getResources().getDrawable(android.R.drawable.sym_action_chat));
            writeTypeImage.setColorFilter(ContextCompat.getColor(context,android.R.color.holo_blue_dark));

            varThree.setVisibility(View.GONE);
            varThreeHeader.setVisibility(View.GONE);

        } else if(uriPrefix.equalsIgnoreCase("geo")){

            recordType.setText(ndefToolRecord.getClass().getSimpleName() + " - " + " Location");
            varOneHeader.setText(context.getString(R.string.dialog_location_latitude));
            varTwoHeader.setText(context.getString(R.string.dialog_location_longitude));

            Log.d(TAG, "Setting geo var 1");
            varOne.setText(uriSubStrings[1]);
            Log.d(TAG, "Setting geo var 2");
            varTwo.setText(uriSubStrings[2]);

            writeTypeImage.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_mylocation));
            writeTypeImage.setColorFilter(ContextCompat.getColor(context,android.R.color.black));

            varThree.setVisibility(View.GONE);
            varThreeHeader.setVisibility(View.GONE);

        }else if(uriPrefix.equalsIgnoreCase("mailto")){
            recordType.setText(ndefToolRecord.getClass().getSimpleName() + " - " + " Email");

            varOneHeader.setText(context.getString(R.string.write_header_email_address));
            varTwoHeader.setText(context.getString(R.string.write_header_email_subject));
            varThreeHeader.setText(context.getString(R.string.write_header_body));

            Log.d(TAG, "Setting mail var 1");
            varOne.setText(uriSubStrings[1]);
            Log.d(TAG, "Setting mail var 2");
            varTwo.setText(uriSubStrings[3]);
            Log.d(TAG, "Setting mail var 3");
            varThree.setText(uriSubStrings[5]);

            writeTypeImage.setImageDrawable(context.getResources().getDrawable(android.R.drawable.sym_action_email));
            writeTypeImage.setColorFilter(ContextCompat.getColor(context,android.R.color.holo_red_dark));

        }


    }

}
