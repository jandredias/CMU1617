package cmu1617.andred.pt.locmess;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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


    // public static final String TAG = MapsActivity.class.getSimpleName();

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

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
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        ((FloatingActionButton) findViewById(R.id.add_location)).setOnClickListener(this);
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

        MarkerOptions options = new MarkerOptions()
                .position(position_pressed)
                .title("You are here");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((position_pressed), 11.0F));
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
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                handleNewLocation(location);
            }
        }
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
        handleNewLocation(location);
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
        /*if(!mLocationPermissionGranted) {
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitudeLisbon, longitudeLisbon)).title("1"); //create marker
            mMap.addMarker(marker); // adding marker
        }*/
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "pressed on: " + latLng.latitude + " " + latLng.longitude);
        if(marker_on_map){
            float a = calculationByDistance(position_pressed, latLng);
            drawCircle(position_pressed, (double) a);
            radius = a;
            marker_on_map=false;
            return;
        }
        position_pressed = latLng;
        mMap.clear();
        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.selected_city));

        mMap.addMarker(marker);
        marker_on_map=true;


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
        int totalPonts = 30; // number of corners of the pseudo-circle
        for (int i = 0; i < totalPonts; i++) {
            points.add(getPoint(center, radius, i*2*Math.PI/totalPonts));
        }
        // Create and return the polygon
        return mMap.addPolygon(new PolygonOptions().addAll(points).strokeWidth(2).strokeColor(0x700a420b));
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

    public static class NewGPSLocationAsync extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            JSONObject result;
            try {
                result = LocMessAPIClientImpl.getInstance().newGPSLocation(params[0], params[1], params[2], params[3]);

                if(result.getInt("status") != 200){
                    Log.e(TAG, "Error in sending to server, status= " + result.getInt("status"));
                }
            }catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "not sent to server, there was an exception");
            }
            Log.d(TAG, "Sent to server and everything is fine");
            return null;
        }
    }
}