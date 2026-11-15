package model;

public class Product {
    private final String id;
    private final String name;
    private double price;
    private int stock;

    public Product(String id, String name, double price, int stock) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product id must not be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name must not be empty.");
        }
        if (!(price > 0)) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock must not be negative.");
        }
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void setPrice(double price) {
        if (!(price > 0)) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }
        this.price = price;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0 || quantity > stock) {
            throw new IllegalArgumentException("Quantity must be between 1 and current stock.");
        }
        this.stock -= quantity;
    }

    public String getDescription() {
        return name;
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + price + " | remain: " + stock;
    }
}
