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

        NotificationHelper notificationHelper = new NotificationHelper(context);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = "Geofence error with code " + geofencingEvent.getErrorCode();
            Log.e("GEO_FENCE", " errorMessage " + errorMessage);
            Log.d("TESTING", " GEO_FENCE errorMessage " + errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();


        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Utility.writeLogFileToDevice(context, " User enter to  his geo location circle ");
                Log.d("TESTING", " User enter to his geo location circle ");
                notificationHelper.sendHighPriorityNotification("User enter to his geo location circle", "", MapActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                notificationHelper.sendHighPriorityNotification("User Dwell in  geo  circle", "", MapActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Utility.writeLogFileToDevice(context, " User exit from his geo location circle ");
                Log.d("TESTING", " User exit from his geo location circle ");
                notificationHelper.sendHighPriorityNotification("User exit from his geo location circle", "", MapActivity.class);
                break;
        }

    }

}
