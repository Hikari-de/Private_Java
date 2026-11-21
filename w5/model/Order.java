package model;

import java.util.List;

public class Order {
    private String id;
    private Customer customer;
    private List<OrderItem> items;
    private OrderStatus status;
    private String paidVia;     // null cho tới khi đơn được trả

    public Order(String id, Customer customer, List<OrderItem> items) {
        this.id = id;
        this.customer = customer;
        this.items = items;
        this.status = OrderStatus.UNPAID;   // đơn mới luôn là chưa trả
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getPaidVia() {
        return paidVia;
    }

    public void setPaidVia(String paidVia) {
        this.paidVia = paidVia;
    }

    public double getTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public double getProfit() {
        double profit = 0;
        for (OrderItem item : items) {
            profit += item.getProfit();
        }
        return profit;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ").append(id).append(" | ").append(customer).append(" | ").append(status);
        if (paidVia != null) {
            sb.append(" (via ").append(paidVia).append(")");
        }
        for (OrderItem item : items) {
            sb.append("\n   - ").append(item);
        }
        sb.append("\n   Total: ").append(String.format("%,.0f", getTotal()));
        return sb.toString();
    }
}
