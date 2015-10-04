package com.jerryfeng.terriblemaps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.jerryfeng.terriblemaps.component.Compass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements SensorEventListener {

    public static final String serverKey = "AIzaSyAyD0skqAcwz-z1BzwJz8S_6kAFHkBOI40";
    private static final int REQUEST_MAP_ACTIVITY = 10;

    private JSONObject mResponseObject;

    private LocationManager mLocationManager;
    private MockLocationProvider mMockLocationManager;

    private Location mLocation;
    private LatLng mDestinationCoords;

    private SensorManager mSensorManager;

    private TextView mDebugHeading;
    private TextView mDebugLat;
    private TextView mDebugLon;
    private Compass mCompass;
    private TextView mSearchAddress;
    private Button mMapsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayout();
        establishLocationManager();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=Waterloo&destination=Toronto&key="
                + serverKey;
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);

                        try {
                            mResponseObject = new JSONObject(response);
                            JSONArray mRoutesArray = mResponseObject.getJSONArray("routes");

                            if (mRoutesArray != null && mRoutesArray.length() > 0) {
                                JSONObject mRoute = mRoutesArray.getJSONObject(0);
                                JSONArray mLegsArray = mRoute.getJSONArray("legs");

                                if (mLegsArray != null && mLegsArray.length() > 0) {
                                    JSONObject mLeg = mLegsArray.getJSONObject(0);
                                    JSONArray mStepsArray = mLeg.getJSONArray("steps");

                                    if (mStepsArray != null && mStepsArray.length() > 0) {
                                        for (int i = 0; i < mStepsArray.length(); i++) {
                                            JSONObject mStep = mStepsArray.getJSONObject(i);

                                            Log.d("step", mStep.toString());
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error response", error.getMessage());
            }
        });

        queue.add(mStringRequest);
    }

    private void initLayout() {
        mDebugHeading = (TextView) findViewById(R.id.debug_heading);
        mDebugLat = (TextView) findViewById(R.id.debug_lat);
        mDebugLon = (TextView) findViewById(R.id.debug_long);
        mCompass = (Compass) findViewById(R.id.compass);
        mSearchAddress = (TextView) findViewById(R.id.search_address);
        mMapsButton = (Button) findViewById(R.id.maps_button);

        mMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("latitude", mLocation.getLatitude());
                intent.putExtra("longitude", mLocation.getLongitude());
                startActivityForResult(intent, REQUEST_MAP_ACTIVITY);
            }
        });
    }

    private void establishLocationManager() {
        mMockLocationManager = new MockLocationProvider(LocationManager.GPS_PROVIDER, this);
        mMockLocationManager.pushLocation(-12.34, 23.45);

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
                mDebugLat.setText(String.valueOf(location.getLatitude()));
                mDebugLon.setText(String.valueOf(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.OUT_OF_SERVICE:
                        break;
                    case LocationProvider.AVAILABLE:
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        break;
                }
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MAP_ACTIVITY && resultCode == RESULT_OK) {
            mSearchAddress.setText(data.getStringExtra("search_string"));
            mDestinationCoords = new LatLng(data.getDoubleExtra("latitude", 0f),
                    data.getDoubleExtra("longitude", 0f));
            Log.d("lat-lng", mDestinationCoords.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        mDebugHeading.setText("Heading: " + Float.toString(degree) + " degrees");

        mCompass.setHeading(degree);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

}
