package model;

import java.util.ArrayList;
import java.util.List;

// Giỏ hàng của một người mua: nháp, đổi thoải mái, CHƯA trừ kho.
// Lỗi được báo bằng IllegalArgumentException; menu bắt và in e.getMessage().
public class Cart {
    private final List<OrderItem> items = new ArrayList<>();

    // Trả về bản sao để bên ngoài không tự ý sửa danh sách bên trong
    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    private OrderItem find(String productId) {
        for (OrderItem item : items) {
            if (item.getProduct().getId().equalsIgnoreCase(productId)) {
                return item;
            }
        }
        return null;
    }

    public void add(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }
        if (!product.isAvailable()) {
            throw new IllegalArgumentException(product.getName() + " is out of stock or expired.");
        }

        OrderItem existing = find(product.getId());
        int newQuantity = quantity + (existing == null ? 0 : existing.getQuantity());
        if (newQuantity > product.getStock()) {
            throw new IllegalArgumentException("Not enough stock. Available: " + product.getStock());
        }

        if (existing == null) {
            items.add(new OrderItem(product, quantity));
        } else {
            existing.setQuantity(newQuantity);   // thêm món đã có thì cộng dồn
        }
    }

    public void setQuantity(String productId, int quantity) {
        OrderItem item = find(productId);
        if (item == null) {
            throw new IllegalArgumentException("Item is not in the cart.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }
        if (quantity > item.getProduct().getStock()) {
            throw new IllegalArgumentException("Not enough stock. Available: "
                    + item.getProduct().getStock());
        }
        item.setQuantity(quantity);
    }

    public void remove(String productId) {
        OrderItem item = find(productId);
        if (item == null) {
            throw new IllegalArgumentException("Item is not in the cart.");
        }
        items.remove(item);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
}
