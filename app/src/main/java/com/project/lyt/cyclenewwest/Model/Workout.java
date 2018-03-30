package com.project.lyt.cyclenewwest.Model;

import java.util.ArrayList;

/**
 * Represents a workout, a pre-defined concatenation of different bikeways and
 * greenways that forms a loop and
 * approximates a certain distance in kilometres.
 */
public class Workout {
    /**
     * Workout length.
     * SHORT ~= 5km
     * MEDIUM ~= 10km
     * LONG ~= 20km
     */
    public enum WorkoutLength {
        SHORT,
        MEDIUM,
        LONG
    }

    /**
     * BikeWays comprising the workout.
     */
    private ArrayList<BikeWay> bikeways;

    /**
     * Length of the workout.
     */
    private WorkoutLength length;

    /**
     * Creates a new Workout.
     * @param length
     *      Workout length.
     */
    public Workout(WorkoutLength length) {
        this.length = length;
        bikeways = new ArrayList<BikeWay>();
    }

    /**
     * Adds a BikeWay to the workout.
     * @param bw
     *      The BikeWay to add.
     */
    public void addBikeWay(BikeWay bw) {
        bikeways.add(bw);
    }

    /**
     * Gets all BikeWays in the workout.
     * @return
     *      ArrayList of BikeWay objects.
     */
    public ArrayList<BikeWay> getBikeways() {
        return bikeways;
    }

    /**
     * Gets the workout length.
     * @return
     *      WorkoutLength value.
     */
    public WorkoutLength getLength() {
        return length;
    }


}
