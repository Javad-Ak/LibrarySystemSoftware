package customer;

import vendor.Book;
import vendor.Borrowable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class BorrowSheet {
    private final LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    final private User user;
    final private Borrowable resource;
    private long penalty;
    private boolean isReturned;

    public BorrowSheet(User user, Borrowable resource, LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
        this.user = user;
        this.resource = resource;
        returnDate = null;
        penalty = 0;
        isReturned = false;
    }

    public long returnSheet(LocalDateTime Date) {
        isReturned = true;
        returnDate = Date;
        penalty = calcPenalty(returnDate);
        return penalty;
    }

    public long calcPenalty(LocalDateTime returnDate) {
        int limitDay, hourFine;
        if (user instanceof Student) {
            limitDay = resource instanceof Book ? 10 : 7;
            hourFine = 50;
        } else {
            limitDay = resource instanceof Book ? 14 : 10;
            hourFine = 100;
        }

        long timeSpan = (long) Math.ceil(borrowDate.until(returnDate, ChronoUnit.MINUTES) / 60.0);
        long res = (timeSpan - limitDay * 24) * hourFine;
        return res > 0 ? res : 0;
    }

    public int daysBorrowed() {
        return (int) Math.ceil(borrowDate.until(returnDate, ChronoUnit.HOURS) / 24.0);
    }

    public boolean isReturned() {
        return isReturned;
    }

    public String getUserId() {
        return user.getUsername();
    }

    public String getResourceId() {
        return resource.getId();
    }
}
