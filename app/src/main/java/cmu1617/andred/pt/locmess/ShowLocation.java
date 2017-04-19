package cmu1617.andred.pt.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class ShowLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String _name;
    private double _latitude;
    private double _longitude;
    private int _radius;

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
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng center = new LatLng(_latitude, _longitude);
        drawCircle(center, _radius);

    }
    public Polygon drawCircle(LatLng center, double radius) {
        // Clear the map to remove the previous circle
        mMap.clear();
        MarkerOptions marker = new MarkerOptions()
                .position(center)
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
}
