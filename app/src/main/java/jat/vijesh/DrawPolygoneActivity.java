package jat.vijesh;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DrawPolygoneActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap map;
    private Marker mMarker;
    private UserPreference mUserPreference;
    private EditText edtInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_polygon);
        mUserPreference = UserPreference.getInstance(this);
        //mapGeofence
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        edtInput = findViewById(R.id.edtInput);


        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtInput.setText("");

            }
        });

        findViewById(R.id.btnDraw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtInput.getText().toString().isEmpty()) {
                    Toast.makeText(DrawPolygoneActivity.this, " Please paste your json", Toast.LENGTH_LONG).show();
                } else {
                    drawPolygon(edtInput.getText().toString());

                }
            }
        });

    }


    private List<LatLng> getLatLngList(String json) {
        List<LatLng> list = new ArrayList<>();
        String ssss = " [\n" +
                "        {\n" +
                "          \"lat\": 22.57191783492612,\n" +
                "          \"lng\": 77.03784307915181\n" +
                "        },\n" +
                "        {\n" +
                "          \"lat\": 22.567519060876066,\n" +
                "          \"lng\": 77.04288563209981\n" +
                "        },\n" +
                "        {\n" +
                "          \"lat\": 22.56349663562882,\n" +
                "          \"lng\": 77.0415337987563\n" +
                "        },\n" +
                "        {\n" +
                "          \"lat\": 22.563476820435667,\n" +
                "          \"lng\": 77.03365883308858\n" +
                "        },\n" +
                "        {\n" +
                "          \"lat\": 22.56771720687017,\n" +
                "          \"lng\": 77.03229627090901\n" +
                "        }\n" +
                "      ]";

        try {
            JSONArray jj = new JSONArray(json);

            for (int i = 0; i < jj.length(); i++) {
                JSONObject jo = jj.getJSONObject(i);
                double lat = jo.optDouble("lat");
                double lng = jo.optDouble("lng");

                list.add(new LatLng(lat, lng));
            }
        } catch (JSONException e) {

            return list;
        }

        return list;
    }


    private void drawPolygon(String json) {

        PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE);
        List<LatLng> list = getLatLngList(json);

        if (list.size() == 0)
            return;

        LatLng firstPoint = null;
        for (int z = 0; z < list.size(); z++) {
            LatLng point = list.get(z);
            options.add(point);
            if (z == 0) {
                firstPoint = point;

            }
        }
        mMarker = map.addMarker(new MarkerOptions().position(firstPoint));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPoint, 16.0f));
        options.add(firstPoint);
        Polyline line = map.addPolyline(options);

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
