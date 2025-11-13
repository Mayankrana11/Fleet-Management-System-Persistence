package vehicles;

import interfaces.CargoCarrier;
import interfaces.Maintainable;
import interfaces.FuelConsumable;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import exceptions.InsufficientFuelException;

/**
 * CargoShip
 * -------------------------------------------
 * FINAL WORKING VERSION
 * ✅ Resets mileage & maintenance flags correctly
 * ✅ Works perfectly with FleetManager
 */
public class CargoShip extends WaterVehicle implements CargoCarrier, Maintainable, FuelConsumable {

    private final double cargoCapacity;
    private double currentCargo;
    private boolean maintenanceNeeded;
    private double fuelLevel;
    private final boolean sailPowered;

    public CargoShip(String id, String model, double maxSpeed, boolean sailPowered) {
        super(id, model, maxSpeed);
        this.cargoCapacity = 50000.0;
        this.currentCargo = 0.0;
        this.maintenanceNeeded = false;
        this.fuelLevel = 0.0;
        this.sailPowered = sailPowered;
    }

    public boolean isSailPowered() {
        return sailPowered;
    }

    // -------------------------------------------------------------
    // Movement and Fuel Operations
    // -------------------------------------------------------------
    @Override
    public void move(double distance) throws InvalidOperationException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative.");

        if (!sailPowered) {
            double fuelNeeded = distance / calculateFuelEfficiency();
            if (fuelLevel < fuelNeeded)
                throw new InvalidOperationException("Not enough fuel for the journey.");
            fuelLevel -= fuelNeeded;
        }

        addMileage(distance);
        System.out.println("Sailing " + (sailPowered ? "using wind power" : "using engines")
                + " for " + distance + " km.");
    }

    @Override
    public double calculateFuelEfficiency() {
        return sailPowered ? 0.0 : 4.0;
    }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (sailPowered)
            throw new InvalidOperationException("Sail-powered ships cannot refuel.");
        if (amount <= 0)
            throw new InvalidOperationException("Refuel amount must be positive.");
        fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        return fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        if (sailPowered)
            throw new InsufficientFuelException("Sail-powered ships do not consume fuel.");

        double fuelUsed = distance / calculateFuelEfficiency();
        if (fuelLevel < fuelUsed)
            throw new InsufficientFuelException("Insufficient fuel for distance.");

        fuelLevel -= fuelUsed;
        return fuelUsed;
    }

    // -------------------------------------------------------------
    // Cargo Operations
    // -------------------------------------------------------------
    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (currentCargo + weight > cargoCapacity)
            throw new OverloadException("Cargo exceeds capacity!");
        currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight > currentCargo)
            throw new InvalidOperationException("Cannot unload more cargo than currently loaded.");
        currentCargo -= weight;
    }

    @Override
    public double getCargoCapacity() {
        return cargoCapacity;
    }

    @Override
    public double getCurrentCargo() {
        return currentCargo;
    }


    // Maintenance Operations

    @Override
    public void scheduleMaintenance() {
        maintenanceNeeded = true;
    }

    @Override
    public boolean needsMaintenance() {
        return getCurrentMileage() > 10000 || maintenanceNeeded;
    }

    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        setCurrentMileage(0);
        setLastMaintenanceMileage(0);
        System.out.println("Maintenance completed for CargoShip: " + getId());
    }


    // Display
    @Override
    public void displayInfo() {
        String powerTag = isSailPowered() ? " (SAIL)" : " (FUEL)";
        System.out.printf(
                "ID: %s, Model: %s%s, Max Speed: %.1f km/h, Mileage: %.1f km%n",
                getId(), getModel(), powerTag, getMaxSpeed(), getCurrentMileage()
        );
    }
}
