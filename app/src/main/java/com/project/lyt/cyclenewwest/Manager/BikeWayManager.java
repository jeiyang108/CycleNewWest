package com.project.lyt.cyclenewwest.Manager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import com.google.android.gms.maps.model.LatLng;
import com.project.lyt.cyclenewwest.Model.BikeWay;
import com.project.lyt.cyclenewwest.Model.BikeWayLine;
import com.project.lyt.cyclenewwest.Model.GreenWay;

/**
 * Class for managing BikeWay objects.
 * Created by sophie on 2017-11-13.
 */
public class BikeWayManager {
    /**
     * List of all BikeWay objects.
     */
    private static ArrayList<BikeWay> bikeWays;


    /**
     * Gets a BikeWay based on its ObjectId.
     * @param id
     *      The ObjectId of a BikeWay
     * @return
     *      The BikeWay with that ObjectId,
     *      or null if not found.
     */
    public static BikeWay getBikeWayById(int id) {
        for (BikeWay bw : bikeWays) {
            if (!(bw instanceof GreenWay) && bw.getObjectId() == id) {
                return  bw;
            }
        }
        return null;
    }

    /**
     * Gets a greenway based on its name;
     * @param bikewayName
     *      The name of the greenway.
     * @return
     *      The BikeWay object of the greenway, or null if not found.
     */
    public static GreenWay getGreenWayByName(String bikewayName) {
        for (BikeWay bw : bikeWays) {

            if (bw instanceof GreenWay) {
                if (((GreenWay) bw).getFullName().equals(bikewayName)) {
                    return (GreenWay) bw;
                }
            }
        }
        return null;
    }

    /**
     * Initializes the list of BikeWays and GreenWays from JSON data.
     * @param jsonBWString
     *          JSON string of BikeWays
     * @param jsonGWString
     *          JSON string of GreenWays
     */
    public static void initializeBikeWays(String jsonBWString, String jsonGWString) {
        bikeWays = new ArrayList<BikeWay>();

        if (!jsonBWString.isEmpty()) {
            try {
                JSONArray jsonBWArray = new JSONArray(jsonBWString);
                for (int x = 0; x < jsonBWArray.length(); x++) {
                    JSONObject jsonBW = jsonBWArray.getJSONObject(x);

                    int objectId = jsonBW.getInt("OBJECTID");
                    String status = jsonBW.getString("Status");
                    String paved = jsonBW.getString("Paved");
                    String bikeOnStreet = jsonBW.getString("Bike_OnStreet");
                    String bikeOffStreet = jsonBW.getString("Bike_OffStreet");
                    String bikeLane = jsonBW.getString("Bike_Lane");
                    String name = jsonBW.getString("Name");
                    String bike = jsonBW.getString("Bike");
                    double length = jsonBW.getDouble("SHAPE_Length");
                    JSONObject jsonGeometry = jsonBW.getJSONObject("json_geometry");

                    //only add existing bike routes
                    if (status.equals("Existing") && bike.equals("Y")) {
                        BikeWay bikeway = new BikeWay();
                        bikeway.setObjectId(objectId);
                        bikeway.setPaved(paved.equals("Y"));

                        if (bikeOnStreet.equals("Y")) {
                            bikeway.setRoadType(BikeWay.RoadType.ONSTREET);
                        } else if (bikeOffStreet.equals("Y")) {
                            bikeway.setRoadType(BikeWay.RoadType.OFFSTREET);
                        } else if (bikeLane.equals("Y")) {
                            bikeway.setRoadType(BikeWay.RoadType.BIKELANE);
                        }

                        bikeway.setName(name);
                        bikeway.setLength(length);

                        if (jsonGeometry.getString("type").equals("LineString")) { //single line
                            JSONArray jsonCoords = jsonGeometry.getJSONArray("coordinates");
                            BikeWayLine line = new BikeWayLine();

                            for (int y = 0; y < jsonCoords.length(); y++) {
                                String[] coordString = jsonCoords.getString(y).replace("[", "")
                                        .replace("]", "").split(",");
                                if (coordString.length > 1) {
                                    double myLat = Double.parseDouble(coordString[1]);
                                    double myLong = Double.parseDouble(coordString[0]);
                                    LatLng coords = new LatLng(myLat, myLong);
                                    line.addPoint(coords);
                                }
                            }

                            bikeway.addLine(line);
                        }

                        bikeWays.add(bikeway);
                    }

                }
            } catch (JSONException e) {
                System.out.println(e.toString());
            }
        }

        if (!jsonGWString.isEmpty()) {
            try {
                JSONArray jsonGWArray = new JSONArray(jsonGWString);
                for (int x = 0; x < jsonGWArray.length(); x++) {
                    JSONObject jsonGW = jsonGWArray.getJSONObject(x);

                    int objectId = jsonGW.getInt("OBJECTID");
                    String name = jsonGW.getString("Name");
                    String fullName = jsonGW.getString("FullName");
                    double length = jsonGW.getDouble("SHAPE_Length");
                    JSONObject jsonGeometry = jsonGW.getJSONObject("json_geometry");

                    GreenWay greenway = new GreenWay();
                    greenway.setObjectId(objectId);
                    greenway.setPaved(true);
                    greenway.setName(name);
                    greenway.setFullName(fullName);
                    greenway.setLength(length);

                    if (jsonGeometry.getString("type").equals("LineString")) { //single line
                        JSONArray jsonCoords = jsonGeometry.getJSONArray("coordinates");
                        BikeWayLine line = new BikeWayLine();

                        for (int y = 0; y < jsonCoords.length(); y++) {
                            String[] coordString = jsonCoords.getString(y).replace("[", "")
                                    .replace("]", "").split(",");
                            if (coordString.length > 1) {
                                double myLat = Double.parseDouble(coordString[1]);
                                double myLong = Double.parseDouble(coordString[0]);
                                LatLng coords = new LatLng(myLat, myLong);
                                line.addPoint(coords);
                            }
                        }

                        greenway.addLine(line);
                    } else { //multiline string
                        JSONArray jsonCoordsArray = jsonGeometry.getJSONArray("coordinates");

                        for (int y = 0; y < jsonCoordsArray.length(); y++) {
                            BikeWayLine line = new BikeWayLine();
                            JSONArray jsonCoords = jsonCoordsArray.getJSONArray(y);
                            for (int z = 0; z < jsonCoords.length(); z++) {
                                String[] coordString = jsonCoords.getString(z).replace("[", "")
                                        .replace("]", "").split(",");
                                if (coordString.length > 1) {
                                    double myLat = Double.parseDouble(coordString[1]);
                                    double myLong = Double.parseDouble(coordString[0]);
                                    LatLng coords = new LatLng(myLat, myLong);
                                    line.addPoint(coords);
                                }
                            }
                            greenway.addLine(line);
                        }

                    }

                    bikeWays.add(greenway);
                }
            } catch (JSONException e) {
                System.out.println(e.toString());
            }
        }
    }


    /**
     * Gets all greenways from the loaded bikeways.
     * @return
     *      ArrayList of BikeWay objects that are greenways.
     */
    public static ArrayList<GreenWay> getAllGreenways() {
        ArrayList<GreenWay> greenWays = new ArrayList<GreenWay>();

        for (BikeWay bw : bikeWays) {
            if (bw instanceof GreenWay) {
                greenWays.add((GreenWay) bw);
            }
        }

        return greenWays;
    }
}
