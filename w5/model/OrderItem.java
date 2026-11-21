package model;

// Một dòng trong giỏ hoặc trong đơn: "món này, số lượng này".
// Giá bán và giá vốn được CHỐT lúc tạo dòng, nên đổi giá sau này không làm sai doanh thu cũ.
public class OrderItem {
    private Product product;
    private int quantity;
    private double unitPrice;
    private double unitCost;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        this.unitCost = product.getCostPrice();
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    public double getProfit() {
        return (unitPrice - unitCost) * quantity;
    }

    @Override
    public String toString() {
        return product.getId() + " | " + product.getName() + " x" + quantity
                + " @ " + String.format("%,.0f", unitPrice)
                + " = " + String.format("%,.0f", getSubtotal());
    }
}
