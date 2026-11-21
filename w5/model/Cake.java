package model;

import java.time.LocalDate;

public class Cake extends PerishableProduct {
    private String brand;
    private String flavor;
    private int calories;

    public Cake(String id, String name, double price, double costPrice, int stock,
                String brand, String flavor, int calories, LocalDate expiryDate) {
        super(id, name, price, costPrice, stock, expiryDate);
        this.brand = brand;
        this.flavor = flavor;
        this.calories = calories;
    }

    public String getBrand() {
        return brand;
    }

    public String getFlavor() {
        return flavor;
    }

    public int getCalories() {
        return calories;
    }

    @Override
    public boolean isExpensive() {
        return getPrice() > 100000;
    }

    @Override
    public ProductGroup getGroup() {
        return ProductGroup.CAKE;
    }

    @Override
    public String toString() {
        return super.toString() + " | brand: " + brand + " | flavor: " + flavor
                + " | calories: " + calories;
    }
}
