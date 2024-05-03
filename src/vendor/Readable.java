package vendor;

import java.time.LocalDateTime;

public interface Readable {
    boolean isAvailable(LocalDateTime dateTime);

    void read(LocalDateTime dateTime);
}

