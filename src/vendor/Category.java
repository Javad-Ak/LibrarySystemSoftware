package vendor;

public class Category {
    private final String id, name;
    private final Category parentCategory;

    public Category(String id, String name, Category parentCategory) {
        this.id = id;
        this.name = name;
        this.parentCategory = parentCategory;
    }

    public String getId() {
        return id;
    }

    public Category getParent() {
        return parentCategory;
    }
}

