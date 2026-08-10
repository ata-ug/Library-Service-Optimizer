public class Book {
    private int bookId;
    private String isbn;
    private String title;
    private String author;
    private String category;
    private int shelfLocationId;
    private int totalCopies;
    private int availableCopies;

    public Book(int bookId, String isbn, String title, String author, String category, int shelfLocationId, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.shelfLocationId = shelfLocationId;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public int getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public int getShelfLocationId() { return shelfLocationId; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }

    @Override
    public String toString() {
        return "Book{id=" + bookId + ", title='" + title + "', author='" + author + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return bookId == book.bookId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(bookId);
    }
}
