package exceptions;

public class OverloadException extends Exception {

    public OverloadException() {
        super("Overload occurred, try reducing the load.");
    }

    // Custom msg constructor
    public OverloadException(String message) {
        super(message);
    }
}
