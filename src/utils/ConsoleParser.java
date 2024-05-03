package utils;

import java.util.Scanner;

public class ConsoleParser {
    private static ConsoleParser singleton = null; // singleton object

    private ConsoleParser() {}

    public static ConsoleParser getSingleton() {
        if (singleton == null) singleton = new ConsoleParser();
        return singleton;
    }

    public void run() {
        LibraryManagement libManagement = LibraryManagement.getSingleton();
        Scanner scan = new Scanner(System.in);
        String buffer = scan.nextLine();


        while (!buffer.equals("finish")) {
            String command = buffer.trim().split("#")[0];
            String[] arguments = buffer.trim().split("#")[1].split("\\|");
            if (buffer.trim().isEmpty()) continue;

            try {
                System.out.println(commandParser(libManagement, command, arguments));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            do {
                buffer = scan.nextLine();
            } while (buffer.trim().isEmpty());
        }
    }

    private String commandParser(LibraryManagement libManagement, String command, String[] arguments) {
        switch (getCommandWord(command)) {
            case CommandWord.add_library -> libManagement.addLibrary(arguments[0], arguments[1], arguments[2], arguments[3],
                    Integer.parseInt(arguments[4]), Integer.parseInt(arguments[5]), arguments[6]);
            case CommandWord.add_category ->
                    libManagement.addCategory(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
            case CommandWord.add_student ->
                    libManagement.addUser(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4],
                            arguments[5], arguments[6], Integer.parseInt(arguments[7]), arguments[8], 'l');
            case CommandWord.add_staff ->
                    libManagement.addUser(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5],
                            arguments[6], Integer.parseInt(arguments[7]), arguments[8], arguments[9].charAt(0));
            case CommandWord.add_manager ->
                    libManagement.addManager(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5],
                            arguments[6], Integer.parseInt(arguments[7]), arguments[8], arguments[9]);
            case CommandWord.remove_user -> libManagement.removeUser(arguments[0], arguments[1], arguments[2]);
            case CommandWord.add_book ->
                    libManagement.addBook(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5],
                            Integer.parseInt(arguments[6]), Integer.parseInt(arguments[7]), arguments[8], arguments[9]);
            case CommandWord.add_thesis ->
                    libManagement.addThesis(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5],
                            Integer.parseInt(arguments[6]), arguments[7], arguments[8]);
            case CommandWord.add_rareBook ->
                    libManagement.addRareBook(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5],
                            Integer.parseInt(arguments[6]), arguments[7], arguments[8], arguments[9]);
            case CommandWord.add_selling_book ->
                    libManagement.addSellingBook(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5],
                            Integer.parseInt(arguments[6]), Integer.parseInt(arguments[7]), Integer.parseInt(arguments[8]),
                            Integer.parseInt(arguments[9]), arguments[10], arguments[11]);
            case CommandWord.remove_resource ->
                    libManagement.removeResource(arguments[0], arguments[1], arguments[2], arguments[3]);
            case CommandWord.borrow ->
                    libManagement.borrowResource(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
            case CommandWord.buy -> libManagement.buy(arguments[0], arguments[1], arguments[2], arguments[3]);
            case CommandWord.read ->
                    libManagement.read(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
            case CommandWord.add_comment ->
                    libManagement.addComment(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
            case CommandWord.return_resource -> {
                return libManagement.returnResource(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
            }
            case CommandWord.search -> {
                return libManagement.searchResources(arguments[0]);
            }
            case CommandWord.search_user -> {
                return libManagement.searchUsers(arguments[0], arguments[1], arguments[2]);
            }
            case CommandWord.category_report -> {
                return libManagement.cateGoryReport(arguments[0], arguments[1], arguments[2], arguments[3]);
            }
            case CommandWord.library_report -> {
                return libManagement.libraryReport(arguments[0], arguments[1], arguments[2]);
            }
            case CommandWord.report_passed_deadline -> {
                return libManagement.passedDeadlinesReport(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
            }
            case CommandWord.report_penalties_sum -> {
                return libManagement.penaltiesReport(arguments[0], arguments[1]);
            }
            case CommandWord.report_most_popular -> {
                return libManagement.mostPopularReport(arguments[0], arguments[1], arguments[2]);
            }
            case CommandWord.report_sell -> {
                return libManagement.mostSoldReport(arguments[0], arguments[1], arguments[2]);
            }
            case null ->
                    throw new IllegalArgumentException("Invalid command: " + command);
        }
        return "success";
    }

    private enum CommandWord {
        add_library("add-library"),
        add_category("add-category"),
        add_student("add-student"),
        add_staff("add-staff"),
        add_manager("add-manager"),
        remove_user("remove-user"),
        add_book("add-book"),
        add_thesis("add-thesis"),
        add_selling_book("add-selling-book"),
        remove_resource("remove-resource"),
        borrow("borrow"),
        buy("buy"),
        add_rareBook("add-ganjineh-book"),
        read("read"),
        add_comment("add-comment"),
        return_resource("return"),
        search("search"),
        search_user("search-user"),
        category_report("category-report"),
        library_report("library-report"),
        report_passed_deadline("report-passed-deadline"),
        report_penalties_sum("report-penalties-sum"),
        report_most_popular("report-most-popular"),
        report_sell("report-sell");

        public final String val;

        CommandWord(String val) {
            this.val = val;
        }
    }

    private CommandWord getCommandWord(String Command){
        for (CommandWord tmp : CommandWord.values()) {
            if (tmp.val.equals(Command)) return tmp;
        }
        return null;
    }
}
