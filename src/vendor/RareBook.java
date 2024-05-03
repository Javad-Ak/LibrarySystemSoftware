package vendor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class RareBook extends Resource implements Readable {
    private final String publisher, donor;
    private final ArrayList<LocalDateTime> reservedDates;

    public RareBook(String id, String title, String author, String publisher, int markYear, String donor, Category category, Library library) {
        super(id, title, author, markYear, category, library);
        this.publisher = publisher;
        this.donor = donor.equals("null") ? null : donor;
        reservedDates = new ArrayList<>();
    }

    public void read(LocalDateTime dateTime) {
        reservedDates.add(dateTime);
    }

    public boolean isAvailable(LocalDateTime dateTime) {
        for (LocalDateTime current : reservedDates) {
            if (current.until(dateTime, ChronoUnit.MINUTES) < 120 && current.until(dateTime, ChronoUnit.MINUTES) > -120)
                return false;
        }
        return true;
    }

    public boolean isMatched(String key) {
        return super.isMatched(key) || publisher.toLowerCase().contains(key.toLowerCase());
    }
}

