package com.apps.seanc.nfcwriterpal.ActivityHandlers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;

import com.apps.seanc.nfcwriterpal.R;

/**
 * Created by seanc on 26/04/2017.
 *
 * This class provides the logic for the contact activity.
 * The contact activity provides users with a number of options for providing feedback on the app.
 *
 */

public class ContactActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = ContactActivity.class.getName();
    private Button bugButton, queryButton, suggestionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bugButton = (Button) findViewById(R.id.contact_btn_bug);
        queryButton = (Button) findViewById(R.id.contact_btn_query);
        suggestionButton = (Button) findViewById(R.id.contact_btn_suggestion);

        setOnClickListeners();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_write) {
            Intent writeActivity = new Intent(ContactActivity.this, WriteActivity.class);
            startActivity(writeActivity);
        } else if (id == R.id.nav_read) {
            Intent readActivityIntent = new Intent(ContactActivity.this, ReadActivity.class);
            startActivity(readActivityIntent);
        } else if (id == R.id.nav_format) {
            Intent formatActivity = new Intent(ContactActivity.this, FormatActivity.class);
            startActivity(formatActivity);
        } else if (id == R.id.nav_share) {
            Intent shareActivity = new Intent(ContactActivity.this, ShareActivity.class);
            startActivity(shareActivity);
        } else if (id == R.id.nav_contact) {
            Intent contactActivity = new Intent(ContactActivity.this, ContactActivity.class);
            startActivity(contactActivity);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // Sets onClickListeners for the buttons present in the contact activity
    // For each button an email intent is created with a subject referencing the
    // reason the getting in contact
    public void setOnClickListeners(){
        bugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","seansapps12321@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bug - NFC Writer Pal");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","seansapps12321@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Query - NFC Writer Pal");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        suggestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","seansapps12321@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion - NFC Writer Pal");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
    }
}
