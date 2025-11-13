package util;

import vehicles.Vehicle;

import java.util.Comparator;

/**
 * Comparator to sort Vehicles by model name (A -> Z).
 */
public class SortByModel implements Comparator<Vehicle> {

    @Override
    public int compare(Vehicle v1, Vehicle v2) {
        if (v1 == null && v2 == null) return 0;
        if (v1 == null) return -1;
        if (v2 == null) return 1;
        String m1 = v1.getModel() == null ? "" : v1.getModel();
        String m2 = v2.getModel() == null ? "" : v2.getModel();
        return m1.compareToIgnoreCase(m2);
    }
}
