package com.jerryfeng.terriblemaps;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private EditText mAddressField;
    private Button mSearchButton, mDoneButton;

    private LatLng mCurrentLocation;
    private LatLng mSelectedLocation;
    private String mSearchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        mCurrentLocation = new LatLng(intent.getDoubleExtra("latitude", 0f), intent.getDoubleExtra("longitude", 0f));

        setUpMapIfNeeded();

        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        final Context mContext = this;

        mAddressField = (EditText) findViewById(R.id.address_field);
        mSearchButton = (Button) findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchString = mAddressField.getText().toString();
                try {
                    List<Address> addressList = geocoder.getFromLocationName(mSearchString, 1);

                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);
                        LatLng coords = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 13));
                        mMap.clear();
                        Marker selectedMarker = mMap.addMarker(
                                new MarkerOptions().position(coords).draggable(true));
                        mSelectedLocation = selectedMarker.getPosition();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mDoneButton = (Button) findViewById(R.id.done_button);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedLocation == null) {
                    Toast.makeText(mContext, "Select a location first ya dingus", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent();
                    intent.putExtra("latitude", mSelectedLocation.latitude);
                    intent.putExtra("longitude", mSelectedLocation.longitude);
                    intent.putExtra("search_string", mSearchString);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.setMyLocationEnabled(true);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(mCurrentLocation).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 13));
    }
}
