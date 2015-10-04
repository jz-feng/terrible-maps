package com.jerryfeng.terriblemaps;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class Step {

    private LatLng mStartLocation;
    private LatLng mEndLocation;
    private int mDistance;
    private String mPolyline;

    public Step(JSONObject step) {
        try {
            JSONObject start = step.getJSONObject("start_location");
            mStartLocation = new LatLng(start.getDouble("lat"), start.getDouble("lng"));

            JSONObject end = step.getJSONObject("end_location");
            mStartLocation = new LatLng(end.getDouble("lat"), end.getDouble("lng"));

            JSONObject distance = step.getJSONObject("distance");
            mDistance = distance.getInt("value");

            JSONObject polyline = step.getJSONObject("polyline");
            mPolyline = polyline.getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LatLng getStartLocation() {
        return mStartLocation;
    }

    public void setStartLocation(LatLng mStartLocation) {
        this.mStartLocation = mStartLocation;
    }

    public LatLng getEndLocation() {
        return mEndLocation;
    }

    public void setEndLocation(LatLng mEndLocation) {
        this.mEndLocation = mEndLocation;
    }

    public int getDistance() {
        return mDistance;
    }

    public void setDistance(int mDistance) {
        this.mDistance = mDistance;
    }
}
