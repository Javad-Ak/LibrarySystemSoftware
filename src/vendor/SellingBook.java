package vendor;

public class SellingBook extends Resource implements Sellable {
    private final String publisher;
    private final int count, price, discount, finalCost;
    private int boughtCount;

    public SellingBook(String id, String title, String author, String publisher, int markYear, int count, int price, int discount, Category category, Library library) {
        super(id, title, author, markYear, category, library);
        this.count = count;
        this.publisher = publisher;
        this.price = price;
        this.discount = discount;
        boughtCount = 0;
        finalCost = price * (100 - discount) / 100;
    }

    public boolean isAvailable() {
        return boughtCount < count;
    }

    public void buy() {
        boughtCount++;
    }

    public boolean isMatched(String key) {
        return super.isMatched(key) || publisher.toLowerCase().contains(key.toLowerCase());
    }

    public int getRemainedCount() {
        return count - boughtCount;
    }

    public int getBoughtCount() {
        return boughtCount;
    }

    public int netSold() {
        return finalCost * boughtCount;
    }
}

