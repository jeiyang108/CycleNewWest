package com.project.lyt.cyclenewwest.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.project.lyt.cyclenewwest.Handler.HttpHandler;
import com.project.lyt.cyclenewwest.Handler.LocalStorageHandler;
import com.project.lyt.cyclenewwest.Manager.BikeWayManager;
import com.project.lyt.cyclenewwest.Helper.FavDbHelper;
import com.project.lyt.cyclenewwest.Manager.MapManager;
import com.project.lyt.cyclenewwest.Model.BikeWay;
import com.project.lyt.cyclenewwest.Model.BikeWayLine;
import com.project.lyt.cyclenewwest.Model.GreenWay;
import com.project.lyt.cyclenewwest.Model.Workout;
import com.project.lyt.cyclenewwest.R;
import com.project.lyt.cyclenewwest.Manager.WorkoutManager;
import java.util.ArrayList;

import static com.project.lyt.cyclenewwest.R.color.colorAccent;

/**
 * The main activity.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ProgressDialog pDialog;
    private Cursor cursor;
    private static GoogleMap mMap;

    private static String BIKEWAYS_URL = "http://opendata.newwestcity.ca/downloads/parks-bikeways/PARKS_BIKEWAYS.json";
    private static String GREENWAYS_URL = "http://opendata.newwestcity.ca/downloads/parks-major-greenways/PARKS_MAJOR_NAMED_GREENWAYS.json";

    private SQLiteDatabase db;
    private static ArrayList<GreenWay> favs = new ArrayList<GreenWay>();

    private static String trail_name = "Trail Name (Default)";
    private static LatLng currentLocation;

    private static TextView lbl_trail_name;
    private static Button btnAddFav;
    private static int accent;
    private static int blue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(fm.findFragmentById(R.id.frag_trails));
        ft.commit();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_maps);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setVisibility(View.GONE);

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.hide(fm.findFragmentById(R.id.frag_trails));
                ft.commit();

                clearMap();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new GetBikeWays().execute();

        lbl_trail_name = (TextView) findViewById(R.id.lbl_trail_name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(cursor != null) {
            cursor.close();
        }

        if(db != null) {
            db.close();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng newWest = new LatLng(49.2057, -122.9110);
        MarkerOptions startMarkerOptions = new MarkerOptions().position(newWest).title("Let's Bike New West!");
        Marker startMarker = mMap.addMarker(startMarkerOptions);
        MapManager.addMarker(startMarker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newWest));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
    }

    /**
     * Sets up the favourites system with the local database.
     */
    private void setupFavs() {
        accent = getResources().getColor(R.color.colorAccent);
        blue = getResources().getColor(R.color.colorBlue);

        btnAddFav = (Button) findViewById(R.id.btn_addFav);
        btnAddFav.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                setFav();
            }
        });

        favs = loadFavs();
    }

    /**
     * Sets the current location of the user.
     * @param latitude
     *          Lat of current location
     * @param longitude
     *          Long of current location
     */
    public static void setCurrentLocation(double latitude, double longitude) {
        currentLocation = new LatLng(latitude, longitude);
    }

    /**
     * Shows the given bikeway to the map.
     * @param bw
     *      The BikeWay to show.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void showBikeWay(BikeWay bw) {
        clearMap();
        trail_name = bw.getFullName();

        LatLng routeStart = bw.getAllLines().get(0).getPoints().get(0);
        MarkerOptions startMarkerOptions = new MarkerOptions().position(routeStart).title("Bikeway " + bw.getName());

        Marker startMarker = mMap.addMarker(startMarkerOptions);
        startMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.routestart));
        MapManager.addMarker(startMarker);

        for (BikeWayLine line : bw.getAllLines()) {
            PolylineOptions route = new PolylineOptions();
            route.width(5);
            route.color(Color.BLUE);

            for (LatLng point : line.getPoints()) {
                route.add(point);
            }

            Polyline polyline = mMap.addPolyline(route);
            MapManager.addPolyline(polyline);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(routeStart));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));

        //Set the trail name displayed on the screen.
        lbl_trail_name.setText(trail_name);

        if(!favs.contains(BikeWayManager.getGreenWayByName(trail_name))) {
            btnAddFav.setBackgroundTintList(ColorStateList.valueOf(accent));
        } else {
            btnAddFav.setBackgroundTintList(ColorStateList.valueOf(blue));
        }
    }

    /**
     * Clears all lines and pointers from the map.
     */
    private static void clearMap() {
        for (Polyline line : MapManager.getAllLines()) {
            line.remove();
        }
        MapManager.clearMapLines();
        for (Marker marker : MapManager.getAllMarkers()) {
            marker.remove();
        }
        MapManager.clearMarkers();
    }

    /**
     * Shows the given workout to the map.
     * @param workout
     *      The workout to show.
     */
    public static void showWorkout(Workout workout) {
        clearMap();

        for (BikeWay bw : workout.getBikeways()) {
            for (BikeWayLine bwl : bw.getAllLines()) {
                PolylineOptions route = new PolylineOptions();
                route.width(7);
                route.color(Color.argb(255, 215, 101, 90));

                for (LatLng point : bwl.getPoints()) {
                    route.add(point);
                }

                Polyline line = mMap.addPolyline(route);
                MapManager.addPolyline(line);
            }
        }

        MarkerOptions currentLocOptions = new MarkerOptions()
                .position(currentLocation)
                .title("You are here");

        Marker currentMarker = mMap.addMarker(currentLocOptions);
        MapManager.addMarker(currentMarker);

        MarkerOptions startMarkerOptions = new MarkerOptions()
                .position(WorkoutManager.getClosestLocation(workout, currentLocation))
                .title("Start workout");

        Marker startMarker = mMap.addMarker(startMarkerOptions);
        startMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.routestart));

        MapManager.addMarker(startMarker);


        mMap.moveCamera(CameraUpdateFactory.newLatLng(startMarkerOptions.getPosition()));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        FloatingActionButton fb = (FloatingActionButton) findViewById(R.id.fab);

        Intent intent;

        switch(item.getItemId()) {
            case R.id.menu_fav:
                intent = new Intent(MainActivity.this, FavouritesActivity.class);
                startActivity(intent);

                fb.setVisibility(View.VISIBLE);

                ft.show(fm.findFragmentById(R.id.frag_trails));
                ft.commit();

                return true;
            case R.id.menu_trails:
                intent = new Intent(MainActivity.this, TrailsActivity.class);
                startActivity(intent);

                fb.setVisibility(View.VISIBLE);

                ft.show(fm.findFragmentById(R.id.frag_trails));
                ft.commit();

                return true;
            case R.id.menu_workout:
                ft.hide(fm.findFragmentById((R.id.frag_trails)));
                ft.commit();

                intent = new Intent(MainActivity.this, WorkoutActivity.class);
                startActivity(intent);

                fb.setVisibility(View.VISIBLE);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Loads favourite trails (greenways) from the database.
     * @return
     *      ArrayList of favourite GreenWay objects
     */
    public ArrayList<GreenWay> loadFavs() {
        SQLiteOpenHelper helper = new FavDbHelper(this);
        ArrayList<GreenWay> favs = new ArrayList<GreenWay>();

        try {
            db = helper.getReadableDatabase();

            cursor = db.rawQuery("SELECT DISTINCT name FROM Fav", null);

            if(cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(0);
                    favs.add(BikeWayManager.getGreenWayByName(name));
                } while(cursor.moveToNext());
            }
        } catch(SQLiteException e) {
            String msg = "[MainActivity / loadFavs] DB unavailable";
            msg += "\n\n" + e.toString();

            Toast t = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            t.show();
        }

        return favs;
    }

    /**
     * Inserts a record into the favourites.
     */
    public void insertFav() {
        SQLiteOpenHelper helper = new FavDbHelper(this);

        try {
            db = helper.getReadableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", trail_name);

            db.insert("Fav", null, values);
        } catch(SQLiteException e) {
            String msg = "[MainActivity / insertFav] DB unavailable";
            msg += "/n/n" + e.toString();

            Toast t = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            t.show();
        }

        favs = loadFavs();
    }

    /**
     * Deletes a record from the favourites.
     */
    public void deleteFav() {
        SQLiteOpenHelper helper = new FavDbHelper(this);

        try {
            db = helper.getReadableDatabase();

            String sql = "DELETE FROM Fav WHERE name = '" + trail_name + "';";
            db.execSQL(sql);
        } catch(SQLiteException e) {
            String msg = "[MainActivity / deleteFav] DB unavailable";
            msg += "\n\n" + e.toString();

            Toast t = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            t.show();
        }

        favs = loadFavs();
    }

    /**
     * Gets the list of favourites.
     * @return
     *      Favourites as ArrayList of GreenWay objects.
     */
    public static ArrayList<GreenWay> getFavs() {
        return favs;
    }


    /**
     * Sets a favourite greenway.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setFav() {
        TextView lblTrailName = (TextView) findViewById(R.id.lbl_trail_name);
        String trailName = lblTrailName.getText().toString();
        GreenWay greenWay = BikeWayManager.getGreenWayByName(trailName);

        if(favs.contains(greenWay)) {
            btnAddFav.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(colorAccent)));
            deleteFav();
        } else {
            btnAddFav.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlue)));
            insertFav();
        }
    }


    /**
     * Async task to retrieve list of bikeways and greenways from JSON opendata.
     */
    private class GetBikeWays extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(BIKEWAYS_URL);
            String jsonStrGreenWays = sh.makeServiceCall(GREENWAYS_URL);

            //if no internet connection, try to read from local storage
            if (jsonStr == null) {
                jsonStr = LocalStorageHandler.getJSONFromStorage(MainActivity.this, false);
            }

            if (jsonStrGreenWays == null) {
                jsonStrGreenWays = LocalStorageHandler.getJSONFromStorage(MainActivity.this, true);
            }

            if (jsonStr != null && jsonStrGreenWays != null) {
                BikeWayManager.initializeBikeWays(jsonStr, jsonStrGreenWays);
                LocalStorageHandler.saveJSONToStorage(MainActivity.this, jsonStr, false);
                LocalStorageHandler.saveJSONToStorage(MainActivity.this, jsonStrGreenWays, true);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            WorkoutManager.initializeWorkouts();
            setupFavs();

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}