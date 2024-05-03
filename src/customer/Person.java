package customer;

public abstract class Person extends User {
    private final String firstname, lastname, NationalId, address;
    private final int birthYear;

    public Person(String username, String password, String firstname, String lastname, String nationalID, int birthYear, String address) {
        super(username, password);
        this.firstname = firstname;
        this.lastname = lastname;
        NationalId = nationalID;
        this.address = address;
        this.birthYear = birthYear;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
}
