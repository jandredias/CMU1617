package cmu1617.andred.pt.locmess;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.Domain.GPSLocation;
import pt.andred.cmu1617.LocMessAPIClientImpl;

import static cmu1617.andred.pt.locmess.R.id.map;

public class NewGPSLocation extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        View.OnClickListener{

    private final static String TAG = "NewGPSLocation";

    private static int EARTH_RADIUS = 6371000;

    //These variable are initalized here as they need to be used in more than one methid
    private double currentLatitude; //lat of user
    private double currentLongitude; //long of user

    private double latitudeLisbon = 38.736946;
    private double longitudeLisbon = -9.142685;


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private LatLng position_pressed;

    private boolean marker_on_map = false;
    private float radius;

    private View _mainView;
    private View mProgressView;

    // public static final String TAG = MapsActivity.class.getSimpleName();

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int mFillColorArgb = 0x79CDCD00;
    private SQLDataStoreHelper _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_gpslocation);
        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        // Create the LocationRequest object
        View plusButton = findViewById(R.id.add_location);
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        ((FloatingActionButton) plusButton).setOnClickListener(this);

        View google_maps = findViewById(R.id.map);

        _mainView = findViewById(R.id.main_view_add_gps_location);

        mProgressView = findViewById(R.id.add_gps_location_progress);

        _db = new SQLDataStoreHelper(getApplicationContext());

    }

    /*These methods all have to do with the map and wht happens if the activity is paused etc*/
    //contains lat and lon of another marker
    private void setUpMap() {

        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitudeLisbon, longitudeLisbon)).title("1"); //create marker
        mMap.addMarker(marker); // adding marker
    }

    //contains your lat and lon
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        position_pressed = new LatLng(currentLatitude, currentLongitude);

       /* MarkerOptions options = new MarkerOptions()
                ._position(position_pressed)
                .title("You are here");
        mMap.addMarker(options);*/
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((position_pressed), 15.0F));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(map)).getMapAsync(this);

            // Check if we were successful in obtaining the map.
           /* if (mMap != null) {
                setUpMap();
            }*/

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                handleNewLocation(location);
                Log.e(TAG, "passed here!");
            }
        }
        Log.d(TAG, "Permission is " + mLocationPermissionGranted);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
       // handleNewLocation(location);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                onMapClick(latLng);
            }
        });
       /* if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            handleNewLocation(location);
        }*/
        /*if(!mLocationPermissionGranted) {
            MarkerOptions marker = new MarkerOptions()._position(new LatLng(latitudeLisbon, longitudeLisbon)).title("1"); //create marker
            mMap.addMarker(marker); // adding marker
        }*/
    }

    private double pointsDistance(Point p1, Point p2) {
        int dx = p1.x - p2.x;
        int dy = p1.y - p2.y;

        double distance = Math.sqrt((dx * dx) + (dy * dy));
        return distance ;
    }

    private LatLng getRadiusPoint(LatLng center) {
        View view = getSupportFragmentManager().findFragmentById(R.id.map).getView();

        Point center_point = mMap.getProjection().toScreenLocation(center);
        int x = center_point .x;
        int y = center_point .y;

        Log.d(TAG,"x: "+ x+"");
        Log.d(TAG,"y: " +y+"");
        Log.d(TAG,"alto h: " + view.getHeight() * 24 / 25 + "");
        Log.d(TAG,"baixo h: " +view.getHeight() * 1 / 25 + "");
        Log.d(TAG,"alto w: "+view.getWidth() * 49 / 50 + "");
        Log.d(TAG,"baixo w:" +view.getWidth() * 1 / 50 + "");


        int posicao = 0;
        double distancia;
        double distancia_min;




//        LatLng cima     = mMap.getProjection().fromScreenLocation(new Point(x, view.getHeight() * 1 / 25));
//        distancia_min = calculationByDistance(center,cima);


        LatLng direita  = mMap.getProjection().fromScreenLocation(new Point(view.getWidth() * 49 / 50,y));
        distancia = calculationByDistance(center,direita);
        distancia_min = distancia;
        if(distancia < distancia_min) {
            distancia_min = distancia;
            posicao = 1;
        }

//        LatLng baixo    = mMap.getProjection().fromScreenLocation(new Point(x, view.getHeight() * 24 / 25));
//        distancia = calculationByDistance(center,baixo);
//        if(distancia < distancia_min) {
//            distancia_min = distancia;
//            posicao = 2;
//        }
        LatLng esquerda = mMap.getProjection().fromScreenLocation(new Point(view.getWidth() * 1 / 50,y));
        distancia = calculationByDistance(center,esquerda);
        if(distancia < distancia_min) {
            distancia_min = distancia;
            posicao = 3;
        }
        Log.wtf(TAG,posicao+"");
        switch (posicao) {
//            case 0: return   cima;
            case 1: return  direita;
//            case 2: return  baixo;
            case 3: return  esquerda;

        }
        return direita;

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "pressed on: " + latLng.latitude + " " + latLng.longitude);

        mMap.clear();
        position_pressed = latLng;
        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.selected_city));
        mMap.addMarker(marker);


        LatLng radiusLatLng = getRadiusPoint(position_pressed);
        radius = (float) toRadiusMeters(position_pressed, radiusLatLng);
        drawCircle(position_pressed, radius);
        marker_on_map=false;
//        if(marker_on_map){
//            float a = calculationByDistance(position_pressed, latLng);
//
//            radius = a;
//            return;
//        }
    }
    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }
    protected void setName(String name, boolean exists){
        setName(name, exists, Math.round(radius));
    }


    protected void setName(String name, boolean exists, int r) {
        if (exists) {
            Log.d(TAG, "setName //name= " + name + " //latitude= " + String.valueOf(position_pressed.latitude) + " //longitude= " + String.valueOf(position_pressed.longitude) +
            " //radius= " + r);
            new NewGPSLocationAsync().execute(name, String.valueOf(position_pressed.latitude), String.valueOf(position_pressed.longitude), String.valueOf(r));
            //LocMessAPIClientImpl.getInstance().newGPSLocation(name, String.valueOf(position_pressed.latitude), String.valueOf(position_pressed.longitude), "10");
        } else
            Toast.makeText(getBaseContext(), R.string.give_name_location, Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "Clicked on marker");
        return false;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add_location:
                new NameDialog(this).show(getSupportFragmentManager(), TAG);
        }
    }

    public Polygon drawCircle(LatLng center, double radius) {
        // Clear the map to remove the previous circle
        mMap.clear();
        MarkerOptions marker = new MarkerOptions()
                .position(position_pressed)
                .title(getString(R.string.selected_city));
        mMap.addMarker(marker);

        // Generate the points
        List<LatLng> points = new ArrayList<>();
        int totalPonts = 40; // number of corners of the pseudo-circle
        for (int i = 0; i < totalPonts; i++) {
            points.add(getPoint(center, radius, i*2*Math.PI/totalPonts));
        }
        // Create and return the polygon
        return mMap.addPolygon(new PolygonOptions().addAll(points).strokeWidth(5).strokeColor(0x700a420b).fillColor(mFillColorArgb));
    }

    private LatLng getPoint(LatLng center, double radius, double angle) {
        // Get the coordinates of a circle point at the given angle
        double east = radius * Math.cos(angle);
        double north = radius * Math.sin(angle);

        double cLat = center.latitude;
        double cLng = center.longitude;
        double latRadius = EARTH_RADIUS * Math.cos(cLat / 180 * Math.PI);

        double newLat = cLat + (north / EARTH_RADIUS / Math.PI * 180);
        double newLng = cLng + (east / latRadius / Math.PI * 180);

        return new LatLng(newLat, newLng);
    }

    public float calculationByDistance(LatLng latLongA, LatLng latLongB) {
        Location lA = new Location("A");
        lA.setLatitude(latLongA.latitude);
        lA.setLongitude(latLongA.longitude);
        Location lB = new Location("B");
        lB.setLatitude(latLongB.latitude);
        lB.setLongitude(latLongB.longitude);

        return lA.distanceTo(lB);
    }

    /*public float[] calculationByDistance(LatLng latLongA, LatLng latLongB) {
        float[] results = new float[1];
        Location.distanceBetween(latLongA.latitude, latLongB.longitude,
                latLongB.latitude, latLongB.longitude,
                results);
        Log.d(TAG, "distance between two points: " + results[0]);
        return results[0];
    }*/
   /* public double calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }*/

    @SuppressLint("ValidFragment")
    public static class NameDialog extends DialogFragment {

        private NewGPSLocation _n;

        public NameDialog(NewGPSLocation n) {
            _n = n;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View v = inflater.inflate(R.layout.name_location_dialog, null);

            final EditText editText = (EditText) v.findViewById(R.id.written_text);
            //v.findViewById(R.id.radius1).setVisibility(View.VISIBLE);
            //v.findViewById(R.id.radius2).setVisibility(View.VISIBLE);
//            final NumberPicker picker = (NumberPicker) v.findViewById(R.id.picker);
//            picker.setMaxValue(100);
//            picker.setMinValue(1);
//            picker.setVisibility(View.VISIBLE);

            builder.setView(v)
                    // Add action buttons
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            getDialog().cancel();
                            String text = editText.getText().toString();
                            if (text.isEmpty() || text.contentEquals(" "))
                                _n.setName(null, false, 0);
                            else
                                _n.setName(text, true);

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            _n.setName(null, false, 0);
                            getDialog().cancel();
                        }
                    });
            return builder.create();

        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            _mainView.setVisibility(show ? View.GONE : View.VISIBLE);
            _mainView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                    _mainView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
                });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            _mainView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    class NewGPSLocationAsync extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
                onBackPressed();
            } else {
                final Snackbar snackbar = Snackbar.make(_mainView, "Could not connect to server", Snackbar.LENGTH_LONG);
                snackbar.show();
                snackbar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String location_id;
            try {
                location_id = LocMessAPIClientImpl.getInstance().addLocation(params[0], params[1], params[2], params[3]);
                new GPSLocation(_db,location_id).completeObject(params[0], Double.parseDouble(params[1]), Double.parseDouble(params[2]), Integer.parseInt(params[3]),"1");

                return true;
            }catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "not sent to server, there was an exception");
                return false;
            }
        }
    }
}