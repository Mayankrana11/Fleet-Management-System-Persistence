package interfaces;

import exceptions.InvalidOperationException;
import exceptions.OverloadException;

//carriers inf
public interface PassengerCarrier {
    void boardPassengers(int count) throws OverloadException;
    void disembarkPassengers(int count) throws InvalidOperationException;
    int getPassengerCapacity();
    int getCurrentPassengers();
}
