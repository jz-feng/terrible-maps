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
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import java.util.ArrayList;


public class MainActivity extends Activity implements SensorEventListener {

    private static final int REQUEST_MAP_ACTIVITY = 10;

    private LocationManager mLocationManager;
    private MockLocationProvider mMockLocationManager;

    private Location mLocation;
    private LatLng mDestinationCoords;
    private ArrayList<Step> mSteps;

    private SensorManager mSensorManager;

    private TextView mDebugHeading;
    private TextView mDebugLat;
    private TextView mDebugLon;

    private Compass mCompass;

    private TextView mSearchAddress;
    private Button mMapsButton;

    private RelativeLayout root;
    private int backgroundFlashIndex = 0;
    private boolean isBackgroundFlashing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSteps = new ArrayList<>();

        initLayout();
        establishLocationManager();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    private void initLayout() {
        mDebugHeading = (TextView) findViewById(R.id.debug_heading);
        mDebugLat = (TextView) findViewById(R.id.debug_lat);
        mDebugLon = (TextView) findViewById(R.id.debug_long);
        mCompass = (Compass) findViewById(R.id.compass);
        mSearchAddress = (TextView) findViewById(R.id.search_address);
        mMapsButton = (Button) findViewById(R.id.maps_button);
        root = (RelativeLayout) findViewById(R.id.root_background);

        mMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLocation != null) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    intent.putExtra("latitude", mLocation.getLatitude());
                    intent.putExtra("longitude", mLocation.getLongitude());
                    startActivityForResult(intent, REQUEST_MAP_ACTIVITY);
                }
            }
        });
    }

    private void establishLocationManager() {
        mMockLocationManager = new MockLocationProvider(LocationManager.GPS_PROVIDER, this);

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

        mMockLocationManager.pushLocation(44.0, -80.0);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MAP_ACTIVITY && resultCode == RESULT_OK) {
            mSearchAddress.setText(data.getStringExtra("search_string"));
            mDestinationCoords = new LatLng(data.getDoubleExtra("latitude", 0f),
                    data.getDoubleExtra("longitude", 0f));

            Log.d("destination lat-lng", mDestinationCoords.toString());

            mSteps.clear();

            try {
                JSONArray stepsArray = new JSONArray(data.getStringExtra("steps"));

                if (stepsArray.length() > 0) {
                    for (int i = 0; i < stepsArray.length(); i++) {
                        JSONObject mStep = stepsArray.getJSONObject(i);

                        mSteps.add(new Step(mStep));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("steps", mSteps.size() + "");
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

    private void toggleBackgroundFlash(boolean flash) {
        if (flash) {
            if (!isBackgroundFlashing) {
                isBackgroundFlashing = true;
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (backgroundFlashIndex == 0) {
                            root.setBackgroundResource(R.drawable.background_buzz_1);
                            backgroundFlashIndex++;
                        } else {
                            root.setBackgroundResource(R.drawable.background_buzz_3);
                            backgroundFlashIndex = 0;
                        }

                        if (isBackgroundFlashing) {
                            handler.postDelayed(this, 500);
                        } else {
                            root.setBackgroundResource(R.drawable.background);
                        }
                    }
                });
            }
        } else {
            isBackgroundFlashing = false;
        }
    }

}
