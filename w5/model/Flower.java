package model;

import java.time.LocalDate;

public class Flower extends PerishableProduct {
    private String origin;
    private String colour;

    public Flower(String id, String name, double price, double costPrice, int stock,
                  String origin, String colour, LocalDate expiryDate) {
        super(id, name, price, costPrice, stock, expiryDate);
        this.origin = origin;
        this.colour = colour;
    }

    public String getOrigin() {
        return origin;
    }

    public String getColour() {
        return colour;
    }

    @Override
    public boolean isExpensive() {
        return getPrice() > 50000;
    }

    @Override
    public ProductGroup getGroup() {
        return ProductGroup.FLOWER;
    }

    @Override
    public String toString() {
        return super.toString() + " | origin: " + origin + " | colour: " + colour;
    }
}
