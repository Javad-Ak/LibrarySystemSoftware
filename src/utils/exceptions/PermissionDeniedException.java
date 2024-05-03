package utils.exceptions;

public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException(){
        super("permission-denied");
    }
}
