package com.project.lyt.cyclenewwest.Model;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

/**
 * Represents a bikeway or greenway created from City of New West's
 * JSON open data.
 */

public class BikeWay {
    /**
     * The type of the bikeway.
     */
    public enum RoadType {
        ONSTREET,
        OFFSTREET,
        BIKELANE
    }

    /**
     * Direction used for trimming the bikeway.
     */
    public enum Direction  {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    /**
     * ObjectId in JSON object.
     */
    private int objectId;

    /**
     * Type of the bikeway.
     */
    private RoadType roadType;
    /**
     * Whether the bikeway is paved.
     */
    private boolean paved;
    /**
     * Three-letter name of the bikeway.
     */
    private String name;
    /**
     * Full name of the bikeway.
     */
    private String fullName;
    /**
     * Length of the Bikeway.
     */
    private double length;
    /**
     * All of the lines in the bikeway.
     */
    private ArrayList<BikeWayLine> bikeWayLines;

    /**
     * Maximum difference between two points before they are deemed to be equal.
     */
    private static final double EQUAL_DIFFERENCE = 0.00000000001;

    public BikeWay() {
        bikeWayLines = new ArrayList<BikeWayLine>();
    }

    /**
     * Sets the objectId.
     * @param value
     *      objectId
     */
    public void setObjectId(int value) {
        objectId = value;
    }

    /**
     * Sets the RoadType.
     * @param value
     *      roadType
     */
    public void setRoadType(RoadType value) {
        roadType = value;
    }

    /**
     * Sets whether the bikeway is paved.
     * @param value
     *       paved
     */
    public void setPaved(boolean value) {
        paved = value;
    }

    /**
     * Sets the BikeWay name.
     * @param value
     *      name
     */
    public void setName(String value) {
        name = value;
    }

    /**
     * Sets the BikeWay length.
     * @param value
     *      length
     */
    public void setLength(double value) {
        length = value;
    }

    /**
     * Sets the full name.
     * @param value
     *      fullName
     */
    public void setFullName(String value) {
        fullName = value;
    }

    /**
     * Gets the objecttId
     * @return
     *      objectId
     */
    public int getObjectId() {
        return objectId;
    }

    /**
     * Gets the name.
     * @return
     *      name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the full name.
     * @return
     *      fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Adds a line to the BikeWay.
     * @param bikeWayLine
     *      The line to add.
     */
    public void addLine(BikeWayLine bikeWayLine) {
        bikeWayLines.add(bikeWayLine);
    }

    /**
     * Gets all lines in the BikeWay.
     * @return
     *      ArrayList of BikeWayLine objects.
     */
    public ArrayList<BikeWayLine> getAllLines() {
        return bikeWayLines;
    }


    /**
     * Gets the length of the bikeway in KM.
     * @return
     *      length in KM
     */
    public double getKM() {
        return Math.round(length / 100) / 10;
    }

    /**
     * Checks if a BikeWay includes a point equivalent in Lat/Long
     * coordinates to a given other point.
     * @param point
     *      The point to check.
     * @return
     *      True if the BikeWay includes that point.
     */
    public boolean includesPoint(LatLng point) {
        for (BikeWayLine bwl : bikeWayLines) {
            for (LatLng latlng : bwl.getPoints()) {
                if (Math.abs(latlng.latitude - point.latitude) < EQUAL_DIFFERENCE &&
                        Math.abs(latlng.longitude - point.longitude) < EQUAL_DIFFERENCE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates a trimmed BikeWay ending at a Lat or Long boundary
     * in a given direction.
     * @param direction
     *      The direction to trim.
     * @param boundary
     *      The Latitude or Longitude value at which to trim the
     *      BikeWay.
     * @return
     *      Trimmed BikeWay6 object.
     */
    public BikeWay trim(Direction direction, double boundary) {
        BikeWay newbw = new BikeWay();

        for (BikeWayLine bwl : this.getAllLines()) {
            BikeWayLine newbwl = new BikeWayLine();
            for (LatLng latlng : bwl.getPoints()) {
                boolean omit = false;
                switch (direction) {
                    case NORTH :
                        omit = latlng.latitude > boundary;
                        break;
                    case SOUTH:
                        omit = latlng.latitude < boundary;
                        break;
                    case EAST:
                        omit = latlng.longitude > boundary;
                        break;
                    case WEST:
                        omit = latlng.longitude < boundary;
                        break;
                }
                if (!omit) {
                    newbwl.addPoint(latlng);
                }
            }
            newbw.addLine(newbwl);
        }

        return newbw;
    }

}
