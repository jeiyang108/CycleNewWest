package com.project.lyt.cyclenewwest.Manager;

import com.google.android.gms.maps.model.LatLng;
import com.project.lyt.cyclenewwest.Model.BikeWay;
import com.project.lyt.cyclenewwest.Model.BikeWayLine;
import com.project.lyt.cyclenewwest.Model.Workout;

/**
 * Class for managing workouts, predefined bike circuits based on combinations
 * of bikeways and greenways.
 */

public class WorkoutManager {
    /**
     * List of predefined workouts.
     */
    private static Workout[] workouts;

    /**
     * List of BikeWay ids comprising the short workout.
     */
    private static final int[] shortWorkoutIds = {216, 214, 215, 196, 195, 189, 193, 194, 190, 191, 192, 188, 187, 185
    , 186, 673, 674, 125, 231, 665, 666, 126, 664, 225, 650, 651, 652, 661, 222, 217, 219, 653, 218};

    /**
     * List of BikeWay ids comprising the medium workout.
     */
    private static final int[] mediumWorkoutIds = {674, 231, 673};

    /**
     * List of GreenWay names comprising the medium workout.
     */
    private static final String[] mediumWorkoutGreenWays = {"North Arm Trail", "Boundary Trail", "Annacis Channel Trail"};

    /**
     * List of GreenWay ids comprising the long workout.
     */
    private static final int[] longWorkoutIds = {47, 48, 49, 29, 30, 50, 6, 32, 5, 55, 56, 57, 58, 59, 2, 61, 60, 26, 62,
    25, 64, 63, 10, 8, 20, 19, 12, 62, 13, 37, 67, 7, 34};

    /**
     * Initializes predefined workouts.
     */
    public static void initializeWorkouts() {
        Workout shortWorkout = new Workout(Workout.WorkoutLength.SHORT);
        for (int id : shortWorkoutIds) {
            shortWorkout.addBikeWay(BikeWayManager.getBikeWayById(id));
        }

        Workout mediumWorkout = new Workout(Workout.WorkoutLength.MEDIUM);
        for (int id : mediumWorkoutIds) {
            mediumWorkout.addBikeWay(BikeWayManager.getBikeWayById(id));
        }
        for (String greenWay : mediumWorkoutGreenWays) {
            mediumWorkout.addBikeWay(BikeWayManager.getGreenWayByName(greenWay));
        }
        //add partial Port Royal Loop
        mediumWorkout.addBikeWay(BikeWayManager.getGreenWayByName("Port Royal Loop")
        .removeBikeWay(BikeWayManager.getBikeWayById(211))
                .removeBikeWay(BikeWayManager.getBikeWayById(230)));

        Workout longWorkout = new Workout(Workout.WorkoutLength.LONG);
        for (int id : longWorkoutIds) {
            longWorkout.addBikeWay(BikeWayManager.getBikeWayById(id));
        }

        //add partial lines
        longWorkout.addBikeWay(BikeWayManager.getBikeWayById(33).trim(BikeWay.Direction.NORTH, 49.199260));
        longWorkout.addBikeWay(BikeWayManager.getBikeWayById(36).trim(BikeWay.Direction.WEST, -122.948953));

        //add custom line to fill space between two paths
        BikeWay missingPath = new BikeWay();
        missingPath.addLine(
                new BikeWayLine(
                        new LatLng(49.2221552941299, -122.907736426824),
                        new LatLng(49.2227613554574, -122.906573146382)));
        longWorkout.addBikeWay(missingPath);


        workouts = new Workout[] {shortWorkout, mediumWorkout, longWorkout};
    }

    /**
     * Gets a workout based on length.
     * @param length
     *      Length enum (SHORT, MEDIUM, LONG)
     * @return
     *      The workout corresponding to that length.
     */
    public static Workout getWorkoutByLength(Workout.WorkoutLength length) {
        for (Workout workout : workouts) {
            if (workout.getLength() == length) {
                return workout;
            }
        }
        return null;
    }


    /**
     * Gets the closest location in a workout to a given location.
     * Used for determining the starting point of a workout based on its
     * proximity to the user's current location.
     * @param workout
     *      The workout to search.
     * @param location
     *      The location to check proximity.
     * @return
     *      The closest LatLng in the workout to the user's current location.
     */
    public static LatLng getClosestLocation(Workout workout, LatLng location) {
        LatLng closest = null;
        double closestDistance = 0;

        for (BikeWay bw : workout.getBikeways()) {
            for (BikeWayLine bwl : bw.getAllLines()) {
                for (LatLng latlng : bwl.getPoints()) {
                    double tempDistance = getBirdsEyeDistance(latlng, location);
                    if (closest == null || closestDistance > tempDistance) {
                        closestDistance = tempDistance;
                        closest = latlng;
                    }
                }
            }
        }

        return closest;
    }

    /**
     * Gets the absolute distance between two LatLong coordinates.
     * @param loc1
     *      The first location.
     * @param loc2
     *      The second location.
     * @return
     *      The distance between the two in KM.
     */
    private static double getBirdsEyeDistance(LatLng loc1, LatLng loc2) {
        int R = 6371; // Radius of the earth in km
        double distLat = degreesToRadians(loc2.latitude-loc1.latitude);
        double distLong = degreesToRadians(loc2.longitude-loc1.longitude);
        double a =
                Math.sin(distLat/2) * Math.sin(distLat/2) +
                        Math.cos(degreesToRadians(loc1.latitude)) * Math.cos(degreesToRadians(loc2.latitude)) *
                                Math.sin(distLong/2) * Math.sin(distLong/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }

    /**
     * Converts degrees to radians. Used for the Birds' Eye distance formula.
     * @param degrees
     *      Degrees value.
     * @return
     *      Radians value.
     */
    private static double degreesToRadians(double degrees) {
        return degrees * (Math.PI/180);
    }

}
