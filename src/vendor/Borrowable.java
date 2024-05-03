package vendor;

public interface Borrowable {
    String getId();

    boolean isAvailable();

    boolean isBorrowed();

    void borrowResource();

    void returnResource(int minutesBorrowed);

    int totalBorrows();

    int getDaysBorrowed();
}

