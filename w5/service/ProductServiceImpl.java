package service;

import java.util.ArrayList;
import java.util.List;

import model.Product;
import model.ProductGroup;

public class ProductServiceImpl implements ProductService {
    private final List<Product> products = new ArrayList<>();

    @Override
    public void addProduct(Product product) {
        if (findById(product.getId()) != null) {
            throw new IllegalArgumentException("Product ID already exists.");
        }
        products.add(product);
    }

    @Override
    public void removeProduct(String id) {
        Product product = findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found.");
        }
        products.remove(product);
    }

    @Override
    public Product findById(String id) {
        for (Product p : products) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public List<Product> getAll() {
        return new ArrayList<>(products);
    }

    @Override
    public List<Product> getAvailable(ProductGroup group) {
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            boolean rightGroup = group == null || p.getGroup() == group;
            if (rightGroup && p.isAvailable()) {
                result.add(p);
            }
        }
        return result;
    }
}
