package jat.vijesh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = "Geofence error with code " + geofencingEvent.getErrorCode();
            Log.e("GEO_FENCE", " errorMessage " + errorMessage);
            Log.d("TESTING", " GEO_FENCE errorMessage "+ errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            Utility.writeLogFileToDevice(context, " User exit from his geo location circle ");
            Log.d("TESTING", " User exit from his geo location circle ");

            UserPreference mUserPreference = UserPreference.getInstance(context);
            LocationClient locationClient = LocationClient.getInstance(context);

            locationClient.addGeoFenceCircle(mUserPreference.getCurrentLatitude(), mUserPreference.getCurrentLongitude(), 400);
        }

    }

}
