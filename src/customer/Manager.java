package customer;

public class Manager extends Person {
    private final String libraryId;

    public Manager(String username, String password, String firstname, String lastname, String nationalID, int birthYear, String address, String libraryId) {
        super(username, password, firstname, lastname, nationalID, birthYear, address);
        this.libraryId = libraryId;
    }

    public boolean isManagerOf(String libraryId) {
        return libraryId.equals(this.libraryId);
    }
}

