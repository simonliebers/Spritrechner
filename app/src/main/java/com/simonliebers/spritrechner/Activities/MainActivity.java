package com.simonliebers.spritrechner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.simonliebers.spritrechner.General.Constants;
import com.simonliebers.spritrechner.R;

public class MainActivity extends AppCompatActivity {

    SharedPreferences pref;
    ProgressBar spinner;

    String gas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);

        pref = getApplicationContext().getSharedPreferences(Constants.DataPrefs, 0);

        gas = pref.getString(Constants.GasKey, null);

        nextActivity();
    }

    private void nextActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gas != null) {
                    Intent appIntent = new Intent(MainActivity.this, AppActivity.class);
                    startActivity(appIntent);
                } else {
                    Intent dataIntent = new Intent(MainActivity.this, DataActivity.class);
                    startActivity(dataIntent);
                }
            }
        }, 4000);

    }
}
