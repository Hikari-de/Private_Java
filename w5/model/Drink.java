package model;

import java.time.LocalDate;

public class Drink extends PerishableProduct {
    private int volumeMl;
    private boolean sugarFree;
    private boolean carbonated;
    private String flavor;       // để chuỗi rỗng nếu không có

    public Drink(String id, String name, double price, double costPrice, int stock,
                 int volumeMl, boolean sugarFree, boolean carbonated, String flavor,
                 LocalDate expiryDate) {
        super(id, name, price, costPrice, stock, expiryDate);
        this.volumeMl = volumeMl;
        this.sugarFree = sugarFree;
        this.carbonated = carbonated;
        this.flavor = flavor;
    }

    public int getVolumeMl() {
        return volumeMl;
    }

    public boolean isSugarFree() {
        return sugarFree;
    }

    public boolean isCarbonated() {
        return carbonated;
    }

    public String getFlavor() {
        return flavor;
    }

    @Override
    public boolean isExpensive() {
        return getPrice() > 30000;
    }

    @Override
    public ProductGroup getGroup() {
        return ProductGroup.DRINK;
    }

    @Override
    public String toString() {
        String text = super.toString() + " | " + volumeMl + "ml";
        if (!flavor.isEmpty()) {
            text += " | flavor: " + flavor;
        }
        if (sugarFree) {
            text += " | sugar-free";
        }
        if (carbonated) {
            text += " | carbonated";
        }
        return text;
    }
}
