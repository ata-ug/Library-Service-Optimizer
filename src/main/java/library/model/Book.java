package library.model;

/** Mirrors the `books` table. shelfLocationId links a book into the locations graph. */
public class Book {
    public int bookId;
    public String isbn;          // nullable
    public String title;
    public String author;
    public String category;      // used for indexing/search
    public Integer shelfLocationId; // nullable
    public int totalCopies;
    public int availableCopies;

    public Book() { }

    public Book(int bookId, String isbn, String title, String author, String category,
                Integer shelfLocationId, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.shelfLocationId = shelfLocationId;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    @Override
    public String toString() {
        return "Book{id=" + bookId + ", title='" + title + "', category='" + category + "'}";
    }
}
