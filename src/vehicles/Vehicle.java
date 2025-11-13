package vehicles;

import exceptions.InvalidOperationException;

/**
 * Base abstract class for all vehicles in the Fleet Management System.
 * Tracks mileage and provides fuel-efficiency comparison.
 */
public abstract class Vehicle implements Comparable<Vehicle> {

    private String id;
    private String model;
    private double maxSpeed;
    private double currentMileage;

    // Track mileage at last maintenance
    private double lastMaintenanceMileage = 0.0;

    public Vehicle(String id, String model, double maxSpeed) {
        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.currentMileage = 0.0;
    }


    // Abstract methods

    public abstract void move(double distance) throws InvalidOperationException, Exception;
    public abstract double calculateFuelEfficiency();
    public abstract double estimateJourneyTime(double distance);


    // Getters and Setters

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getCurrentMileage() {
        return currentMileage;
    }

    //  Added public setter used by persistence & maintenance
    public void setCurrentMileage(double mileage) {
        if (mileage < 0) this.currentMileage = 0;
        else this.currentMileage = mileage;
    }

    // Update mileage after a journey
    public void addMileage(double distance) {
        if (distance > 0) currentMileage += distance;
    }

    // Maintenance tracking helpers
    public double getLastMaintenanceMileage() {
        return lastMaintenanceMileage;
    }

    public void setLastMaintenanceMileage(double mileage) {
        this.lastMaintenanceMileage = mileage;
    }


    // Comparison sort by efficiency (descending)
    @Override
    public int compareTo(Vehicle other) {
        return Double.compare(other.calculateFuelEfficiency(), this.calculateFuelEfficiency());
    }


    // Display info
    public void displayInfo() {
        System.out.println("ID: " + id +
                           ", Model: " + model +
                           ", Max Speed: " + maxSpeed +
                           " km/h, Mileage: " + currentMileage + " km");
    }
}
