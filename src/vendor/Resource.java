package vendor;

import java.util.ArrayList;

public abstract class Resource {
    private final String id, title, author;
    private final int markYear;
    private final Library library;
    private final Category category;
    private final ArrayList<String[]> comments;

    public Resource(String id, String title, String author, int markYear, Category category, Library library) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.library = library;
        this.markYear = markYear;
        this.category = category;
        comments = new ArrayList<>();
    }

    public void addComment(String userId, String comment) {
        comments.add(new String[]{userId, comment});
    }

    public String getId() {
        return id;
    }

    public boolean isMatched(String key) {
        return title.toLowerCase().contains(key.toLowerCase()) || author.toLowerCase().contains(key.toLowerCase());
    }

    public String getCategory() {
        return category.getId();
    }
}

