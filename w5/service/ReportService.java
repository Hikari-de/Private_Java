package service;

import java.util.ArrayList;
import java.util.List;

import model.Order;
import model.OrderItem;
import model.OrderStatus;
import model.PerishableProduct;
import model.Product;

// Chỉ tính toán và trả số liệu, không in gì. AdminMenu lo phần hiển thị.
public class ReportService {
    private final ProductService productService;
    private final OrderService orderService;

    public ReportService(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    // Doanh thu: chỉ tính đơn ĐÃ TRẢ
    public double getRevenue() {
        double total = 0;
        for (Order o : orderService.getByStatus(OrderStatus.PAID)) {
            total += o.getTotal();
        }
        return total;
    }

    // Lãi gộp = giá bán - giá vốn, của các đơn đã trả
    public double getProfit() {
        double total = 0;
        for (Order o : orderService.getByStatus(OrderStatus.PAID)) {
            total += o.getProfit();
        }
        return total;
    }

    // Tiền đang treo ở các đơn chưa trả
    public double getUnpaidTotal() {
        double total = 0;
        for (Order o : orderService.getByStatus(OrderStatus.UNPAID)) {
            total += o.getTotal();
        }
        return total;
    }

    // Số lượng đã bán của một món (không tính đơn đã hủy)
    public int getSoldQuantity(Product product) {
        int sold = 0;
        for (Order o : orderService.getAll()) {
            if (o.getStatus() == OrderStatus.CANCELLED) {
                continue;
            }
            for (OrderItem item : o.getItems()) {
                if (item.getProduct().equals(product)) {
                    sold += item.getQuantity();
                }
            }
        }
        return sold;
    }

    // Món bán chạy nhất; null nếu chưa bán được gì
    public Product getBestSeller() {
        Product best = null;
        int bestSold = 0;
        for (Product p : productService.getAll()) {
            int sold = getSoldQuantity(p);
            if (sold > bestSold) {
                best = p;
                bestSold = sold;
            }
        }
        return best;
    }

    // Hàng có kho <= ngưỡng
    public List<Product> getLowStock(int threshold) {
        List<Product> result = new ArrayList<>();
        for (Product p : productService.getAll()) {
            if (p.getStock() <= threshold) {
                result.add(p);
            }
        }
        return result;
    }

    // Hàng tươi còn <= số ngày cho trước (bao gồm cả hàng đã quá hạn)
    public List<PerishableProduct> getExpiringSoon(int days) {
        List<PerishableProduct> result = new ArrayList<>();
        for (Product p : productService.getAll()) {
            if (p instanceof PerishableProduct perishable && perishable.daysLeft() <= days) {
                result.add(perishable);
            }
        }
        return result;
    }
}
