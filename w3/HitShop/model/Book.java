package model;

public class Book extends Product {
    private final String author;

    public Book(String id, String name, double price, int stock, String author) {
        super(id, name, price, stock);
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author must not be empty.");
        }
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String getDescription() {
        return getName() + " — tác giả: " + author;
    }
}
