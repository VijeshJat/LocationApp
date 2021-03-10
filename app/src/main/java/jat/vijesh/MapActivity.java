package jat.vijesh;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private UserPreference mUserPreference;
    private LocationClient locationClient;
    private GoogleMap map;
    private Marker mMarker;
    private boolean isIncreaseTimeInterval = true;
    private boolean isDecreaseTimeInterval = false;
    public static final int REQUEST_CODE_PERMISSIONS = 101;


    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            Location location = locationResult.getLastLocation();

            if (location != null && location.hasAccuracy()) {

                float accuracy = location.getAccuracy();

                Log.d("TESTING", " onLocationResult()  Latitude -- " + location.getLatitude() + "  , Longitude -- " + location.getLongitude() + "  , accuracy -- " +accuracy);

                if (accuracy >= 500)
                    return;


                if (accuracy < 100) {
                    isDecreaseTimeInterval = true;
                    if (isIncreaseTimeInterval) {
                        isIncreaseTimeInterval = false;

                        locationClient.updateLocationRequest(240000,240000);
                        locationClient.startLocationUpdates(locationClient.createLocationRequest(), mLocationCallback);

                        Log.d("TESTING", " --  set interval to 4 minute -   -- ");
                    }
                } else {
                    isIncreaseTimeInterval = true;
                    if (isDecreaseTimeInterval) {
                        isDecreaseTimeInterval = false;
                        locationClient.updateLocationRequest(4000,4000);
                        locationClient.startLocationUpdates(locationClient.createLocationRequest(), mLocationCallback);
                        Log.d("TESTING", " -- set interval to 4 sec - ");
                    }
                }

                if (map != null) {
                    LatLng position = new LatLng(mUserPreference.getCurrentLatitude(), mUserPreference.getCurrentLongitude());
                    mMarker.setPosition(position);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));


                }

                mUserPreference.setCurrentLatitude(location.getLatitude());
                mUserPreference.setCurrentLongitude(location.getLongitude());
                mUserPreference.savePreference(MapActivity.this);

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationClient = LocationClient.getInstance(this);
        mUserPreference = UserPreference.getInstance(this);
        requestLocationPermission();

        setUpGeoFenceMap();

        //mapGeofence
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    private void requestLocationPermission() {

        boolean foreground = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (foreground) {
            boolean background = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

            if (background) {
                handleLocationUpdates();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE_PERMISSIONS);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {

            boolean foreground = false, background = false;

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //foreground permission allowed
                    if (grantResults[i] >= 0) {
                        foreground = true;
                        Toast.makeText(getApplicationContext(), "Foreground location permission allowed", Toast.LENGTH_SHORT).show();
                        continue;
                    } else {
                        Toast.makeText(getApplicationContext(), "Location Permission denied", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                if (permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    if (grantResults[i] >= 0) {
                        foreground = true;
                        background = true;
                        Toast.makeText(getApplicationContext(), "Background location location permission allowed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Background location location permission denied", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            if (foreground) {
                if (background) {
                    handleLocationUpdates();
                } else {
                    handleForegroundLocationUpdates();
                }
            }
        }
    }

    private void handleLocationUpdates() {
        //foreground and background
        Toast.makeText(getApplicationContext(),"Start Foreground and Background Location Updates",Toast.LENGTH_SHORT).show();
        locationClient.startLocationUpdates(locationClient.createLocationRequest(), mLocationCallback);
    }

    private void handleForegroundLocationUpdates() {
        //handleForeground Location Updates
        Toast.makeText(getApplicationContext(),"Start foreground location updates",Toast.LENGTH_SHORT).show();
        locationClient.startLocationUpdates(locationClient.createLocationRequest(), mLocationCallback);
    }

    private void setUpGeoFenceMap() {
        SupportMapFragment mapGeofence = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapGeofence);


        mapGeofence.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                if (mUserPreference.getGeoFenceLatitude() > 0 && mUserPreference.getGeoFenceLongitude() > 0) {

                    LatLng positionGeoFence = new LatLng(mUserPreference.getGeoFenceLatitude(), mUserPreference.getGeoFenceLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionGeoFence, 16.0f));
                    CircleOptions circleOptions = drawMarkerCircle(positionGeoFence, googleMap, 400);
                    try {
                        if (circleOptions != null)
                            googleMap.addCircle(circleOptions);
                    } catch (Exception e) {

                    }

                }

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng position = new LatLng(mUserPreference.getCurrentLatitude(), mUserPreference.getCurrentLongitude());


        mMarker = map.addMarker(new MarkerOptions().position(position));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));


    }

    /**
     * Function to add circle on map around the marker
     *
     * @param latLng            latitude longitude bond
     * @param map               Google map
     * @param mapLocationRadius Radius in feet.
     * @return circle option.
     */
    public CircleOptions drawMarkerCircle(LatLng latLng, GoogleMap map, int mapLocationRadius) {
        CircleOptions circleOptions = null;
        if (mapLocationRadius != 0 && map != null) {
            circleOptions = new CircleOptions();
            double meters = mapLocationRadius / 3.2808;
            circleOptions
                    .center(latLng)
                    .strokeColor(Color.rgb(15, 117, 188))
                    .fillColor(Color.argb(100, 135, 206, 255))
                    .strokeWidth(2.0f)
                    .radius(meters + .0f);
        }
        return circleOptions;
    }

}
