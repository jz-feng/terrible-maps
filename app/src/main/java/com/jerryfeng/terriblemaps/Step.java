package com.jerryfeng.terriblemaps;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Step {

    private LatLng mStartLocation;
    private LatLng mEndLocation;
    private int mDistance;
    private String mPolyline;
    private ArrayList<LatLng> mWaypoints;

    public Step(JSONObject step) {
        try {
            JSONObject start = step.getJSONObject("start_location");
            mStartLocation = new LatLng(start.getDouble("lat"), start.getDouble("lng"));

            JSONObject end = step.getJSONObject("end_location");
            mEndLocation = new LatLng(end.getDouble("lat"), end.getDouble("lng"));

            JSONObject distance = step.getJSONObject("distance");
            mDistance = distance.getInt("value");

            JSONObject polyline = step.getJSONObject("polyline");
            mPolyline = polyline.getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Courtesy of stack overflow @
    //http://stackoverflow.com/questions/15924834/decoding-polyline-with-new-google-maps-api
    private ArrayList<LatLng> decodePoly(String encoded) {

        Log.i("Location", "String received: "+encoded);
        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),(((double) lng / 1E5)));
            poly.add(p);
        }

        for(int i=0;i<poly.size();i++){
            Log.i("Location", "Point sent: Latitude: " + poly.get(i).latitude + " Longitude: " + poly.get(i).longitude);
        }
        return poly;
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

    public ArrayList<LatLng> getWaypoints() {
        mWaypoints = decodePoly(mPolyline);
        return mWaypoints;
    }
}
