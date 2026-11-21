package service;

import model.Order;

public class MoMoPaymentService implements PaymentService {
    @Override
    public String getName() {
        return "MoMo";
    }

    @Override
    public String pay(Order order) {
        return "Order " + order.getId() + " was paid successfully via MoMo ("
                + String.format("%,.0f", order.getTotal()) + "). Thank you for your purchase!";
    }
}
