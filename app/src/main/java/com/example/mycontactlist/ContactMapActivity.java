package com.example.mycontactlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    ImageButton ibList, ibMap, ibSettings;
    final int PERMISSION_REQUEST_LOCATION = 101;
    GoogleMap gMap;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    ArrayList<Contact> contacts = new ArrayList<>();
    Contact currentContact = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_map);
        initMapTypeButtons();
        getMapData();

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(ContactMapActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ContactMapActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Snackbar.make(findViewById(R.id.activity_contact_map), "MyContactList requires this permission to locate",
                                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ActivityCompat.requestPermissions(
                                                ContactMapActivity.this,
                                                new String[]{
                                                        Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                                    }
                                })
                                .show();
                    } else {
                        ActivityCompat.requestPermissions(ContactMapActivity.this,
                                new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_LOCATION);
                    }
                } else {
                    startMap();
                }
            } else {
                startMap();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        createLocationRequest();
        createLocationCallback();

        initListButton();
        initMapButton();
        initSettingsButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getBaseContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        gMap.setMyLocationEnabled(true);
    }

    private void stopLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void startMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        getMapData();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                if (ContextCompat.checkSelfPermission(ContactMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    gMap = googleMap;
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    startLocationUpdates();
                    Point size = new Point();
                    WindowManager w = getWindowManager();
                    w.getDefaultDisplay().getSize(size);
                    int measureWidth = size.x;
                    int measureHeight = size.y;
                    if(contacts.size()>0){
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for(int i=0; i<contacts.size(); i++){
                            currentContact = contacts.get(i);

                            Geocoder geo = new Geocoder(ContactMapActivity.this);
                            List<Address> addresses = null;

                            String address = currentContact.getStreetAddress() +
                                    ", "+currentContact.getCity() +
                                    ", " + currentContact.getState() + " " +
                                    currentContact.getZipCode();
                            try{
                                addresses = geo.getFromLocationName(address, 1);
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                            LatLng point = new LatLng(addresses.get(0).getLatitude(),
                                    addresses.get(0).getLongitude());
                            builder.include(point);
                            gMap.addMarker(new MarkerOptions().position(point)).setTitle(currentContact.getContactName());
                        }
                        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),measureWidth,measureHeight,450));
                    }
                    else{
                        if(currentContact != null){
                            Geocoder geo = new Geocoder(ContactMapActivity.this);
                            List<Address> addresses = null;
                            String address = currentContact.getStreetAddress() +
                                    ", "+currentContact.getCity() +
                                    ", " + currentContact.getState() + " " +
                                    currentContact.getZipCode();
                            try{
                                addresses = geo.getFromLocationName(address, 1);
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                            LatLng point = new LatLng(addresses.get(0).getLatitude(),
                                    addresses.get(0).getLongitude());
                            gMap.addMarker(new MarkerOptions().position(point)).setTitle(currentContact.getContactName());
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
                        }
                        else{
                            AlertDialog alertDialog = new AlertDialog.Builder(ContactMapActivity.this).create();
                            alertDialog.setTitle("No Data");
                            alertDialog.setMessage("No Data is available for the mapping function.");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            alertDialog.show();
                        }
                    }
                }
            }
        });
    }

    private void getMapData() {
        Bundle extras = getIntent().getExtras();
        try {
            ContactDataSource ds = new ContactDataSource(ContactMapActivity.this);
            ds.open();
            if (extras != null) {
                currentContact = ds.getSpecificContact(extras.getInt("contactid"));
            } else {
                contacts = ds.getContacts(ContactDBHelper.NAME, "ASC");
            }
            ds.close();
        } catch (Exception e) {
            Toast.makeText(this,"Contact(s) cound not be retrived.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(ContactMapActivity.this, "MyContactList will not locate your contacts.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestPriority(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return ;
                }
                for (Location location : locationResult.getLocations()) {
                    Toast.makeText(getBaseContext(), "Lat: " + location.getLatitude() + " Long: " + location.getLongitude() + " Accuracy: " + location.getAccuracy(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void initMapTypeButtons() {
        RadioGroup mapTypeGroup = findViewById(R.id.map_type);
        mapTypeGroup.check(R.id.mapTypenormal);
        mapTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.mapTypenormal:
                        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case R.id.mapTypeSatellite:
                        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                }
            }
        });
    }

    private void initListButton () {
        ibList = findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactMapActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    private void initMapButton () {
        ibMap = findViewById(R.id.imageButtonMap);
        ibMap.setEnabled(false);
    }

    private void initSettingsButton () {
        ibSettings = findViewById(R.id.imageButtonSettings);
        ibSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactMapActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}