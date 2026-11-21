package model;

public class GeneralProduct extends Product {
    public GeneralProduct(String id, String name, double price, double costPrice, int stock) {
        super(id, name, price, costPrice, stock);
    }

    @Override
    public boolean isExpensive() {
        return getPrice() > 10000;
    }

    @Override
    public ProductGroup getGroup() {
        return ProductGroup.GENERAL;
    }
}
