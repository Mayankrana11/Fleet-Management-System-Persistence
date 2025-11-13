package vehicles;


//Abstract class for all land-based vehicles.

public abstract class LandVehicle extends Vehicle {

    private int numWheels;

    public LandVehicle(String id, String model, double maxSpeed, int numWheels) {
        super(id, model, maxSpeed);
        this.numWheels = numWheels;
    }

    // Journey time calculation
    @Override
    public double estimateJourneyTime(double distance) {
        if (distance < 0) return 0;
        return distance / getMaxSpeed();
    }

    public int getNumWheels() {
        return numWheels;
    }
}
