package com.project.lyt.cyclenewwest.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.project.lyt.cyclenewwest.Model.Workout;
import com.project.lyt.cyclenewwest.R;
import com.project.lyt.cyclenewwest.Manager.WorkoutManager;


/**
 * Activity showing hardcoded workouts -- circular trails of bikeways around New West.
 */
public class WorkoutActivity extends AppCompatActivity implements LocationListener {
    private static final long MIN_TIME_BW_UPDATES = 10000;  //minimum time interval between location updates, in milliseconds
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000; //minimum distance between location updates, in meters
    LocationManager locationManager;
    Boolean isGPSEnabled;
    Boolean isNetworkEnabled;
    Boolean canGetLocation;
    double latitude, longitude;
    Location location;
    TextView input_workout_start;
    Spinner input_workout_length;
    Button route_button;
    //private FusedLocationProviderClient mFusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        input_workout_start = (TextView) findViewById(R.id.input_workout_start);
        input_workout_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        input_workout_length = (Spinner) findViewById(R.id.input_workout_length);
        route_button = (Button) findViewById(R.id.btn_workout_route);
        route_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.setCurrentLocation(latitude, longitude);
                Workout.WorkoutLength workoutLength;
                switch (input_workout_length.getSelectedItemPosition()) {
                    case 0:
                        workoutLength = Workout.WorkoutLength.LONG;
                        break;
                    case 1:
                        workoutLength = Workout.WorkoutLength.MEDIUM;
                        break;
                    default :
                        workoutLength = Workout.WorkoutLength.SHORT;
                }
                MainActivity.showWorkout(WorkoutManager.getWorkoutByLength(workoutLength));
                finish();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d("WorkoutActivity", "getSystemService/ findCurrentLocation method");

        //It will show a DialogBox in which user will choose whether they authorize app to use location or not.
        //Calls onRequestPermissionResult method.
        ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION}, 1);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                    findCurrentLocation();
                } else {

                    // Permission denied. Disable the functionality that depends on this permission.
                }
                return;
            }

            // Other 'case' lines to check for other permissions this app might request
        }
    }

    /**
     * Gets the phone's current location and displays the coordinates in a textview.
     */
    public void findCurrentLocation() {
        checkPermission();

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
            Log.d("WorkoutActivity", "No network provider is enabled");
        } else {
            this.canGetLocation = true;

            if (isNetworkEnabled) {

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                Log.d("WorkoutActivity", "LOC Network Enabled");
                if (locationManager != null) {

                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        Log.d("WorkoutActivity", "LOC by Network");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        input_workout_start.setText("NETWORK: Latitude: " + latitude + " Longitude: " + longitude);
                    }

                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("WorkoutActivity", "RLOC: GPS Enabled");
                    if (locationManager != null) {
                        Log.d("WorkoutActivity", "GPS Enabled. locationManager != NULL");


                        //==============Cannot get last known location.============
                        //getLastKnownLocation returns null.
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


                        if (location != null) {
                            Log.d("WorkoutActivity", "RLOC: loc by GPS");

                            latitude = location.getLatitude(); //get the current latitude
                            longitude = location.getLongitude(); //get the current longitude
                            input_workout_start.setText("GPS: Latitude: " + latitude + " Longitude: " + longitude);
                        } else {
                            input_workout_start.setText("GPS: location not found.");
                        }
                    }
                }
            }
        }


    }
    @Override
    public void onLocationChanged(Location location) {
        findCurrentLocation();
        input_workout_start.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(WorkoutActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    // this method name should not be changed.
    public void checkPermission() {
        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


}
