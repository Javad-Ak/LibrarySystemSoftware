package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vendor.Readable;
import vendor.*;
import customer.*;
import utils.exceptions.*;

/**
 * LibraryManagement Class can be used to create libraries and categories and to add or remove various resources.
 *
 * @author Mohammad Javad Akbari
 */
public class LibraryManagement {
    private static LibraryManagement singleton = null; // singleton object.

    private final HashMap<String, Library> libraries;
    private final HashMap<String, Category> categories;
    private final HashMap<String, User> users;

    private LibraryManagement() {
        libraries = new HashMap<>();
        categories = new HashMap<>();
        users = new HashMap<>();

        Category nullCat = new Category("null", "null", null);
        categories.put("null", nullCat);
        Admin admin = new Admin("admin", "AdminPass");
        users.put("admin", admin);
    }

    public static LibraryManagement getSingleton() {
        if (singleton == null) singleton = new LibraryManagement();
        return singleton;
    }

    public void addLibrary(String accessName, String accessWord, String id, String name, int establishedYear, int tableCount, String address) {
        authenticateAdmin(accessName, accessWord);
        if (libraries.containsKey(id)) throw new DuplicateIdException();

        libraries.put(id, new Library(id, name, address, establishedYear, tableCount));
    }

    public void addCategory(String accessName, String accessWord, String id, String name, String parentId) {
        authenticateAdmin(accessName, accessWord);

        if (categories.containsKey(id)) throw new DuplicateIdException();
        if (!parentId.equals("null") && !categories.containsKey(parentId)) throw new NotFoundException();

        categories.put(id, new Category(id, name, parentId.equals("null") ? null : categories.get(parentId)));
    }

    public void addUser(String accessName, String accessWord, String username, String password, String firstname,
                        String lastname, String nationalID, int birthYear, String address, char position) {
        authenticateAdmin(accessName, accessWord);
        if (users.containsKey(username)) throw new DuplicateIdException();

        users.put(username,
                switch (position) {
                    case 'l' -> new Student(username, password, firstname, lastname, nationalID, birthYear, address);
                    case 's' -> new Staff(username, password, firstname, lastname, nationalID, birthYear, address);
                    case 'p' -> new Professor(username, password, firstname, lastname, nationalID, birthYear, address);
                    default -> null;
                });
    }

    public void addManager(String accessName, String accessWord, String username, String password, String firstname,
                           String lastname, String nationalID, int birthYear, String address, String libId) {
        authenticateAdmin(accessName, accessWord);
        if (users.containsKey(username)) throw new DuplicateIdException();
        if (!libraries.containsKey(libId)) throw new NotFoundException();

        users.put(username, new Manager(username, password, firstname, lastname, nationalID, birthYear, address, libId));
    }

    public void removeUser(String accessName, String accessWord, String username) {
        authenticateAdmin(accessName, accessWord);
        if (!users.containsKey(username)) throw new NotFoundException();
        if (users.get(username).hasBorrowed() || users.get(username).getPenalty() > 0) throw new NotAllowedException();

        users.remove(username);
    }

    public void addBook(String accessName, String accessWord, String id, String title, String author, String publisher, int markYear, int count, String catId, String libId) {
        authenticateManager(accessName, accessWord, id, catId, libId);
        libraries.get(libId).addResource(new Book(id, title, author, publisher, markYear, count, categories.get(catId), libraries.get(libId)));
    }

    public void addThesis(String accessName, String accessWord, String id, String title, String author, String professorName, int markYear, String catId, String libId) {
        authenticateManager(accessName, accessWord, id, catId, libId);
        libraries.get(libId).addResource(new Thesis(id, title, author, professorName, markYear, categories.get(catId), libraries.get(libId)));
    }

    public void addRareBook(String accessName, String accessWord, String id, String title, String author, String publisher, int markYear, String donor, String catId, String libId) {
        authenticateManager(accessName, accessWord, id, catId, libId);
        libraries.get(libId).addResource(new RareBook(id, title, author, publisher, markYear, donor, categories.get(catId), libraries.get(libId)));
    }

    public void addSellingBook(String accessName, String accessWord, String id, String title, String author,
                               String publisher, int markYear, int count, int price, int discount, String catId, String libId) {
        authenticateManager(accessName, accessWord, id, catId, libId);
        libraries.get(libId).addResource(new SellingBook(id, title, author, publisher, markYear, count, price, discount, categories.get(catId), libraries.get(libId)));
    }

    public void removeResource(String accessName, String accessWord, String id, String libId) {
        if (!libraries.containsKey(libId) || !users.containsKey(accessName) || !libraries.get(libId).resourceExists(id))
            throw new NotFoundException();
        if (!users.get(accessName).getPassword().equals(accessWord))
            throw new InvalidPassException();
        if (!(users.get(accessName) instanceof Manager) || !((Manager) users.get(accessName)).isManagerOf(libId))
            throw new PermissionDeniedException();

        Resource resource = libraries.get(libId).getResource(id);
        if (resource instanceof Borrowable && ((Borrowable) resource).isBorrowed()) throw new NotAllowedException();

        libraries.get(libId).removeResource(id);
    }

    public void borrowResource(String username, String password, String libId, String resourceId, String dateStr, String timeStr) {
        if (!libraries.containsKey(libId) || !users.containsKey(username) || !libraries.get(libId).resourceExists(resourceId))
            throw new NotFoundException();

        Library library = libraries.get(libId);
        User user = users.get(username);

        if (!user.getPassword().equals(password))
            throw new InvalidPassException();
        if (!library.canBorrow(users.get(username), library.getResource(resourceId)))
            throw new NotAllowedException();

        Borrowable borrowed = (Borrowable) library.getResource(resourceId);
        borrowed.borrowResource();
        user.borrowResource();
        library.borrow(new BorrowSheet(user, borrowed, dateTimeParser(dateStr, timeStr)));
    }

    public String returnResource(String username, String password, String libId, String resourceId, String dateStr, String timeStr) {
        if (!libraries.containsKey(libId) || !users.containsKey(username) || !libraries.get(libId).resourceExists(resourceId))
            throw new NotFoundException();

        Library library = libraries.get(libId);
        User user = users.get(username);
        BorrowSheet sheet = library.returnSheet(username, resourceId);
        if (sheet == null) throw new NotFoundException();
        if (!user.getPassword().equals(password)) throw new InvalidPassException();

        long penalty = sheet.returnSheet(dateTimeParser(dateStr, timeStr));
        user.returnResource(penalty);
        ((Borrowable) library.getResource(resourceId)).returnResource(sheet.daysBorrowed());
        return penalty == 0 ? "success" : penalty + "";
    }

    public void buy(String username, String password, String libId, String resourceId) {
        if (!libraries.containsKey(libId) || !users.containsKey(username) || !libraries.get(libId).resourceExists(resourceId))
            throw new NotFoundException();
        if (!users.get(username).getPassword().equals(password)) throw new InvalidPassException();
        if (users.get(username) instanceof Manager) throw new PermissionDeniedException();

        Resource sold = libraries.get(libId).getResource(resourceId);
        if (!(sold instanceof Sellable) || !((Sellable) sold).isAvailable() || users.get(username).getPenalty() > 0)
            throw new NotAllowedException();

        ((Sellable) sold).buy();
    }

    public void read(String username, String password, String libId, String resourceId, String dateStr, String timeStr) {
        if (!libraries.containsKey(libId) || !users.containsKey(username) || !libraries.get(libId).resourceExists(resourceId))
            throw new NotFoundException();
        if (!users.get(username).getPassword().equals(password)) throw new InvalidPassException();
        if (!(users.get(username) instanceof Professor)) throw new PermissionDeniedException();

        Resource wanted = libraries.get(libId).getResource(resourceId);
        LocalDateTime date = dateTimeParser(dateStr, timeStr);
        if (!(wanted instanceof Readable) || !((Readable) wanted).isAvailable(date) || users.get(username).getPenalty() > 0)
            throw new NotAllowedException();

        ((Readable) wanted).read(date);
    }

    public void addComment(String username, String password, String libId, String resourceId, String comment) {
        if (!libraries.containsKey(libId) || !users.containsKey(username) || !libraries.get(libId).resourceExists(resourceId))
            throw new NotFoundException();
        if (!users.get(username).getPassword().equals(password)) throw new InvalidPassException();
        if (!(users.get(username) instanceof Student) && !(users.get(username) instanceof Professor))
            throw new PermissionDeniedException();


        libraries.get(libId).getResource(resourceId).addComment(username, comment);
    }

    public String searchResources(String key) {
        ArrayList<String> res = new ArrayList<>();
        for (Library lib : libraries.values()) res.addAll(lib.searchResource(key));

        StringBuilder out = new StringBuilder();
        res.stream().sorted().forEach(txt -> out.append("|").append(txt));
        return out.length() > 1 ? out.substring(1) : "not-found";
    }

    public String searchUsers(String username, String password, String key) {
        if (!users.containsKey(username)) throw new NotFoundException();
        if (!users.get(username).getPassword().equals(password)) throw new InvalidPassException();
        if (users.get(username) instanceof Student || users.get(username) instanceof Admin)
            throw new PermissionDeniedException();

        ArrayList<String> res = new ArrayList<>();
        for (User user : users.values().stream().filter(obj -> obj instanceof Person).toList()) {
            if (((Person) user).getFirstname().toLowerCase().contains(key.toLowerCase()) || ((Person) user).getLastname().toLowerCase().contains(key.toLowerCase()))
                res.add(user.getUsername());
        }

        StringBuilder out = new StringBuilder();
        res.stream().sorted().forEach(txt -> out.append("|").append(txt));
        return out.length() > 1 ? out.substring(1) : "not-found";
    }

    public String cateGoryReport(String username, String password, String catId, String libId) {
        authenticateManager(username, password, null, catId, libId);
        return libraries.get(libId).categoryReport(subCategoriesOf(catId));
    }

    public String libraryReport(String username, String password, String libId) {
        authenticateManager(username, password, null, null, libId);
        return libraries.get(libId).libraryReport();
    }

    public String passedDeadlinesReport(String username, String password, String libId, String dateStr, String timeStr) {
        authenticateManager(username, password, null, null, libId);

        StringBuilder out = new StringBuilder();
        libraries.get(libId).passedDeadlinesReport(dateTimeParser(dateStr, timeStr)).forEach(txt -> out.append("|").append(txt));
        return out.length() > 1 ? out.substring(1) : "none";
    }

    public String penaltiesReport(String username, String password) {
        authenticateAdmin(username, password);

        long penalty = 0;
        for (User user : users.values()) penalty += user.getPenalty();
        return penalty + "";
    }

    public String mostPopularReport(String username, String password, String libId) {
        authenticateManager(username, password, null, null, libId);
        return libraries.get(libId).mostPopularReport();
    }

    public String mostSoldReport(String username, String password, String libId) {
        authenticateManager(username, password, null, null, libId);
        return libraries.get(libId).mostSoldReport();
    }

    private void authenticateManager(String accessName, String accessWord, String id, String catId, String libId) {
        if (!libraries.containsKey(libId) || (catId != null && !categories.containsKey(catId)) || !users.containsKey(accessName))
            throw new NotFoundException();
        if (!users.get(accessName).getPassword().equals(accessWord))
            throw new InvalidPassException();
        if (!(users.get(accessName) instanceof Manager) || !((Manager) users.get(accessName)).isManagerOf(libId))
            throw new PermissionDeniedException();
        if (id != null && libraries.get(libId).resourceExists(id))
            throw new DuplicateIdException();
    }

    private void authenticateAdmin(String username, String password) {
        if (!users.containsKey(username)) throw new NotFoundException();
        if (!users.get(username).getPassword().equals(password)) throw new InvalidPassException();
        if (!(users.get(username) instanceof Admin)) throw new PermissionDeniedException();
    }

    private static LocalDateTime dateTimeParser(String date, String time) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("y-M-d-H:m");
        return LocalDateTime.parse(date + "-" + time, format);
    }

    private List<String> subCategoriesOf(String catId) {
        ArrayList<String> res = new ArrayList<>();
        res.add(catId);
        if (catId.equals("null")) return res;

        for (Category current : categories.values()) {
            Category tmp = current;
            while (tmp.getParent() != null) {
                if (catId.equals(tmp.getParent().getId())) {
                    res.add(current.getId());
                    break;
                }
                tmp = tmp.getParent();
            }
        }
        return res;
    }
}

