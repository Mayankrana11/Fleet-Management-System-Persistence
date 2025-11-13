package exceptions;

public class InsufficientFuelException extends Exception {

    public InsufficientFuelException() {
        super("Not enough fuel, please refuel.");
    }

    // Custom msg constructor
    public InsufficientFuelException(String message) {
        super(message);
    }
}
