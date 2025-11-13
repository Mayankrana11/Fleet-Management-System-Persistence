package vehicles;

//Abstract class for all water-based vehicles.

public abstract class WaterVehicle extends Vehicle {

    public WaterVehicle(String id, String model, double maxSpeed) {
        super(id, model, maxSpeed);
    }

    // Override journey time: slower due to water resistance
    @Override
    public double estimateJourneyTime(double distance) {
        if (distance < 0) return 0;
        double baseTime = distance / getMaxSpeed();
        return baseTime * 1.2; // 20 perc slower on water
    }
}
