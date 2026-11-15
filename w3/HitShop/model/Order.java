package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String id;
    private final Customer customer;
    private final List<CartItem> items;
    private String status;

    public Order(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<>();
        this.status = "PENDING";
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public String getStatus() {
        return status;
    }

    public void addItem(CartItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null.");
        }
        items.add(item);
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotal();
        }
        return total;
    }

    public void confirm() {
        if ("PENDING".equals(status)) {
            status = "CONFIRMED";
        }
    }

    public void cancel() {
        if ("PENDING".equals(status)) {
            status = "CANCELLED";
        }
    }
}
