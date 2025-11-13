package util;

import vehicles.Vehicle;

import java.util.Comparator;

/**
 * Comparator to sort Vehicles by max speed, descending (fastest first).
 */
public class SortBySpeed implements Comparator<Vehicle> {

    @Override
    public int compare(Vehicle v1, Vehicle v2) {
        if (v1 == null && v2 == null) return 0;
        if (v1 == null) return 1;
        if (v2 == null) return -1;
        return Double.compare(v2.getMaxSpeed(), v1.getMaxSpeed()); // descending
    }
}
