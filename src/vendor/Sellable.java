package vendor;

public interface Sellable {
    String getId();

    boolean isAvailable();

    int getBoughtCount();

    int netSold();

    void buy();
}

