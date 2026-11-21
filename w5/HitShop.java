import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import model.Book;
import model.Cake;
import model.Drink;
import model.Flower;
import model.GeneralProduct;
import model.Genre;
import service.OrderService;
import service.OrderServiceImpl;
import service.ProductService;
import service.ProductServiceImpl;
import service.ReportService;

public class HitShop {

    // Dữ liệu mẫu để chạy thử ngay (dữ liệu chỉ nằm trong bộ nhớ, tắt chương trình là mất)
    private static void seedData(ProductService productService) {
        LocalDate today = LocalDate.now();

        productService.addProduct(new GeneralProduct("G01", "Notebook", 15000, 9000, 50));
        productService.addProduct(new Book("B01", "Dune", 90000, 60000, 12,
                "Frank Herbert", Genre.SCIFI, 1965));
        productService.addProduct(new Book("B02", "Murder on the Orient Express", 80000, 50000, 8,
                "Agatha Christie", Genre.DETECTIVE, 1934));
        productService.addProduct(new Cake("C01", "Strawberry Cupcake", 50000, 30000, 20,
                "HIT Bakery", "Strawberry", 320, today.plusDays(4)));
        productService.addProduct(new Cake("C02", "Old Tiramisu", 70000, 40000, 5,
                "HIT Bakery", "Coffee", 450, today.minusDays(1)));   // đã hết hạn: khách sẽ không thấy
        productService.addProduct(new Drink("D01", "Coca Cola", 15000, 10000, 100,
                330, false, true, "", today.plusDays(120)));
        productService.addProduct(new Drink("D02", "Peach Tea", 25000, 15000, 60,
                500, true, false, "Peach", today.plusDays(90)));
        productService.addProduct(new Flower("F01", "Red Rose Bouquet", 120000, 70000, 15,
                "Da Lat", "Red", today.plusDays(3)));
    }

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        // Hai vai trò DÙNG CHUNG đúng một bộ service nên kho và đơn luôn khớp nhau
        ProductService productService = new ProductServiceImpl();
        OrderService orderService = new OrderServiceImpl();
        ReportService reportService = new ReportService(productService, orderService);
        seedData(productService);

        AdminMenu adminMenu = new AdminMenu(productService, orderService, reportService);
        CustomerMenu customerMenu = new CustomerMenu(productService, orderService);

        int choice;
        do {
            System.out.println("\n===== HITSHOP =====");
            System.out.println("1. Admin");
            System.out.println("2. Customer");
            System.out.println("0. Exit");
            choice = Input.readInt("Your choice: ");

            switch (choice) {
                case 1:
                    adminMenu.run();
                    break;
                case 2:
                    customerMenu.run();
                    break;
                case 0:
                    System.out.println("Thank you for using HIT SHOP!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }
}
