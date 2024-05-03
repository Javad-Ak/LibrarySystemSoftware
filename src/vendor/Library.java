package vendor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import customer.*;

public class Library {
    private final HashMap<String, Resource> resources;
    private final ArrayList<BorrowSheet> borrowSheets;
    private final String id, name, address;
    private final int establishedYear, tableCount;

    public Library(String id, String name, String address, int establishedYear, int tableCount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.establishedYear = establishedYear;
        this.tableCount = tableCount;
        resources = new HashMap<>();
        borrowSheets = new ArrayList<>();
    }

    public void addResource(Resource newRes) {
        resources.put(newRes.getId(), newRes);
    }

    public void removeResource(String id) {
        resources.remove(id);
    }

    public boolean resourceExists(String id) {
        return resources.containsKey(id);
    }

    public Resource getResource(String id) {
        return resources.get(id);
    }

    public void borrow(BorrowSheet sheet) {
        borrowSheets.add(sheet);
    }

    public boolean canBorrow(User user, Resource resource) {
        if (!(resource instanceof Borrowable)) return false;
        return user.canBorrow() && ((Borrowable) resource).isAvailable() && returnSheet(user.getUsername(), resource.getId()) == null;
    }

    public BorrowSheet returnSheet(String userId, String resourceId) {
        for (BorrowSheet sheet : borrowSheets) {
            if (!sheet.isReturned() && sheet.getUserId().equals(userId) && sheet.getResourceId().equals(resourceId))
                return sheet;
        }
        return null;
    }

    public List<String> searchResource(String key) {
        ArrayList<String> res = new ArrayList<>();
        for (Resource resource : resources.values()) if (resource.isMatched(key)) res.add(resource.getId());
        return res;
    }

    public String categoryReport(List<String> catIds) {
        int booksNo = 0, thesesNo = 0, rareBooksNo = 0, sellingBooksNo = 0;
        for (Resource resource : resources.values()) {
            for (String catId : catIds) {
                if (resource.getCategory().equals(catId)) {
                    if (resource instanceof Book)
                        booksNo += ((Book) resource).getRemainedCount();
                    else if (resource instanceof Thesis && ((Thesis) resource).isAvailable())
                        thesesNo++;
                    else if (resource instanceof RareBook)
                        rareBooksNo++;
                    else if (resource instanceof SellingBook)
                        sellingBooksNo += ((SellingBook) resource).getRemainedCount();
                    break;
                }
            }
        }
        return booksNo + " " + thesesNo + " " + rareBooksNo + " " + sellingBooksNo;
    }

    public String libraryReport() {
        int booksNo = 0, thesesNo = 0, borrowedBooksNo = 0, borrowedThesesNo = 0, rareBooksNo = 0, remainedSellingBooksNo = 0;
        for (Resource resource : resources.values()) {
            if (resource instanceof Book) {
                booksNo += ((Book) resource).getRemainedCount();
                borrowedBooksNo += ((Book) resource).getBorrowedCount();
            } else if (resource instanceof Thesis) {
                if (((Thesis) resource).isAvailable()) thesesNo++;
                else borrowedThesesNo++;
            } else if (resource instanceof RareBook) {
                rareBooksNo++;
            } else if (resource instanceof SellingBook)
                remainedSellingBooksNo += ((SellingBook) resource).getRemainedCount();
        }

        return booksNo + " " + thesesNo + " " + borrowedBooksNo + " " + borrowedThesesNo + " " +
                rareBooksNo + " " + remainedSellingBooksNo;
    }

    public List<String> passedDeadlinesReport(LocalDateTime date) {
        ArrayList<String> res = new ArrayList<>();
        for (BorrowSheet sheet : borrowSheets) {
            if (!sheet.isReturned() && sheet.calcPenalty(date) > 0) res.add(sheet.getResourceId());
        }
        return res.stream().distinct().sorted().toList();
    }

    public String mostPopularReport() {
        Borrowable mostPopularBook = null;
        Borrowable mostPopularThesis = null;
        for (Borrowable resource : resources.values().stream().filter(obj -> obj instanceof Borrowable).map(obj -> (Borrowable) obj).toList()) {
            if (resource instanceof Book) {
                if (mostPopularBook == null || resource.getDaysBorrowed() > mostPopularBook.getDaysBorrowed())
                    mostPopularBook = resource;
            } else if (resource instanceof Thesis) {
                if (mostPopularThesis == null || resource.getDaysBorrowed() > mostPopularThesis.getDaysBorrowed())
                    mostPopularThesis = resource;
            }
        }

        StringBuilder res1 = new StringBuilder();
        StringBuilder res2 = new StringBuilder();
        if (mostPopularBook == null || mostPopularBook.getDaysBorrowed() <= 0) {
            res1.append("null");
        } else {
            res1.append(mostPopularBook.getId()).append(" ").append(mostPopularBook.totalBorrows()).append(" ").append(mostPopularBook.getDaysBorrowed());
        }
        if (mostPopularThesis == null || mostPopularThesis.getDaysBorrowed() <= 0) {
            res2.append("null");
        } else {
            res2.append(mostPopularThesis.getId()).append(" ").append(mostPopularThesis.totalBorrows()).append(" ").append(mostPopularThesis.getDaysBorrowed());
        }
        return res1 + "\n" + res2;
    }

    public String mostSoldReport() {
        Sellable mostSoldBook = null;
        int netSold = 0, soldCount = 0;
        for (Sellable resource : resources.values().stream().filter(obj -> obj instanceof Sellable).map(obj -> (Sellable) obj).toList()) {
            soldCount += resource.getBoughtCount();
            netSold += resource.netSold();
            if (mostSoldBook == null || resource.getBoughtCount() > mostSoldBook.getBoughtCount())
                mostSoldBook = resource;
        }

        if (mostSoldBook == null || mostSoldBook.getBoughtCount() <= 0) return "null";

        return soldCount + " " + netSold + "\n" + mostSoldBook.getId() + " " + mostSoldBook.getBoughtCount() + " " + mostSoldBook.netSold();
    }
}

