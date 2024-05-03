package utils.exceptions;

public class InvalidPassException extends RuntimeException {
    public InvalidPassException(){
        super("invalid-pass");
    }
}
