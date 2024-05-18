package com.example.gpsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.Manifest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    //private double latitude, longitude;

    TextView latitudeTextView;
    TextView longitudeTextview;
    TextView addressTextView;
    TextView distanceTextView;

    TextView locationOne;

    ArrayList distance;
    private Long Starttime;
    ArrayList locations;

    TextView timeText;

    private final Handler handler = new Handler(Looper.getMainLooper());



    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};;
    private Location lastLocation;
    private float totalDistance;

    private final Runnable updateElapsedTimeTask = new Runnable() {
        @Override
        public void run() {
            // Update the elapsed time
            updateElapsedTime();
            // Post the task again with a delay of one second (1000 milliseconds)
            handler.postDelayed(this, 1000);
        }
    };







    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler.post(updateElapsedTimeTask);
        timeText = findViewById(R.id.id_Time);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        addressTextView = findViewById(R.id.id_address);
        latitudeTextView = findViewById(R.id.id_latText);
        longitudeTextview = findViewById(R.id.id_longText);
        distanceTextView = findViewById(R.id.id_distance);



        distance = new ArrayList<String>();
        locations = new ArrayList<String>();
        Starttime = SystemClock.elapsedRealtime();



        totalDistance = 0;


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                longitudeTextview.setText(String.valueOf(location.getLongitude()));
                latitudeTextView.setText(String.valueOf(location.getLatitude()));
                Starttime = SystemClock.elapsedRealtime();




                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.US);
                String addressString = "";
                try {
                    @SuppressLint("Range")
                    List<Address>addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    addressTextView.setText(addresses.get(0).getAddressLine(0).toString());
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        addressString = address.getAddressLine(0); // Get the first address line
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }



                if (lastLocation != null) {
                    float distance = lastLocation.distanceTo(location);
                    totalDistance += distance;
                }
                float totalDistanceInMiles = totalDistance * 0.000621371f;
                distanceTextView.setText(String.format(Locale.US, "%.2f miles", totalDistanceInMiles));



                lastLocation = new Location(location);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                       int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
            return;
        }
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000L, 1.0f, locationListener);

        updateElapsedTime();
    }

    private void updateElapsedTime() {
        long elapsedTime = SystemClock.elapsedRealtime() - Starttime; // Elapsed time in milliseconds

        // Convert milliseconds to hours, minutes, and seconds for display
        long hours = TimeUnit.MILLISECONDS.toHours(elapsedTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;

        // Update the TextView
        String elapsedTimeText = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        timeText.setText(elapsedTimeText);

    }
}