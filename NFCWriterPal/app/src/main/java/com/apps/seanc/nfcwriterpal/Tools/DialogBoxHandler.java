package com.apps.seanc.nfcwriterpal.Tools;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.seanc.nfcwriterpal.ArrayAdapters.ApplicationAdapter;
import com.apps.seanc.nfcwriterpal.R;
import com.apps.seanc.nfcwriterpal.ActivityHandlers.MessageSelection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by seanc on 22/04/2017.
 */

public class DialogBoxHandler extends ListActivity{


    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter appAdapter = null;

    private static final String TAG = DialogBoxHandler.class.getName();

    private Context context;
    private MessageSelection ms;

    public DialogBoxHandler(Context context){
        this.context = context;
    }

    public DialogBoxHandler(Context context, MessageSelection ms){
        this.context = context;
        this.ms =  ms;
    }


    public Dialog webpageDialog() {
        AlertDialog.Builder webpageDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        webpageDialogBuilder.setView(inflater.inflate(R.layout.dialog_webpage, null))
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog webpageDialog = webpageDialogBuilder.create();

        webpageDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.enter), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText webinput = (EditText) webpageDialog.findViewById(R.id.dw_et_webpage_address);
                ms.setUriString("http://" + webinput.getText().toString());
                ms.returnWithResult(1);
                dialog.cancel();
            }
        });
        return webpageDialog;
    }

    public Dialog phoneCallDialog() {
        AlertDialog.Builder phoneDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        phoneDialogBuilder.setView(inflater.inflate(R.layout.dialog_phonecall, null))
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog phonecallDialog = phoneDialogBuilder.create();

        phonecallDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.enter), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText phoneNumber = (EditText) phonecallDialog.findViewById(R.id.dp_et_phone_number);
                ms.setUriString("tel:" + phoneNumber.getText().toString());
                ms.returnWithResult(1);
                dialog.cancel();
            }
        });
        return phonecallDialog;
    }

    public Dialog smsDialog() {
        AlertDialog.Builder smsDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        smsDialogBuilder.setView(inflater.inflate(R.layout.dialog_sms, null))
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog smsDialog = smsDialogBuilder.create();

        smsDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.enter), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText smsNumber = (EditText) smsDialog.findViewById(R.id.ds_et_phone_number);
                EditText smsBody = (EditText) smsDialog.findViewById(R.id.ds_et_sms_body);
                ms.setUriString("sms:" + smsNumber.getText().toString() + "?body=" + smsBody.getText().toString());
                ms.returnWithResult(1);
                dialog.cancel();
            }
        });
        return smsDialog;
    }

    public Dialog emailDialog() {
        AlertDialog.Builder emailDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        emailDialogBuilder.setView(inflater.inflate(R.layout.dialog_email, null))
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog emailDialog = emailDialogBuilder.create();

        emailDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.enter), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText emailAddress = (EditText) emailDialog.findViewById(R.id.de_et_email_address);
                EditText emailSubject = (EditText) emailDialog.findViewById(R.id.de_et_subject);
                EditText emailBody = (EditText) emailDialog.findViewById(R.id.de_et_body);
                Log.d(TAG, "mailto:" + emailAddress.getText().toString() + "?subject=" + emailSubject.getText().toString() + "&body=" + emailBody.getText().toString());
                ms.setUriString("mailto:" + emailAddress.getText().toString() + "?subject=" + emailSubject.getText().toString() + "&body=" + emailBody.getText().toString());
                ms.returnWithResult(1);
                dialog.cancel();
            }
        });
        return emailDialog;
    }

    public Dialog locationDialog() {
        AlertDialog.Builder locationDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        locationDialogBuilder.setView(inflater.inflate(R.layout.dialog_location, null))
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNeutralButton(context.getString(R.string.use_location), null);

        final AlertDialog locationDialog = locationDialogBuilder.create();

        locationDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        EditText latitude = (EditText) locationDialog.findViewById(R.id.dl_et_latitude);
                        EditText longitude = (EditText) locationDialog.findViewById(R.id.dl_et_longitude);
                        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        try {
                            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            double currentLongitude = location.getLongitude();
                            double currentLatitude = location.getLatitude();
                            latitude.setText(Double.toString(currentLatitude));
                            longitude.setText(Double.toString(currentLongitude));
                        } catch (SecurityException e){
                            Log.d(TAG, e.toString());
                        }
                    }
                });
            }
        });

        locationDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.enter), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText latitude = (EditText) locationDialog.findViewById(R.id.dl_et_latitude);
                EditText longitude = (EditText) locationDialog.findViewById(R.id.dl_et_longitude);
                ms.setUriString("geo:" + latitude.getText().toString() + "," + longitude.getText().toString());
                ms.returnWithResult(1);
                dialog.cancel();
            }
        });

        return locationDialog;
    }

    public Dialog appDialog(){
        AlertDialog.Builder appDialogBuilder = new AlertDialog.Builder(context);
        packageManager = context.getPackageManager();

        appDialogBuilder.setTitle("Select An App:");

        applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));

        Collections.sort(applist, new Comparator<ApplicationInfo>() {
            public int compare(ApplicationInfo appOne, ApplicationInfo appTwo) {
                String appOneName = "" + appOne.loadLabel(packageManager);
                String appTwoName = "" + appTwo.loadLabel(packageManager);
                return appOneName.compareTo(appTwoName);
            }
        });
        appAdapter = new ApplicationAdapter(context,
                R.layout.snippet_app_list, applist);


        appDialogBuilder.setAdapter(appAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApplicationInfo appInfo = appAdapter.getItem(which);
                ms.setAppInfo(appInfo);
                ms.returnWithResult(2);
                dialog.cancel();
            }
        });

        return appDialogBuilder.create();
    }

    private Dialog hmsDialog(){

        AlertDialog.Builder hmsDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        hmsDialogBuilder.setView(inflater.inflate(R.layout.dialog_hms, null))
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog hmsDialog = hmsDialogBuilder.create();

        hmsDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.enter), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText ipAddress = (EditText) hmsDialog.findViewById(R.id.dh_et_ip);
                EditText scriptLocation = (EditText) hmsDialog.findViewById(R.id.dh_et_location);
                EditText scriptName = (EditText) hmsDialog.findViewById(R.id.dh_et_name);
                Log.d(TAG, "hms:" + ipAddress.getText().toString() + "?subject=" + scriptLocation.getText().toString() + "&body=" + scriptName.getText().toString());
                ms.setMimeString("hms:" + ipAddress.getText().toString() + scriptLocation.getText().toString() + "/" + scriptName.getText().toString());
                ms.returnWithResult(3);
                dialog.cancel();
            }
        });
        return hmsDialog;

    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }

    public Dialog writeDialog(){

        TextView title = new TextView(context);
        title.setText("Tap Device Off NFC Tag");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        AlertDialog.Builder writeDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        writeDialogBuilder.setView(inflater.inflate(R.layout.dialog_write, null))
                .setPositiveButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setCustomTitle(title);

        return writeDialogBuilder.create();
    }

}
