package vendor;

public class Book extends Resource implements Borrowable {
    private final String publisher;
    private final int count;
    private int borrowedCount, totalBorrows, daysBorrowed;

    public Book(String id, String title, String author, String publisher, int markYear, int count, Category category, Library library) {
        super(id, title, author, markYear, category, library);
        this.count = count;
        this.publisher = publisher;
        borrowedCount = 0;
        totalBorrows = 0;
        daysBorrowed = -1;
    }

    public boolean isAvailable() {
        return count > borrowedCount;
    }

    public boolean isBorrowed() {
        return borrowedCount > 0;
    }

    public void borrowResource() {
        borrowedCount++;
        totalBorrows++;
    }

    public void returnResource(int daysBorrowed) {
        borrowedCount--;
        if (this.daysBorrowed <= 0) this.daysBorrowed = 0;
        if (daysBorrowed <= 0) daysBorrowed = 1;
        this.daysBorrowed += daysBorrowed;
    }

    public boolean isMatched(String key) {
        return super.isMatched(key) || publisher.toLowerCase().contains(key.toLowerCase());
    }

    public int getRemainedCount() {
        return count - borrowedCount;
    }

    public int getBorrowedCount() {
        return borrowedCount;
    }

    public int getDaysBorrowed() {
        return daysBorrowed;
    }

    public int totalBorrows() {
        return totalBorrows;
    }
}

