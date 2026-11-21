package service;

import java.util.List;

import model.Product;
import model.ProductGroup;

public interface ProductService {
    // Ném IllegalArgumentException nếu mã đã tồn tại
    void addProduct(Product product);

    // Ném IllegalArgumentException nếu không tìm thấy
    void removeProduct(String id);

    // Trả null nếu không tìm thấy
    Product findById(String id);

    List<Product> getAll();

    // Chỉ hàng bán được (còn kho, chưa hết hạn). group = null nghĩa là mọi nhóm.
    List<Product> getAvailable(ProductGroup group);
}
