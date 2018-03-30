package com.project.lyt.cyclenewwest.Model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Represents a greenway, a longer bikeway through park territory.
 */

public class GreenWay extends BikeWay {

    /**
     * Creates a trimmed GreenWay by removing points that
     * coincide with the given BikeWay.
     * @param removebw
     *      The BikeWay representing points to be removed.
     * @return
     *      Trimmed GreenWay.
     */
    public GreenWay removeBikeWay(BikeWay removebw) {
        GreenWay newgw = new GreenWay();

        for (BikeWayLine gwl : this.getAllLines()) {
            BikeWayLine newline = new BikeWayLine();
            for (LatLng latlng : gwl.getPoints()) {
                if (!removebw.includesPoint(latlng)) {
                    newline.addPoint(latlng);
                }
            }
            newgw.addLine(newline);
        }

        return newgw;
    }

}
