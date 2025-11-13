package exceptions;

public class InvalidOperationException extends Exception {

    public InvalidOperationException() {
        super("Invalid operation, try again");
    }

    // Custom msg constructor
    public InvalidOperationException(String message) {
        super(message);
    }
}
