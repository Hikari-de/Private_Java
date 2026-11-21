package service;

import model.Order;

public class BankPaymentService implements PaymentService {
    @Override
    public String getName() {
        return "HITBank";
    }

    @Override
    public String pay(Order order) {
        return "Order " + order.getId() + " was paid successfully via HITBank ("
                + String.format("%,.0f", order.getTotal()) + "). Thank you for your purchase!";
    }
}
