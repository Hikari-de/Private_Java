package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

// Lớp chung cho hàng có hạn dùng (bánh, đồ uống, hoa).
// expiryDate và các hàm liên quan chỉ viết MỘT lần ở đây.
public abstract class PerishableProduct extends Product {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private LocalDate expiryDate;

    public PerishableProduct(String id, String name, double price, double costPrice, int stock,
                             LocalDate expiryDate) {
        super(id, name, price, costPrice, stock);
        this.expiryDate = expiryDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    // Số ngày còn lại; âm nghĩa là đã quá hạn
    public long daysLeft() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    // Hết hạn thì không bán được, dù còn kho
    @Override
    public boolean isAvailable() {
        return super.isAvailable() && !isExpired();
    }

    @Override
    public String toString() {
        return super.toString() + " | expiry: " + expiryDate.format(DATE_FORMAT);
    }
}
