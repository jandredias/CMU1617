package cmu1617.andred.pt.locmess;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class ShowLocation extends FragmentActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    private Marker myLocation;
    private GoogleApiClient mGoogleApiClient;
    private final static String TAG = "ShowLocation";
    private String _name;
    private double _latitude;
    private double _longitude;
    private int _radius;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


    private LocationRequest mLocationRequest;

    private static int EARTH_RADIUS = 6371000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent i = getIntent();
        _name = i.getStringExtra("name");
        _latitude = i.getDoubleExtra("latitude", 0);
        _longitude = i.getDoubleExtra("longitude", 0);
        _radius = i.getIntExtra("radius", 1);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);




        myLocation = mMap    .addMarker(new MarkerOptions()
                .position(new LatLng( 65.07213,-2.109375))
                .title(getString(R.string.your_location))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


        LatLng center = new LatLng(_latitude, _longitude);
        drawCircle(center, _radius);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((center), 15.0F));

    }
    public Polygon drawCircle(LatLng center, double radius) {
        // Clear the map to remove the previous circle
        MarkerOptions marker = new MarkerOptions()
                .position(center)
                .title(_name);
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

    @Override
    public void onLocationChanged(Location location) {


        //myLocation.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        boolean mLocationPermissionGranted=false;
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
                onLocationChanged(location);
            }
        }
        Log.d(TAG, "Permission is " + mLocationPermissionGranted);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
