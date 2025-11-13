package interfaces;

import exceptions.InvalidOperationException;
import exceptions.InsufficientFuelException;


//Interface for vehicles that consume fuel.

public interface FuelConsumable {
    void refuel(double amount) throws InvalidOperationException;
    double getFuelLevel();
    double consumeFuel(double distance) throws InsufficientFuelException;
}
