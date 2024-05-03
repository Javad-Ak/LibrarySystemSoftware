package vendor;

public class Thesis extends Resource implements Borrowable {
    private final String professorName;
    private boolean isAvailable;
    private int totalBorrows, daysBorrowed;

    public Thesis(String id, String title, String author, String professorName, int markYear, Category category, Library library) {
        super(id, title, author, markYear, category, library);
        this.professorName = professorName;
        isAvailable = true;
        totalBorrows = 0;
        daysBorrowed = -1;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isBorrowed() {
        return !isAvailable;
    }

    public void borrowResource() {
        isAvailable = false;
        totalBorrows++;
    }

    public void returnResource(int daysBorrowed) {
        isAvailable = true;
        if (this.daysBorrowed <= 0) this.daysBorrowed = 0;
        if (daysBorrowed <= 0) daysBorrowed = 1;
        this.daysBorrowed += daysBorrowed;
    }

    public boolean isMatched(String key) {
        return super.isMatched(key) || professorName.toLowerCase().contains(key.toLowerCase());
    }

    public int getDaysBorrowed() {
        return daysBorrowed;
    }

    public int totalBorrows() {
        return totalBorrows;
    }
}

