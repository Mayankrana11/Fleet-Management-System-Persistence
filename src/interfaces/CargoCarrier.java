package interfaces;

import exceptions.InvalidOperationException;
import exceptions.OverloadException;


 //Interface for vehicles that carry cargo.

public interface CargoCarrier {

    void loadCargo(double weight) throws OverloadException;
    void unloadCargo(double weight) throws InvalidOperationException;
    double getCargoCapacity();
    double getCurrentCargo();
}
