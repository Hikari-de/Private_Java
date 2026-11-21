package model;

// Vòng đời đơn: UNPAID -> PAID, hoặc UNPAID -> CANCELLED
public enum OrderStatus {
    UNPAID, PAID, CANCELLED
}
