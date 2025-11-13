package util;

import vehicles.Vehicle;
import vehicles.CargoShip;
import interfaces.FuelConsumable;

import java.util.Comparator;

/**
 * Comparator to sort Vehicles by effective fuel efficiency, descending.
 *  Non-fuel vehicles and sail-powered CargoShips are treated as +Infinity
 *  so they appear first in "most efficient" lists.
 */
public class SortByEfficiency implements Comparator<Vehicle> {

    @Override
    public int compare(Vehicle v1, Vehicle v2) {
        double e1 = effectiveEfficiency(v1);
        double e2 = effectiveEfficiency(v2);
        // Descending order: higher (including +INF) first
        return Double.compare(e2, e1);
    }

    private double effectiveEfficiency(Vehicle v) {
        if (v == null) return 0.0;

        // Sail-powered CargoShips → infinite efficiency
        if (v instanceof CargoShip ship && ship.isSailPowered()) {
            return Double.POSITIVE_INFINITY;
        }

        // Fuel-based vehicles
        if (v instanceof FuelConsumable) {
            double eff = safe(v.calculateFuelEfficiency());
            return eff;
        }

        // Non-fuel vehicles (e.g., electric or abstract ones)
        return Double.POSITIVE_INFINITY;
    }

    private double safe(double d) {
        if (Double.isNaN(d) || Double.isInfinite(d)) return 0.0;
        return d;
    }
}
