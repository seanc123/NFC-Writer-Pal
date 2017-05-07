package com.apps.seanc.nfcwriterpal.ActivityHandlers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import com.apps.seanc.nfcwriterpal.R;

/*
 * Created by seanc on 12/02/2017
 *
 * This class is used for providing the logic of the launcher activity for this application.
 *
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getName();

    private Button writeButton, readButton, formatButton, shareButton, contactButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        writeButton = (Button) findViewById(R.id.writeButton);
        readButton = (Button) findViewById(R.id.readButton);
        formatButton = (Button) findViewById(R.id.main_btn_format);
        shareButton = (Button) findViewById(R.id.main_btn_share);
        contactButton = (Button) findViewById(R.id.main_btn_contact);
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
            Intent writeActivity = new Intent(MainActivity.this, WriteActivity.class);
            startActivity(writeActivity);
        } else if (id == R.id.nav_read) {
            Intent readActivityIntent = new Intent(MainActivity.this, ReadActivity.class);
            startActivity(readActivityIntent);
        } else if (id == R.id.nav_format) {
            Intent formatActivity = new Intent(MainActivity.this, FormatActivity.class);
            startActivity(formatActivity);
        } else if (id == R.id.nav_share) {
            Intent shareActivity = new Intent(MainActivity.this, ShareActivity.class);
            startActivity(shareActivity);
        } else if (id == R.id.nav_contact) {
            Intent contactActivity = new Intent(MainActivity.this, ContactActivity.class);
            startActivity(contactActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Sets the onClickListeners for each of the buttons on the main activity
    private void setOnClickListeners(){

        writeButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view){
                Intent writeActivity = new Intent(MainActivity.this, WriteActivity.class);
                startActivity(writeActivity);
            }
        });

        readButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                Intent readActivityIntent = new Intent(MainActivity.this, ReadActivity.class);
                startActivity(readActivityIntent);
            }

        });

        formatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent formatActivity = new Intent(MainActivity.this, FormatActivity.class);
                startActivity(formatActivity);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareActivity = new Intent(MainActivity.this, ShareActivity.class);
                startActivity(shareActivity);
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactActivity = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(contactActivity);
            }
        });

    }



}
