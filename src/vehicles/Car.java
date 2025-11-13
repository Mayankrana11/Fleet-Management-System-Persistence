package vehicles;

import interfaces.*;
import exceptions.*;

// Car class
public class Car extends LandVehicle implements FuelConsumable, PassengerCarrier, Maintainable {

    private double fuelLevel;
    private int passengerCapacity = 5;
    private int currentPassengers;
    private boolean maintenanceNeeded;

    public Car(String id, String model, double maxSpeed, int numWheels) {
        super(id, model, maxSpeed, numWheels);
        this.fuelLevel = 0;
        this.currentPassengers = 0;
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
        addMileage(distance); //updates total mileage
        System.out.println("Driving on road for " + distance + " km.");
    }

    @Override
    public double calculateFuelEfficiency() {
        return 15.0; // km per liter
    }

    // Fuel Consumable
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
            throw new OverloadException("Overload occurred, try reducing passengers.");
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

    // Maintainable
    @Override
    public void scheduleMaintenance() {
        maintenanceNeeded = true;
    }

    @Override
    public boolean needsMaintenance() {
        //check since last maintenance
        return maintenanceNeeded || (getCurrentMileage() - getLastMaintenanceMileage() > 10000);
    }

    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        setLastMaintenanceMileage(getCurrentMileage()); //record service point
        System.out.println("Car " + getId() + " has been serviced.");
    }
}
