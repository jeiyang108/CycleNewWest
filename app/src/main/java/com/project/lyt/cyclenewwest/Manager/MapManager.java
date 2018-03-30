package com.project.lyt.cyclenewwest.Manager;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;


/**
 * Class to manage pointers and lines on the map.
 */
public class MapManager {
    /**
     * List of lines currently on the map.
     */
    private static ArrayList<Polyline> mapLines = new ArrayList<Polyline>();
    /**
     * List of markers currently on the map.
     */
    private static ArrayList<Marker> markers = new ArrayList<Marker>();

    /**
     * Add a polyline to the list.
     * @param line
     *      The line to add.
     */
    public static void addPolyline(Polyline line) {
        mapLines.add(line);
    }

    /**
     * Add a marker to the list.
     * @param marker
     *      The marker to add.
     */
    public static void addMarker(Marker marker) {
        markers.add(marker);
    }

    /**
     * Gets all polylines on the map.
     * @return
     *      The list of polylines.
     */
    public static ArrayList<Polyline> getAllLines() {
        return mapLines;
    }

    /**
     * Gets all markers currently on the map.
     * @return
     *      The list of markers.
     */
    public static ArrayList<Marker> getAllMarkers() {
        return markers;
    }

    /**
     * Clears the list of markers.
     */
    public static void clearMarkers() {
        markers.clear();
    }

    /**
     * Clears the list of map lines.
     */
    public static void clearMapLines() {
        mapLines.clear();
    }
}
