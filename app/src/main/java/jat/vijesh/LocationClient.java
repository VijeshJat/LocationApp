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
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocationClient {


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

    protected LocationRequest createLocationRequest() {

        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(30000);
            //   mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        return mLocationRequest;
    }


    private GeofencingRequest getGeofencingRequest(String geofenceId, double latitude, double longitude, float radius) {

        List<Geofence> geofenceList = new ArrayList<>();

        geofenceList.add(new Geofence.Builder()
                .setRequestId(geofenceId)
                .setCircularRegion(latitude, longitude, radius)  //22.699892, 75.866035
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());


        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);

        return builder.build();
    }


    private PendingIntent geoFencePendingIntent;

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.

        if (geoFencePendingIntent != null) {
            return geoFencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geoFencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geoFencePendingIntent;
    }

    public void addGeoFenceCircle(final double latitude, final double longitude, float radius) {

        if (checkLocationPermission()) {

            final String geofenceId = UUID.randomUUID().toString();

            geofencingClient.addGeofences(getGeofencingRequest(geofenceId,latitude, longitude, radius), getGeofencePendingIntent())
                    .addOnSuccessListener(((Activity) context), new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            Utility.writeLogFileToDevice(context, "Geo Fence Added Successfully");
                            Log.d("TESTING", " Geo Fence Added Successfully ");

                            mUserPreference.setGeoFenceLatitude(latitude);
                            mUserPreference.setGeoFenceLongitude(longitude);
                            mUserPreference.savePreference(context);


                            if (!mUserPreference.getGeoFenceId().isEmpty()) {
                                //remove

                                List<String> geofenceIds = new ArrayList<>();
                                geofenceIds.add(UserPreference.getInstance(context).getGeoFenceId());

                                removeGeoFenceCircle(geofenceIds, mUserPreference.getGeoFenceId());

                            }else {


                                mUserPreference.setGeoFenceId(geofenceId);
                                mUserPreference.savePreference(context);
                            }

                        }
                    })
                    .addOnFailureListener(((Activity) context), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utility.writeLogFileToDevice(context, "Geo Fence Add onFailure ");
                            Log.d("TESTING", " Geo Fence addOnFailureListener  ");

                            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                                Log.e("Provider", "Provider is not avaible");
                            }
                            if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                                Log.e("Network Provider", "Provider is not avaible");
                            }

                            if (!mUserPreference.getGeoFenceId().isEmpty()) {

                                List<String> geofenceIds = new ArrayList<>();
                                geofenceIds.add(UserPreference.getInstance(context).getGeoFenceId());
                                removeGeoFenceCircle(geofenceIds, mUserPreference.getGeoFenceId());

                            }

                            mUserPreference.setGeoFenceId("");
                            mUserPreference.savePreference(context);
                        }
                    });
        }

    }


    public void removeGeoFenceCircle(List<String> geoFenceIds, final String geofenceId) {

        if (checkLocationPermission()) {

            geofencingClient.removeGeofences(geoFenceIds)
                    .addOnSuccessListener(((Activity) context), new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mUserPreference.setGeoFenceId(geofenceId);
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
