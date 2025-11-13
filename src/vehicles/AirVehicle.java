package vehicles;


// Abstract class for all air-based vehicles.

public abstract class AirVehicle extends Vehicle {

    private double maxAltitude; // maximum alt

    public AirVehicle(String id, String model, double maxSpeed, double maxAltitude) {
        super(id, model, maxSpeed);
        this.maxAltitude = maxAltitude;
    }

    // Override journey time: 5% faster for direct air routes
    @Override
    public double estimateJourneyTime(double distance) {
        if (distance < 0) return 0;
        double baseTime = distance / getMaxSpeed();
        return baseTime * 0.95;
    }

    public double getMaxAltitude() {
        return maxAltitude;
    }
}
