package service;

import java.util.List;

import model.Cart;
import model.Customer;
import model.Order;
import model.OrderStatus;

// Mọi lỗi nghiệp vụ được báo bằng IllegalArgumentException (message là câu thông báo)
public interface OrderService {
    // Biến giỏ hàng thành đơn: kiểm tra lại kho, trừ kho, chốt giá
    Order checkout(Customer customer, Cart cart);

    List<Order> getAll();

    List<Order> getByStatus(OrderStatus status);

    List<Order> getByPhone(String phone);

    Order findById(String id);

    // Chỉ trả về đơn nếu đúng của số điện thoại đó, ngược lại trả null
    Order findByIdAndPhone(String id, String phone);

    // Khách trả qua cổng thanh toán; trả về lời nhắn biên lai
    String payOrder(String id, PaymentService paymentService);

    // Quản lý xác nhận đã nhận tiền tại quầy
    void confirmPaid(String id);

    // Chỉ hủy được đơn chưa trả; hủy thì trả hàng về kho
    void cancelOrder(String id);
}
