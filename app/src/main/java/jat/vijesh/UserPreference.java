package jat.vijesh;

import android.content.Context;

import com.google.gson.Gson;


public class UserPreference {

    private static UserPreference mUserPreference;

    private String geoFenceId = "";
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private double geoFenceLatitude = 0.0;
    private double geoFenceLongitude = 0.0;

    public static UserPreference getInstance(Context context) {

        if (mUserPreference == null) {

            mUserPreference = new Gson().fromJson(context.getSharedPreferences(UserPreference.class.getName(), Context.MODE_PRIVATE).getString("user_preference", null), UserPreference.class);

            if (mUserPreference == null)
                mUserPreference = new UserPreference();
        }


        return mUserPreference;

    }


    public void savePreference(Context context) {
        context.getSharedPreferences(UserPreference.class.getName(), Context.MODE_PRIVATE).edit().putString("user_preference", new Gson().toJson(mUserPreference)).apply();
    }

    public void clearPreference(Context context) {
        context.getSharedPreferences(UserPreference.class.getName(), Context.MODE_PRIVATE).edit().clear().apply();
        mUserPreference = null;
    }


    public double getGeoFenceLatitude() {
        return geoFenceLatitude;
    }

    public void setGeoFenceLatitude(double geoFenceLatitude) {
        this.geoFenceLatitude = geoFenceLatitude;
    }

    public double getGeoFenceLongitude() {
        return geoFenceLongitude;
    }

    public void setGeoFenceLongitude(double geoFenceLongitude) {
        this.geoFenceLongitude = geoFenceLongitude;
    }

    public String getGeoFenceId() {
        return geoFenceId;
    }

    public void setGeoFenceId(String geoFenceId) {
        this.geoFenceId = geoFenceId;
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }
}
