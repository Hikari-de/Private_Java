package model;

public class Book extends Product {
    private String author;
    private Genre genre;
    private int publishYear;

    // Sách không có "name" riêng: tựa sách được lưu ở Product (các chỗ chung như giỏ hàng,
    // báo cáo vẫn gọi getName()), còn với sách thì gọi getTitle() cho đúng nghĩa.
    public Book(String id, String title, double price, double costPrice, int stock,
                String author, Genre genre, int publishYear) {
        super(id, title, price, costPrice, stock);
        this.author = author;
        this.genre = genre;
        this.publishYear = publishYear;
    }

    public String getTitle() {
        return getName();
    }

    public String getAuthor() {
        return author;
    }

    public Genre getGenre() {
        return genre;
    }

    public int getPublishYear() {
        return publishYear;
    }

    @Override
    public boolean isExpensive() {
        return getPrice() > 20000;
    }

    @Override
    public ProductGroup getGroup() {
        return ProductGroup.BOOK;
    }

    @Override
    public String toString() {
        return super.toString() + " | author: " + author + " | genre: " + genre
                + " | year: " + publishYear;
    }
}
