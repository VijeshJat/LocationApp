package jat.vijesh;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private UserPreference mUserPreference;
    private LocationClient locationClient;
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locations = locationResult.getLocations();

            for (Location location : locations) {

                mUserPreference.setCurrentLatitude(location.getLatitude());
                mUserPreference.setCurrentLongitude(location.getLongitude());
                mUserPreference.savePreference(MainActivity.this);

                Log.d("TESTING", " onLocationResult()  getLatitude -- " + location.getLatitude() + "  ,, getLongitude -- " + location.getLongitude());
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserPreference = UserPreference.getInstance(this);
        locationClient = LocationClient.getInstance(this);

        findViewById(R.id.btnCheckLocationService).setOnClickListener(this);
        findViewById(R.id.btnLastLocation).setOnClickListener(this);
        findViewById(R.id.btnRemoveLocationUpdate).setOnClickListener(this);
        findViewById(R.id.btnRequestLocationUpdate).setOnClickListener(this);
        findViewById(R.id.btnAddGeoFence).setOnClickListener(this);
        findViewById(R.id.btnMapScreen).setOnClickListener(this);
        findViewById(R.id.btnRemoveGeofence).setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {

            case R.id.btnCheckLocationService:
                locationClient.displayLocationSettingsRequest();


                break;
            case R.id.btnLastLocation:

                locationClient.getUserLastKnownLocation();

                break;

            case R.id.btnRemoveLocationUpdate:

                locationClient.stopLocationUpdates(mLocationCallback);

                break;

            case R.id.btnRequestLocationUpdate:

                locationClient.startLocationUpdates(locationClient.createLocationRequest(), mLocationCallback);

                break;

            case R.id.btnRemoveGeofence:

                locationClient.removeGeoFenceCircle(mUserPreference.getGeoFenceId());

                break;


            case R.id.btnAddGeoFence:


                if (mUserPreference.getGeoFenceId().isEmpty()) {

                   /* if (Build.VERSION.SDK_INT >= 29) {
                        //We need background permission
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationClient.addGeoFenceCircle(mUserPreference.getCurrentLatitude(), mUserPreference.getCurrentLongitude(), 400);

                        } else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                                //We show a dialog and ask for permission
                                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                            } else {
                                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                            }
                        }

                    } else {*/
                        locationClient.addGeoFenceCircle(mUserPreference.getCurrentLatitude(), mUserPreference.getCurrentLongitude(), 400);

                  //  }
                }

                break;

            case R.id.btnMapScreen:

                startActivity(new Intent(this, MapActivity.class));

                break;


        }


    }


}
