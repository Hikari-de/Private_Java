package model;

public abstract class Product {
    private String id;
    private String name;
    private double price;       // giá bán
    private double costPrice;   // giá vốn: chỉ quản lý được xem
    private int stock;

    public Product(String id, String name, double price, double costPrice, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.costPrice = costPrice;
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

    public double getCostPrice() {
        return costPrice;
    }

    public int getStock() {
        return stock;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // Mỗi nhóm hàng tự quyết định: ngưỡng "đắt" và mình thuộc nhóm nào
    public abstract boolean isExpensive();

    public abstract ProductGroup getGroup();

    // Hàng có thể bán được không? Mặc định: còn kho.
    // Hàng tươi (PerishableProduct) sẽ override thêm điều kiện "chưa hết hạn".
    public boolean isAvailable() {
        return stock > 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Product)) {
            return false;
        }
        Product other = (Product) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    // Cố ý KHÔNG in giá vốn: khách cũng dùng toString này
    @Override
    public String toString() {
        return id + " | " + name + " | " + String.format("%,.0f", price) + " | remain: " + stock;
    }
}
