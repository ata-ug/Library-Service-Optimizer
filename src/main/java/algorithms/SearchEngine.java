import java.util.List;


public class SearchEngine {

    public static class Book {
        int bookId;
        String isbn;
        String title;

        public Book(int bookId, String isbn, String title) {
            this.bookId = bookId;
            this.isbn = isbn;
            this.title = title;
        }

        @Override
        public String toString() {
            return "Book{id=" + bookId + ", isbn='" + isbn + "', title='" + title + "'}";
        }
    }

    public static class ServiceRequest {
        int requestId;
        String status;

        public ServiceRequest(int requestId, String status) {
            this.requestId = requestId;
            this.status = status;
        }

        @Override
        public String toString() {
            return "Req{id=" + requestId + ", status='" + status + "'}";
        }
    }

    public static class UnsortedDataException extends RuntimeException {
        public UnsortedDataException(String message) {
            super(message);
        }
    }

    public static int linearSearchByRequestId(List<ServiceRequest> requests, int targetId) {
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).requestId == targetId) {
                return i;
            }
        }
        return -1;
    }

    public static int linearSearchByTitle(List<Book> books, String targetTitle) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).title.equals(targetTitle)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isSortedByTitle(List<Book> books) {
        for (int i = 1; i < books.size(); i++) {
            if (books.get(i - 1).title.compareTo(books.get(i).title) > 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSortedByIsbn(List<Book> books) {
        for (int i = 1; i < books.size(); i++) {
            if (books.get(i - 1).isbn.compareTo(books.get(i).isbn) > 0) {
                return false;
            }
        }
        return true;
    }

    public static int binarySearchByTitle(List<Book> books, String targetTitle) {
        if (!isSortedByTitle(books)) {
            throw new UnsortedDataException(
                "books list is not sorted by title - binary search cannot run safely"
            );
        }
        return binarySearchByTitleUnchecked(books, targetTitle);
    }

    public static int binarySearchByIsbn(List<Book> books, String targetIsbn) {
        if (!isSortedByIsbn(books)) {
            throw new UnsortedDataException(
                "books list is not sorted by isbn - binary search cannot run safely"
            );
        }
        int low = 0, high = books.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = books.get(mid).isbn.compareTo(targetIsbn);
            if (cmp == 0) return mid;
            else if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    static int binarySearchByTitleUnchecked(List<Book> books, String targetTitle) {
        int low = 0, high = books.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = books.get(mid).title.compareTo(targetTitle);
            if (cmp == 0) return mid;
            else if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        List<Book> sorted = List.of(
                new Book(1, "111", "Algorithms"),
                new Book(2, "222", "Chemistry"),
                new Book(3, "333", "Databases"),
                new Book(4, "444", "Networks")
        );
        System.out.println("Sorted check: " + isSortedByTitle(sorted));
        int idx = binarySearchByTitle(sorted, "Databases");
        System.out.println("binarySearchByTitle('Databases') = " + idx + " (expected 2)\n");

        List<Book> unsorted = List.of(
                new Book(1, "444", "Networks"),
                new Book(2, "111", "Zoology"),
                new Book(3, "333", "Algorithms"),
                new Book(4, "222", "Chemistry")
        );
        System.out.println("Sorted check: " + isSortedByTitle(unsorted));
        try {
            binarySearchByTitle(unsorted, "Algorithms");
        } catch (UnsortedDataException e) {
            System.out.println("Caught expected exception: " + e.getMessage() + "\n");
        }

        int wrongResult = binarySearchByTitleUnchecked(unsorted, "Algorithms");
        System.out.println("Unchecked result = " + wrongResult + " (actual index is 2 - mismatch proves the failure)");
        System.out.println("Correct index via linear search: " + linearSearchByTitle(unsorted, "Algorithms"));
    }
}
