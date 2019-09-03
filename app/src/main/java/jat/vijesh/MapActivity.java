package jat.vijesh;


import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private UserPreference mUserPreference;
    private LocationClient locationClient;
    private GoogleMap map;

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locations = locationResult.getLocations();

            for (Location location : locations) {

                mUserPreference.setCurrentLatitude(location.getLatitude());
                mUserPreference.setCurrentLongitude(location.getLongitude());
                mUserPreference.savePreference(MapActivity.this);


                if (map != null) {
                    LatLng position = new LatLng(mUserPreference.getCurrentLatitude(), mUserPreference.getCurrentLongitude());
                    mMarker.setPosition(position);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));


                }
                Log.d("TESTING", " onLocationResult()  getLatitude -- " + location.getLatitude() + "  ,, getLongitude -- " + location.getLongitude());
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationClient = LocationClient.getInstance(this);
        mUserPreference = UserPreference.getInstance(this);
        locationClient.startLocationUpdates(locationClient.createLocationRequest(), mLocationCallback);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    Marker mMarker;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng position = new LatLng(mUserPreference.getCurrentLatitude(), mUserPreference.getCurrentLongitude());


        mMarker = map.addMarker(new MarkerOptions().position(position));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));


        if (mUserPreference.getGeoFenceLatitude() > 0 && mUserPreference.getGeoFenceLongitude() > 0) {

            LatLng positionGeoFence = new LatLng(mUserPreference.getGeoFenceLatitude(), mUserPreference.getGeoFenceLongitude());

            CircleOptions circleOptions = drawMarkerCircle(positionGeoFence, map, 400);
            try {
                if (circleOptions != null)
                    map.addCircle(circleOptions);
            } catch (Exception e) {

            }

        }

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
