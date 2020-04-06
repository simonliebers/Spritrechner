package com.simonliebers.spritrechner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
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
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.JsonElement;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.BaseMarkerOptions;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
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

import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

public class AppActivity extends AppCompatActivity implements OnResultListener {

    private enum DisplayMode{
        Map, List
    }

    ListView listView;
    TankstellenAPI api;
    Tankstellen tankstellen;
    Station[] stations;

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
    ConstraintLayout bottomLayout;
    Button changeDisplayMode;

    private MapView mapView;
    private MapboxMap mapboxMap;

    DisplayMode displayMode = DisplayMode.List;

    Location latestLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, Constants.MapBoxAccessToken);
        setContentView(R.layout.activity_app);

        mapView = findViewById(R.id.mapView);
        listView = findViewById(R.id.listView);
        spinner = findViewById(R.id.spinner);
        spinnerImage = findViewById(R.id.spinnerImage);
        loadingText = findViewById(R.id.loadingText);
        loadingContainer = findViewById(R.id.loadingContainer);
        changeSort = findViewById(R.id.changeSort);
        changeType = findViewById(R.id.changeType);
        changeDisplayMode = findViewById(R.id.changeDisplayMode);
        bottomLayout = findViewById(R.id.bottomLayout);

        mapView.onCreate(savedInstanceState);
        initializeMap(null);

        listView.setVisibility(View.INVISIBLE);
        mapView.setVisibility(View.INVISIBLE);
        bottomLayout.setVisibility(View.INVISIBLE);

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

        changeDisplayMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(displayMode == DisplayMode.Map){
                    updateDisplayMode(DisplayMode.List);
                } else {
                    updateDisplayMode(DisplayMode.Map);
                }
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
            if(tankstelle.getPrice() != 0.0){
                listData.add(tankstelle);
            }
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
            initializeMap(t.getStations());
            updateDisplayMode(displayMode);

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

    private void updateDisplayMode(DisplayMode mode){
        displayMode = mode;
        if(mode == DisplayMode.Map){
            mapView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);

            changeType.setVisibility(View.INVISIBLE);
            changeSort.setVisibility(View.INVISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
            changeDisplayMode.setText("SHOW LIST");

        } else {
            changeType.setVisibility(View.VISIBLE);
            changeSort.setVisibility(View.VISIBLE);

            mapView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
            changeDisplayMode.setText("SHOW MAP");
        }
    }

    private void initializeMap(final Station[] stations){
        this.stations = stations;
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        IconFactory iconFactory = IconFactory.getInstance(AppActivity.this);
                        Icon icon = iconFactory.fromResource(R.drawable.gps_map);
                        Icon position = iconFactory.fromResource(R.drawable.pos_map);

                        mapboxMap.getUiSettings().setCompassEnabled(false);

                        if(stations != null){
                            for(Station station : stations){
                                mapboxMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(station.getLat(), station.getLng()))
                                        .icon(icon)
                                        .title(station.getID()));
                            }
                        }

                        if(latestLocation != null){
                            mapboxMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latestLocation.getLatitude(), latestLocation.getLongitude()))
                                    .icon(position)
                                    .title("latestPosition"));

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(latestLocation.getLatitude(), latestLocation.getLongitude()))
                                    .zoom(10)
                                    .tilt(20)
                                    .build();
                            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000);
                        }

                        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                Station clicked = null;
                                for(Station station : stations){
                                    if(station.getID().equals(marker.getTitle())){
                                        clicked = station;
                                    }
                                }

                                if(clicked != null){
                                    onShowpopupDetails(mapView, clicked);
                                }

                                return true;
                            }
                        });

                    }
                });

            }
        });
    }

}
