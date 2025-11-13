package vehicles;

import interfaces.*;
import exceptions.*;

//bus class
public class Bus extends LandVehicle implements FuelConsumable, CargoCarrier, PassengerCarrier, Maintainable {

    private double fuelLevel;
    private int passengerCapacity = 50;
    private int currentPassengers;
    private double cargoCapacity = 500;
    private double currentCargo;
    private boolean maintenanceNeeded;

    public Bus(String id, String model, double maxSpeed, int numWheels) {
        super(id, model, maxSpeed, numWheels);
        this.fuelLevel = 0;
        this.currentPassengers = 0;
        this.currentCargo = 0;
        this.maintenanceNeeded = false;
    }

    // Movement
    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) {
            throw new InvalidOperationException("Distance cannot be negative.");
        }
        double requiredFuel = distance / calculateFuelEfficiency();
        if (fuelLevel < requiredFuel) {
            throw new InsufficientFuelException("Not enough fuel, please refuel.");
        }
        fuelLevel -= requiredFuel;
        addMileage(distance);
        System.out.println("Transporting passengers and cargo for " + distance + " km.");
    }

    @Override
    public double calculateFuelEfficiency() {
        return 10.0; // km per liter
    }

    // FuelConsumable
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) {
            throw new InvalidOperationException("Refuel amount must be positive.");
        }
        fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        return fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double required = distance / calculateFuelEfficiency();
        if (fuelLevel < required) {
            throw new InsufficientFuelException("Not enough fuel, please refuel.");
        }
        fuelLevel -= required;
        return required;
    }

    // Passenger Carrier
    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (currentPassengers + count > passengerCapacity) {
            throw new OverloadException("Overload occurred, too many passengers.");
        }
        currentPassengers += count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count > currentPassengers) {
            throw new InvalidOperationException("Cannot disembark more passengers than onboard.");
        }
        currentPassengers -= count;
    }

    @Override
    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    @Override
    public int getCurrentPassengers() {
        return currentPassengers;
    }

    // Cargo Carrier
    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (currentCargo + weight > cargoCapacity) {
            throw new OverloadException("Overload occurred, cargo exceeds capacity.");
        }
        currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight > currentCargo) {
            throw new InvalidOperationException("Cannot unload more cargo than currently loaded.");
        }
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

    // Maintainable
    @Override
    public void scheduleMaintenance() {
        maintenanceNeeded = true;
    }

    @Override
    public boolean needsMaintenance() {
        return maintenanceNeeded || 
               (getCurrentMileage() - getLastMaintenanceMileage()) > 10000;
    }

    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        setLastMaintenanceMileage(getCurrentMileage());
        System.out.println("Bus " + getId() + " has been serviced.");
    }
}
