package service;

import java.util.ArrayList;
import java.util.List;

import model.Cart;
import model.Customer;
import model.Order;
import model.OrderItem;
import model.OrderStatus;
import model.Product;

public class OrderServiceImpl implements OrderService {
    private final List<Order> orders = new ArrayList<>();
    private int nextId = 1;     // mã đơn tự tăng: ORD1, ORD2, ...

    @Override
    public Order checkout(Customer customer, Cart cart) {
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty.");
        }

        // Bước 1: kiểm tra TẤT CẢ trước. Giỏ có thể giữ lâu, hàng có thể đã hết hoặc hết hạn.
        for (OrderItem item : cart.getItems()) {
            Product p = item.getProduct();
            if (!p.isAvailable()) {
                throw new IllegalArgumentException(p.getName() + " is no longer available.");
            }
            if (item.getQuantity() > p.getStock()) {
                throw new IllegalArgumentException("Not enough stock for " + p.getName()
                        + ". Available: " + p.getStock());
            }
        }

        // Bước 2: mọi thứ hợp lệ rồi mới trừ kho, và tạo dòng mới để chốt giá hiện tại
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItem item : cart.getItems()) {
            Product p = item.getProduct();
            p.setStock(p.getStock() - item.getQuantity());
            orderItems.add(new OrderItem(p, item.getQuantity()));
        }

        Order order = new Order("ORD" + nextId++, customer, orderItems);
        orders.add(order);
        return order;
    }

    @Override
    public List<Order> getAll() {
        return new ArrayList<>(orders);
    }

    @Override
    public List<Order> getByStatus(OrderStatus status) {
        List<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (o.getStatus() == status) {
                result.add(o);
            }
        }
        return result;
    }

    @Override
    public List<Order> getByPhone(String phone) {
        List<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (o.getCustomer().getPhone().equals(phone)) {
                result.add(o);
            }
        }
        return result;
    }

    @Override
    public Order findById(String id) {
        for (Order o : orders) {
            if (o.getId().equalsIgnoreCase(id)) {
                return o;
            }
        }
        return null;
    }

    @Override
    public Order findByIdAndPhone(String id, String phone) {
        Order order = findById(id);
        if (order != null && order.getCustomer().getPhone().equals(phone)) {
            return order;
        }
        return null;
    }

    // Lấy đơn và bắt buộc phải đang ở trạng thái chưa trả
    private Order getUnpaidOrder(String id) {
        Order order = findById(id);
        if (order == null) {
            throw new IllegalArgumentException("Order not found.");
        }
        if (order.getStatus() != OrderStatus.UNPAID) {
            throw new IllegalArgumentException("Order " + order.getId() + " is already "
                    + order.getStatus() + ".");
        }
        return order;
    }

    @Override
    public String payOrder(String id, PaymentService paymentService) {
        Order order = getUnpaidOrder(id);
        String receipt = paymentService.pay(order);
        order.setStatus(OrderStatus.PAID);
        order.setPaidVia(paymentService.getName());
        return receipt;
    }

    @Override
    public void confirmPaid(String id) {
        Order order = getUnpaidOrder(id);
        order.setStatus(OrderStatus.PAID);
        order.setPaidVia("Counter");
    }

    @Override
    public void cancelOrder(String id) {
        Order order = getUnpaidOrder(id);
        for (OrderItem item : order.getItems()) {
            Product p = item.getProduct();
            p.setStock(p.getStock() + item.getQuantity());   // trả hàng về kho
        }
        order.setStatus(OrderStatus.CANCELLED);
    }
}
