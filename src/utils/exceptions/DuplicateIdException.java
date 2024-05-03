package utils.exceptions;

public class DuplicateIdException extends RuntimeException {
    public DuplicateIdException(){
        super("duplicate-id");
    }
}
