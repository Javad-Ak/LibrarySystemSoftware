package customer;

public abstract class User {
    private final String username, password;
    private int borrowCount;
    private long penalty;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        borrowCount = 0;
        penalty = 0;
    }

    public boolean canBorrow() {
        return borrowCount < (this instanceof Student ? 3 : 5) && penalty <= 0;
    }

    public boolean hasBorrowed() {
        return borrowCount > 0;
    }

    public void borrowResource() {
        borrowCount++;
    }

    public void returnResource(long penalty) {
        borrowCount--;
        this.penalty += penalty;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public long getPenalty() {
        return penalty;
    }
}

