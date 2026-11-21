package service;

import model.Order;

public interface PaymentService {
    String getName();

    // Trả về lời nhắn biên lai để menu in ra
    String pay(Order order);
}
