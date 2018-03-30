package com.project.lyt.cyclenewwest.Model;

/**
 * Created by sophie on 2017-11-25.
 */

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class BikeWayLine {
    private ArrayList<LatLng> points;

    public BikeWayLine() {
        points = new ArrayList<LatLng>();
    }

    public BikeWayLine(LatLng point1, LatLng point2) {
        points = new ArrayList<LatLng>();
        points.add(point1);
        points.add(point2);
    }

    public void addPoint(LatLng point) {
        points.add(point);
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }

}