package jat.vijesh;


import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocationClient {


    private GeofenceHelper geofenceHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private GeofencingClient geofencingClient;

    private Context context;
    private LocationRequest mLocationRequest;
    private UserPreference mUserPreference;
    private static LocationClient mLocationClient;

    public static LocationClient getInstance(Context context) {

        if (mLocationClient == null)
            mLocationClient = new LocationClient(context);

        return mLocationClient;

    }

    private LocationClient(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        geofencingClient = LocationServices.getGeofencingClient(context);
        mUserPreference = UserPreference.getInstance(context);

        geofenceHelper = new GeofenceHelper(context);

    }


    private boolean isBelowMarshmallow() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    public boolean checkLocationPermission() {

        boolean hasPermission = false;

        if (isBelowMarshmallow()) {
            return true;
        }

        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
        } else {
            hasPermission = false;
        }

        return hasPermission;
    }


    public void getUserLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(((Activity) context), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        Log.d("TESTING", " onSuccess ");
                        if (location != null) {
                            // Logic to handle location object

                            Log.d("TESTING", " LastLocation()  getLatitude -- " + location.getLatitude() + "  ,, getLongitude -- " + location.getLongitude());
                        }
                    }
                })
                .addOnFailureListener(((Activity) context), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TESTING", " onFailure  e -- " + e);
                    }
                })

                .addOnCanceledListener(((Activity) context), new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d("TESTING", " onCanceled ");
                    }
                })
                .addOnCompleteListener(((Activity) context), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Log.d("TESTING", " onComplete  e -- " + task.isSuccessful());
                    }
                })
        ;


    }


    public void stopLocationUpdates(LocationCallback locationCallback) {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    public void startLocationUpdates(LocationRequest mLocationRequest, LocationCallback mLocationCallback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }


    public void displayLocationSettingsRequest() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        builder.addLocationRequest(createLocationRequest());
        builder.setAlwaysShow(true);


        task.addOnSuccessListener(((Activity) context), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d("TESTING", " displayLocationSettingsRequest onSuccess ");
            }
        });

        task.addOnFailureListener(((Activity) context), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("TESTING", " displayLocationSettingsRequest onFailure ");

                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(((MainActivity) context), 101);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


    }

    protected LocationRequest updateLocationRequest(long interval , long fastedInterval) {

        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(interval);
            mLocationRequest.setFastestInterval(fastedInterval);
          //  mLocationRequest.setSmallestDisplacement(15);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }else {
            mLocationRequest.setInterval(interval);
            mLocationRequest.setFastestInterval(fastedInterval);
           // mLocationRequest.setSmallestDisplacement(15);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        return mLocationRequest;
    }

    protected LocationRequest createLocationRequest() {

        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(4000);
            mLocationRequest.setFastestInterval(4000);
          //  mLocationRequest.setSmallestDisplacement(15);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        return mLocationRequest;
    }

    public void addGeoFenceCircle(final double latitude, final double longitude, float radius) {

        if (checkLocationPermission()) {

            final String geofenceId = UUID.randomUUID().toString();


            Geofence geofence = geofenceHelper.getGeofence(geofenceId, new LatLng(latitude, longitude), radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
            GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
            PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Utility.writeLogFileToDevice(context, "Geo Fence Added Successfully");
                            Log.d("TESTING", " Geo Fence Added Successfully ");

                            mUserPreference.setGeoFenceLatitude(latitude);
                            mUserPreference.setGeoFenceLongitude(longitude);
                            mUserPreference.setGeoFenceId(geofenceId);
                            mUserPreference.savePreference(context);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String errorMessage = geofenceHelper.getErrorString(e);
                            Log.d("TAG", "onFailure: " + errorMessage);
                            mUserPreference.setGeoFenceId("");
                            mUserPreference.savePreference(context);
                        }
                    });

        }

    }


    public void removeGeoFenceCircle(String geofenceId) {

        if (checkLocationPermission()) {

            List<String> geofenceIds = new ArrayList<>();
            geofenceIds.add(geofenceId);
            geofencingClient.removeGeofences(geofenceIds)
                    .addOnSuccessListener(((Activity) context), new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mUserPreference.setGeoFenceId("");
                            mUserPreference.savePreference(context);

                            Utility.writeLogFileToDevice(context, " Geo Fence Remove onSuccess ");
                            Log.d("TESTING", " Geo Fence Remove onSuccess ");

                        }
                    })
                    .addOnFailureListener(((Activity) context), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utility.writeLogFileToDevice(context, " Geo Fence Remove onFailure ");
                            Log.d("TESTING", " Geo Fence Remove onFailure ");
                        }
                    });


        }

    }


}
