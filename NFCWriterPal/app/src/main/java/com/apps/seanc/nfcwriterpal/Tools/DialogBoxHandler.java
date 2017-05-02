package com.apps.seanc.nfcwriterpal.Tools;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.apps.seanc.nfcwriterpal.ActivityHandlers.FormatActivity;
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
    private ApplicationAdapter appAdapter = null;

    private static final String TAG = DialogBoxHandler.class.getName();

    private Context context;
    private MessageSelection ms;
    private FormatActivity formatActivity;



    public DialogBoxHandler(Context context){
        this.context = context;
    }

    public DialogBoxHandler(Context context, MessageSelection ms){
        this.context = context;
        this.ms =  ms;
    }

    public DialogBoxHandler(Context context, FormatActivity formatActivity){
        this.context = context;
        this.formatActivity =  formatActivity;
    }


    public void webpageDialog() {
        AlertDialog.Builder webpageDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        webpageDialogBuilder.setView(inflater.inflate(R.layout.dialog_webpage, null))
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(context.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog webpageDialog = webpageDialogBuilder.create();

        webpageDialog.show();

        webpageDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText webpageInput = (EditText) webpageDialog.findViewById(R.id.dw_et_webpage_address);
                if("".equals(webpageInput.getText().toString().trim())){
                    webpageInput.setError(context.getString(R.string.error_web_required));
                    return;
                } else {
                    ms.setUriString("http://" + webpageInput.getText().toString());
                    ms.returnWithResult(1);
                    webpageDialog.dismiss();
                }
            }
        });
    }

    public void phoneCallDialog() {
        AlertDialog.Builder phoneDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        phoneDialogBuilder.setView(inflater.inflate(R.layout.dialog_phonecall, null))
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(context.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog phonecallDialog = phoneDialogBuilder.create();

        phonecallDialog.show();

        phonecallDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText phoneNumber = (EditText) phonecallDialog.findViewById(R.id.dp_et_phone_number);
                if(TextUtils.isEmpty(phoneNumber.getText().toString())){
                    phoneNumber.setError(context.getString(R.string.error_phone_required));
                } else {
                    ms.setUriString("tel:" + phoneNumber.getText().toString());
                    ms.returnWithResult(1);
                    phonecallDialog.cancel();
                }
            }
        });
    }

    public void smsDialog() {
        AlertDialog.Builder smsDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        smsDialogBuilder.setView(inflater.inflate(R.layout.dialog_sms, null))
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(context.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog smsDialog = smsDialogBuilder.create();

        smsDialog.show();

        smsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText smsNumber = (EditText) smsDialog.findViewById(R.id.ds_et_phone_number);
                EditText smsBody = (EditText) smsDialog.findViewById(R.id.ds_et_sms_body);
                if(TextUtils.isEmpty(smsNumber.getText().toString()) && TextUtils.isEmpty(smsBody.getText().toString())){
                    smsNumber.setError(context.getString(R.string.error_phone_required));
                    smsBody.setError(context.getString(R.string.error_body_required));
                } else if(TextUtils.isEmpty(smsNumber.getText().toString())){
                    ms.setUriString("sms:" + "0" + "?body=" + smsBody.getText().toString());
                    ms.returnWithResult(1);
                    smsDialog.cancel();
                } else if(TextUtils.isEmpty(smsBody.getText().toString())){
                    ms.setUriString("sms:" + smsNumber.getText().toString() + "?body=" + "-");
                    ms.returnWithResult(1);
                    smsDialog.cancel();
                } else {
                    ms.setUriString("sms:" + smsNumber.getText().toString() + "?body=" + smsBody.getText().toString());
                    ms.returnWithResult(1);
                    smsDialog.cancel();
                }
            }
        });
    }

    public void emailDialog() {
        AlertDialog.Builder emailDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        emailDialogBuilder.setView(inflater.inflate(R.layout.dialog_email, null))
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(context.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog emailDialog = emailDialogBuilder.create();

        emailDialog.show();

        emailDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailAddress = (EditText) emailDialog.findViewById(R.id.de_et_email_address);
                EditText emailSubject = (EditText) emailDialog.findViewById(R.id.de_et_subject);
                EditText emailBody = (EditText) emailDialog.findViewById(R.id.de_et_body);
                if(TextUtils.isEmpty(emailAddress.getText().toString())
                        && TextUtils.isEmpty(emailSubject.getText().toString())
                        && TextUtils.isEmpty(emailBody.getText().toString())){

                    emailAddress.setError(context.getString(R.string.error_email_required));
                    emailSubject.setError(context.getString(R.string.error_subject_required));
                    emailBody.setError(context.getString(R.string.error_body_required));

                } else if(TextUtils.isEmpty(emailAddress.getText().toString())){
                    Log.d(TAG, "mailto:" + "-" + "?subject=" + emailSubject.getText().toString() + "&body=" + emailBody.getText().toString());
                    ms.setUriString("mailto:" + "-" + "?subject=" + emailSubject.getText().toString() + "&body=" + emailBody.getText().toString());
                    ms.returnWithResult(1);
                    emailDialog.cancel();
                } else if(TextUtils.isEmpty(emailSubject.getText().toString())){
                    Log.d(TAG, "mailto:" + emailAddress.getText().toString() + "?subject=" + "-" + "&body=" + emailBody.getText().toString());
                    ms.setUriString("mailto:" + emailAddress.getText().toString() + "?subject=" + "-" + "&body=" + emailBody.getText().toString());
                    ms.returnWithResult(1);
                    emailDialog.cancel();
                } else if(TextUtils.isEmpty(emailBody.getText().toString())){
                    Log.d(TAG, "mailto:" + emailAddress.getText().toString() + "?subject=" + emailSubject.getText().toString() + "&body=" + "-");
                    ms.setUriString("mailto:" + emailAddress.getText().toString() + "?subject=" + emailSubject.getText().toString() + "&body=" + "-");
                    ms.returnWithResult(1);
                    emailDialog.cancel();
                } else {
                    Log.d(TAG, "mailto:" + emailAddress.getText().toString() + "?subject=" + emailSubject.getText().toString() + "&body=" + emailBody.getText().toString());
                    ms.setUriString("mailto:" + emailAddress.getText().toString() + "?subject=" + emailSubject.getText().toString() + "&body=" + emailBody.getText().toString());
                    ms.returnWithResult(1);
                    emailDialog.cancel();
                }
            }
        });
    }

    public void locationDialog() {
        AlertDialog.Builder locationDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        locationDialogBuilder.setView(inflater.inflate(R.layout.dialog_location, null))
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNeutralButton(context.getString(R.string.dialog_location_use_current), null)
                .setPositiveButton(context.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

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

        locationDialog.show();

        locationDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText latitude = (EditText) locationDialog.findViewById(R.id.dl_et_latitude);
                EditText longitude = (EditText) locationDialog.findViewById(R.id.dl_et_longitude);
                if(TextUtils.isEmpty(latitude.getText().toString()) && TextUtils.isEmpty(longitude.getText().toString())){
                    latitude.setError(context.getString(R.string.error_latitude));
                    longitude.setError(context.getString(R.string.error_longitude));
                } else if(TextUtils.isEmpty(latitude.getText().toString())){
                    latitude.setError(context.getString(R.string.error_latitude));
                } else if(TextUtils.isEmpty(longitude.getText().toString())){
                    longitude.setError(context.getString(R.string.error_longitude));
                } else{
                    ms.setUriString("geo:" + latitude.getText().toString() + "," + longitude.getText().toString());
                    ms.returnWithResult(1);
                    locationDialog.cancel();
                }
            }
        });
    }

    public void appDialog(){
        AlertDialog.Builder appDialogBuilder = new AlertDialog.Builder(context);
        packageManager = context.getPackageManager();

        appDialogBuilder.setTitle("Select An App:");


        List<ApplicationInfo> applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));

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

        appDialogBuilder.create().show();

    }

    public void hmsDialog(){

        final AlertDialog.Builder hmsDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        hmsDialogBuilder.setView(inflater.inflate(R.layout.dialog_hms, null))
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(context.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog hmsDialog = hmsDialogBuilder.create();

        hmsDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                String[] httpTypes = new String[]{context.getString(R.string.http),context.getString(R.string.https)};
                final Spinner httpSpinner = (Spinner) hmsDialog.findViewById(R.id.dh_spinr_http);

                ArrayAdapter<String> httpAdapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, httpTypes);
                httpAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                httpSpinner.setAdapter(httpAdapter);

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        EditText ipAddress = (EditText) hmsDialog.findViewById(R.id.dh_et_ip);
                        EditText scriptLocation = (EditText) hmsDialog.findViewById(R.id.dh_et_location);
                        EditText scriptName = (EditText) hmsDialog.findViewById(R.id.dh_et_name);


                        String httpSelected = "";
                        if(httpSpinner.getSelectedItemPosition()==0){
                            httpSelected = "http://";
                        } else if(httpSpinner.getSelectedItemPosition() == 1){
                            httpSelected = "https://";
                        }

                        if(TextUtils.isEmpty(ipAddress.getText().toString())
                                && TextUtils.isEmpty(scriptName.getText().toString())){
                            ipAddress.setError(context.getString(R.string.error_ip));
                            scriptName.setError(context.getString(R.string.error_script));
                        } else if(TextUtils.isEmpty(ipAddress.getText().toString())){
                            ipAddress.setError(context.getString(R.string.error_ip));
                        } else if(TextUtils.isEmpty(scriptName.getText().toString())){
                            scriptName.setError(context.getString(R.string.error_script));
                        } else if(TextUtils.isEmpty(scriptLocation.getText().toString())){
                            Log.d(TAG, httpSelected + ipAddress.getText().toString() + "/" + scriptName.getText().toString());
                            ms.setMimeString(httpSelected + ipAddress.getText().toString() + "/" + scriptName.getText().toString());
                            ms.returnWithResult(3);
                            hmsDialog.cancel();
                        } else {
                            Log.d(TAG, httpSelected + ipAddress.getText().toString() + scriptLocation.getText().toString() + "/" + scriptName.getText().toString());
                            ms.setMimeString(httpSelected + ipAddress.getText().toString() + scriptLocation.getText().toString() + "/" + scriptName.getText().toString());
                            ms.returnWithResult(3);
                            hmsDialog.cancel();
                        }

                    }
                });

            }
        });

        hmsDialog.show();

        hmsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

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

    public AlertDialog tapTagDialog(){

        TextView title = new TextView(context);
        title.setText(context.getString(R.string.dialog_tap_title));
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

    public AlertDialog unformattableDialog(){
        TextView title = new TextView(context);
        title.setText(context.getString(R.string.dialog_unformattable_title));
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        AlertDialog.Builder writeDialogBuilder = new AlertDialog.Builder(context);
        writeDialogBuilder.setPositiveButton(context.getString(R.string.accept), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        formatActivity.showWipeDialog();
                        dialog.cancel();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCustomTitle(title)
                .setMessage(context.getString(R.string.dialog_unformattable_message));

        return writeDialogBuilder.create();
    }

}
