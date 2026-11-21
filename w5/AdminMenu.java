import java.time.LocalDate;
import java.util.List;

import model.Book;
import model.Cake;
import model.Drink;
import model.Flower;
import model.GeneralProduct;
import model.Genre;
import model.Order;
import model.OrderStatus;
import model.PerishableProduct;
import model.Product;
import model.ProductGroup;
import service.OrderService;
import service.ProductService;
import service.ReportService;

public class AdminMenu {
    private static final String PASSWORD = "admin123";
    private static final int LOW_STOCK = 5;          // kho <= 5 thì cảnh báo
    private static final int EXPIRY_WARN_DAYS = 3;   // còn <= 3 ngày thì cảnh báo

    private final ProductService productService;
    private final OrderService orderService;
    private final ReportService reportService;

    public AdminMenu(ProductService productService, OrderService orderService,
                     ReportService reportService) {
        this.productService = productService;
        this.orderService = orderService;
        this.reportService = reportService;
    }

    private boolean login() {
        for (int attempt = 1; attempt <= 3; attempt++) {
            if (Input.readText("Enter admin password: ").equals(PASSWORD)) {
                return true;
            }
            System.out.println("Wrong password (" + attempt + "/3).");
        }
        return false;
    }

    public void run() {
        if (!login()) {
            System.out.println("Too many failed attempts.");
            return;
        }

        int choice;
        do {
            System.out.println("\n===== HITSHOP - Admin =====");
            System.out.println("1. Add product");
            System.out.println("2. View inventory");
            System.out.println("3. Update / remove product");
            System.out.println("4. View orders");
            System.out.println("5. Confirm payment");
            System.out.println("6. Cancel order");
            System.out.println("7. Check expiry");
            System.out.println("8. Reports");
            System.out.println("0. Back");
            choice = Input.readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    viewInventory();
                    break;
                case 3:
                    updateProduct();
                    break;
                case 4:
                    viewOrders();
                    break;
                case 5:
                    confirmPayment();
                    break;
                case 6:
                    cancelOrder();
                    break;
                case 7:
                    checkExpiry();
                    break;
                case 8:
                    showReports();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    // ---------- 1. Add product ----------

    private void addProduct() {
        int g = Input.readOption("Product group", ProductGroup.values());
        if (g == -1) {
            return;
        }
        ProductGroup group = ProductGroup.values()[g];

        String id = Input.readText("Enter product ID: ");
        if (productService.findById(id) != null) {
            System.out.println("Product ID already exists.");
            return;
        }
        String name = Input.readText(group == ProductGroup.BOOK
                ? "Enter title: " : "Enter product name: ");

        double price = Input.readDouble("Enter selling price: ");
        if (price <= 0) {
            System.out.println("Price must be greater than 0.");
            return;
        }
        double cost = Input.readDouble("Enter cost price: ");
        if (cost < 0) {
            System.out.println("Cost price must not be negative.");
            return;
        }
        int stock = Input.readInt("Enter stock: ");
        if (stock < 0) {
            System.out.println("Stock must not be negative.");
            return;
        }

        Product product;
        switch (group) {
            case BOOK:
                product = createBook(id, name, price, cost, stock);
                break;
            case CAKE:
                product = createCake(id, name, price, cost, stock);
                break;
            case DRINK:
                product = createDrink(id, name, price, cost, stock);
                break;
            case FLOWER:
                product = createFlower(id, name, price, cost, stock);
                break;
            default:
                product = new GeneralProduct(id, name, price, cost, stock);
        }
        if (product == null) {
            return;
        }

        try {
            productService.addProduct(product);
            System.out.println("Product added successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private Product createBook(String id, String title, double price, double cost, int stock) {
        int g = Input.readOption("Genre", Genre.values());
        if (g == -1) {
            return null;
        }
        String author = Input.readText("Enter author: ");
        int year = Input.readInt("Enter publish year: ");
        return new Book(id, title, price, cost, stock, author, Genre.values()[g], year);
    }

    private Product createCake(String id, String name, double price, double cost, int stock) {
        String brand = Input.readText("Enter brand: ");
        String flavor = Input.readText("Enter flavor: ");
        int calories = Input.readInt("Enter calories: ");
        LocalDate expiry = Input.readDate("Enter expiry date (dd/MM/yyyy): ");
        return new Cake(id, name, price, cost, stock, brand, flavor, calories, expiry);
    }

    private Product createDrink(String id, String name, double price, double cost, int stock) {
        int ml = Input.readInt("Enter volume (ml): ");
        boolean sugarFree = Input.readYesNo("Sugar-free?");
        boolean carbonated = Input.readYesNo("Carbonated?");
        String flavor = Input.readOptionalText("Enter flavor (Enter to skip): ");
        LocalDate expiry = Input.readDate("Enter expiry date (dd/MM/yyyy): ");
        return new Drink(id, name, price, cost, stock, ml, sugarFree, carbonated, flavor, expiry);
    }

    private Product createFlower(String id, String name, double price, double cost, int stock) {
        String origin = Input.readText("Enter origin: ");
        String colour = Input.readText("Enter colour: ");
        LocalDate expiry = Input.readDate("Enter expiry date (dd/MM/yyyy): ");
        return new Flower(id, name, price, cost, stock, origin, colour, expiry);
    }

    // ---------- 2. View inventory ----------

    private void viewInventory() {
        List<Product> all = productService.getAll();
        if (all.isEmpty()) {
            System.out.println("\nNo products yet.");
            return;
        }

        System.out.println("\n===== INVENTORY =====");
        for (int i = 0; i < all.size(); i++) {
            Product p = all.get(i);
            String flags = "";
            if (p.getStock() <= LOW_STOCK) {
                flags += " [LOW STOCK]";
            }
            if (p instanceof PerishableProduct perishable) {
                if (perishable.isExpired()) {
                    flags += " [EXPIRED]";
                } else if (perishable.daysLeft() <= EXPIRY_WARN_DAYS) {
                    flags += " [EXPIRING SOON]";
                }
            }
            System.out.println((i + 1) + ". [" + p.getGroup() + "] " + p
                    + " | cost: " + String.format("%,.0f", p.getCostPrice()) + flags);
        }
    }

    // ---------- 3. Update / remove product ----------

    private void updateProduct() {
        viewInventory();
        Product product = productService.findById(Input.readText("Enter product ID: "));
        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.println("\n1. Change selling price");
        System.out.println("2. Add stock (restock)");
        System.out.println("3. Remove product");
        int choice = Input.readInt("Enter your choice: ");

        switch (choice) {
            case 1:
                double price = Input.readDouble("Enter new price: ");
                if (price <= 0) {
                    System.out.println("Price must be greater than 0.");
                    return;
                }
                product.setPrice(price);
                System.out.println("Price updated.");
                break;
            case 2:
                int amount = Input.readInt("Enter amount to add: ");
                if (amount <= 0) {
                    System.out.println("Amount must be greater than 0.");
                    return;
                }
                product.setStock(product.getStock() + amount);
                System.out.println("Stock updated. Now: " + product.getStock());
                break;
            case 3:
                try {
                    productService.removeProduct(product.getId());
                    System.out.println("Product removed.");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // ---------- 4. View orders ----------

    private void viewOrders() {
        // "All" đứng đầu mảng; các trạng thái theo sau, nên chỉ số 0 = tất cả
        Object[] options = {"All", OrderStatus.UNPAID, OrderStatus.PAID, OrderStatus.CANCELLED};
        int c = Input.readOption("Filter orders", options);
        if (c == -1) {
            return;
        }

        List<Order> orders = (c == 0) ? orderService.getAll()
                : orderService.getByStatus((OrderStatus) options[c]);
        printOrders(orders);
    }

    private void printOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("\nNo orders.");
            return;
        }
        System.out.println("\n===== ORDER LIST =====");
        for (Order o : orders) {
            System.out.println(o);
        }
    }

    // ---------- 5 & 6. Confirm payment / Cancel order ----------

    private void confirmPayment() {
        printOrders(orderService.getByStatus(OrderStatus.UNPAID));
        String id = Input.readText("Enter order ID to confirm payment: ");
        try {
            orderService.confirmPaid(id);
            System.out.println("Payment confirmed.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void cancelOrder() {
        printOrders(orderService.getByStatus(OrderStatus.UNPAID));
        String id = Input.readText("Enter order ID to cancel: ");
        try {
            orderService.cancelOrder(id);
            System.out.println("Order cancelled. Stock returned.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // ---------- 7. Check expiry ----------

    private void checkExpiry() {
        System.out.println("\n1. Check one product");
        System.out.println("2. Scan all perishable products");
        int choice = Input.readInt("Enter your choice: ");

        if (choice == 1) {
            Product product = productService.findById(Input.readText("Enter product ID: "));
            if (product == null) {
                System.out.println("Product not found.");
            } else if (product instanceof PerishableProduct p) {
                if (p.isExpired()) {
                    System.out.println(p.getName() + " expired " + (-p.daysLeft()) + " day(s) ago.");
                } else {
                    System.out.println(p.getName() + " is valid, " + p.daysLeft() + " day(s) left.");
                }
            } else {
                System.out.println(product.getName() + " has no expiry date.");
            }
        } else if (choice == 2) {
            printExpiring();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void printExpiring() {
        List<PerishableProduct> list = reportService.getExpiringSoon(EXPIRY_WARN_DAYS);
        if (list.isEmpty()) {
            System.out.println("No product is expired or expiring within " + EXPIRY_WARN_DAYS + " days.");
            return;
        }
        for (PerishableProduct p : list) {
            String state = p.isExpired() ? "EXPIRED " + (-p.daysLeft()) + " day(s) ago"
                    : p.daysLeft() + " day(s) left";
            System.out.println("- " + p.getId() + " | " + p.getName() + " | stock " + p.getStock()
                    + " | " + state);
        }
    }

    // ---------- 8. Reports ----------

    private void showReports() {
        System.out.println("\n===== REPORT =====");
        System.out.println("Revenue (paid orders): " + String.format("%,.0f", reportService.getRevenue()));
        System.out.println("Gross profit:          " + String.format("%,.0f", reportService.getProfit()));
        System.out.println("Unpaid (pending):      " + String.format("%,.0f", reportService.getUnpaidTotal()));

        Product best = reportService.getBestSeller();
        System.out.println("Best seller: " + (best == null ? "none yet"
                : best.getName() + " (" + reportService.getSoldQuantity(best) + " sold)"));

        System.out.println("\n--- Sold per product ---");
        for (Product p : productService.getAll()) {
            System.out.println(p.getId() + " | " + p.getName() + ": " + reportService.getSoldQuantity(p));
        }

        System.out.println("\n--- Low stock (<= " + LOW_STOCK + ") ---");
        List<Product> low = reportService.getLowStock(LOW_STOCK);
        if (low.isEmpty()) {
            System.out.println("None.");
        }
        for (Product p : low) {
            System.out.println(p.getId() + " | " + p.getName() + " | stock " + p.getStock());
        }

        System.out.println("\n--- Expired / expiring within " + EXPIRY_WARN_DAYS + " days ---");
        printExpiring();
    }
}
