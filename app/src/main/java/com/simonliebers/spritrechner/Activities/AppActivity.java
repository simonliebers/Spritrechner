package com.simonliebers.spritrechner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.simonliebers.spritrechner.General.Constants;
import com.simonliebers.spritrechner.General.DetailDialog;
import com.simonliebers.spritrechner.General.Position;
import com.simonliebers.spritrechner.General.StationListAdapter;
import com.simonliebers.spritrechner.R;
import com.simonliebers.spritrechner.Webservice.API.OnResultListener;
import com.simonliebers.spritrechner.Webservice.API.TankstellenAPI;
import com.simonliebers.spritrechner.Webservice.Station;
import com.simonliebers.spritrechner.Webservice.Tankstellen;

import java.util.ArrayList;

public class AppActivity extends AppCompatActivity implements OnResultListener {

    ListView listView;
    TankstellenAPI api;
    Tankstellen tankstellen;

    Dialog dialog;

    ImageButton changeSort;
    Button changeType;

    CheckBox checkSuper;
    CheckBox checkE10;
    CheckBox checkDiesel;

    CheckBox checkPrice;
    CheckBox checkDistance;

    SharedPreferences pref;
    Constants.Type type = Constants.Type.e5;
    Constants.Sort sort = Constants.Sort.price;

    private LocationManager locationManager;
    private LocationListener listener;

    ProgressBar spinner;
    ImageView spinnerImage;
    TextView loadingText;
    ConstraintLayout loadingContainer;

    Location latestLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        listView = findViewById(R.id.listView);
        spinner = findViewById(R.id.spinner);
        spinnerImage = findViewById(R.id.spinnerImage);
        loadingText = findViewById(R.id.loadingText);
        loadingContainer = findViewById(R.id.loadingContainer);
        changeSort = findViewById(R.id.changeSort);
        changeType = findViewById(R.id.changeType);

        if(type == Constants.Type.e10){
            changeType.setText("E10");
        } else if(type == Constants.Type.e5){
            changeType.setText("E5");
        } else {
            changeType.setText("D");
        }

        changeSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowpopupSort(view);
            }
        });

        changeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowpopupType(view);
            }
        });

        startLocationUpdates();

        pref = getApplicationContext().getSharedPreferences(Constants.DataPrefs, 0);
        type = Constants.Type.valueOf(pref.getString(Constants.GasKey, null));

        api = new TankstellenAPI(this);
    }


    @Override
    public void onBackPressed() {

    }


    public void populateListView(Station[] tankstellen){
        System.out.println("populateListView: Displaying Data in ListView");

        final ArrayList<Station> listData = new ArrayList<>();

        for (Station tankstelle:tankstellen) {
            listData.add(tankstelle);
        }

        StationListAdapter adapter = new StationListAdapter(this, R.layout.listview_cell, listData);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = listData.get(i).getID();
                System.out.println("onItemClick: You Clicked on ID: " + id);

                onShowpopupDetails(view, listData.get(i));

            }
        });
    }

    public void onShowpopupType(View v){
        if(dialog != null){
            if(dialog.isShowing()){
                return;
            }
        }
        dialog = new Dialog(AppActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choosetype_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        ImageButton close = dialog.findViewById(R.id.closePopup);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button cancel = dialog.findViewById(R.id.popupCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button submit = dialog.findViewById(R.id.popupSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        checkSuper = dialog.findViewById(R.id.checkSuper);
        checkE10 = dialog.findViewById(R.id.checkE10);
        checkDiesel = dialog.findViewById(R.id.checkDiesel);

        if(type == Constants.Type.e5){
            checkSuper.setChecked(true);
        } else if(type == Constants.Type.e10){
            checkE10.setChecked(true);
        } else {
            checkDiesel.setChecked(true);
        }

        checkSuper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateType(Constants.Type.e5);
            }
        });

        checkDiesel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateType(Constants.Type.diesel);
            }
        });

        checkE10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateType(Constants.Type.e10);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, size.y / 2);
        window.setGravity(Gravity.BOTTOM);

        dialog.show();
    }

    public void onShowpopupSort(View v){
        if(dialog != null){
            if(dialog.isShowing()){
                return;
            }
        }
        dialog = new Dialog(AppActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choosesort_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        ImageButton close = dialog.findViewById(R.id.closePopup);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button cancel = dialog.findViewById(R.id.popupCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button submit = dialog.findViewById(R.id.popupSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        checkDistance = dialog.findViewById(R.id.checkDistance);
        checkPrice = dialog.findViewById(R.id.checkPrice);

        if(sort == Constants.Sort.price){
            checkPrice.setChecked(true);
        } else {
            checkDistance.setChecked(true);
        }

        checkDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSort(Constants.Sort.dist);
            }
        });

        checkPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSort(Constants.Sort.price);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, size.y / 2);
        window.setGravity(Gravity.BOTTOM);

        dialog.show();
    }

    private void onShowpopupDetails(View v, Station station){
        if(dialog != null){
            if(dialog.isShowing()){
                return;
            }
        }

        dialog = new DetailDialog(AppActivity.this, station, v);
        dialog.show();
    }

    private void startLocationUpdates() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latestLocation = location;
                tankstellen = api.getTankstellen(new Position(latestLocation.getLatitude(), latestLocation.getLongitude()), type, sort);

                if(latestLocation != null){
                    changeLoadingState(LoadingState.stations);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 10, listener);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                                ,10);
                    }
                    return;
                }
                locationManager.requestLocationUpdates("gps", 5000, 10, listener);
                break;
            default:
                break;
        }
    }

    @Override
    public void callback(Tankstellen t) {
        if(t != null){
            populateListView(t.getStations());
            loadingContainer.setVisibility(View.INVISIBLE);
        } else {
            changeLoadingState(LoadingState.no);
        }

    }

    private enum LoadingState{
        gps, stations, no, done
    }

    private void updateSort(Constants.Sort sort){
        if(sort == Constants.Sort.dist){
            checkDistance.setChecked(true);
            checkPrice.setChecked(false);
            this.sort = sort;
            changeSort.setImageResource(R.drawable.ic_distance);

            if(latestLocation != null){
                changeLoadingState(LoadingState.stations);
                tankstellen = api.getTankstellen(new Position(latestLocation.getLatitude(), latestLocation.getLongitude()), this.type, this.sort);
            }
        } else {
            checkDistance.setChecked(false);
            checkPrice.setChecked(true);
            this.sort = sort;
            changeSort.setImageResource(R.drawable.ic_price);

            if(latestLocation != null){
                changeLoadingState(LoadingState.stations);
                tankstellen = api.getTankstellen(new Position(latestLocation.getLatitude(), latestLocation.getLongitude()), this.type, this.sort);
            }
        }
    }

    private void updateType(Constants.Type type){
        if(type == Constants.Type.e5){
            checkE10.setChecked(false);
            checkDiesel.setChecked(false);
            checkSuper.setChecked(true);
            this.type = type;
            changeType.setText("E5");

            if(latestLocation != null){
                changeLoadingState(LoadingState.stations);
                tankstellen = api.getTankstellen(new Position(latestLocation.getLatitude(), latestLocation.getLongitude()), this.type, this.sort);
            }
        } else if(type == Constants.Type.e10){
            checkE10.setChecked(true);
            checkDiesel.setChecked(false);
            checkSuper.setChecked(false);
            this.type = type;
            changeType.setText("E10");

            if(latestLocation != null){
                changeLoadingState(LoadingState.stations);
                tankstellen = api.getTankstellen(new Position(latestLocation.getLatitude(), latestLocation.getLongitude()), this.type, this.sort);
            }
        } else if(type == Constants.Type.diesel){
            checkE10.setChecked(false);
            checkDiesel.setChecked(true);
            checkSuper.setChecked(false);
            this.type = type;
            changeType.setText("D");

            if(latestLocation != null){
                changeLoadingState(LoadingState.stations);
                tankstellen = api.getTankstellen(new Position(latestLocation.getLatitude(), latestLocation.getLongitude()), this.type, this.sort);
            }
        }
    }

    private void changeLoadingState(LoadingState state){
        if(state == LoadingState.gps){
            loadingText.setText("Waiting for GPS Position");
            spinnerImage.setImageResource(R.drawable.ic_gps);
            spinner.setVisibility(View.VISIBLE);
            spinnerImage.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
        } else if(state == LoadingState.stations){
            loadingText.setText("Loading Stations");
            spinnerImage.setImageResource(R.drawable.ic_fuel);
            spinner.setVisibility(View.VISIBLE);
            spinnerImage.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
        } else if(state == LoadingState.no){
            loadingText.setText("No stations found");
            spinner.setVisibility(View.INVISIBLE);
            spinnerImage.setVisibility(View.INVISIBLE);
            loadingText.setVisibility(View.VISIBLE);
        } else if(state == LoadingState.done){
            spinner.setVisibility(View.INVISIBLE);
            spinnerImage.setVisibility(View.INVISIBLE);
            loadingText.setVisibility(View.INVISIBLE);
        }
    }
}
