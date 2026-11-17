import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Book;
import model.Cake;
import model.Customer;
import model.Order;
import model.Product;

public class HitShop {
    private static final Scanner sc = new Scanner(System.in);
    private static final List<Product> products = new ArrayList<>();
    private static final List<Order> orders = new ArrayList<>();

    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = sc.nextLine().trim();
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("This field must not be empty.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private static Product findProduct(String id) {
        for (Product p : products) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }

    public static void addOrder() {
        System.out.println("\n===== CUSTOMER INFO =====");
        String customerId = readText("Enter customer ID: ");
        String customerName = readText("Enter customer name: ");
        String phone = readText("Enter customer phone: ");
        Customer customer = new Customer(customerId, customerName, phone);

        System.out.println("\n=== Choose product type ===");
        System.out.println("1. Product - Regular product");
        System.out.println("2. Book - Book");
        System.out.println("3. Cake - Cake");
        int type = readInt("Enter your choice: ");

        if (type < 1 || type > 3) {
            System.out.println("Invalid product type.");
            return;
        }

        String id = readText("Enter product ID: ");
        if (findProduct(id) != null) {
            System.out.println("Product ID already exists.");
            return;
        }

        String name = readText("Enter product name: ");

        double price = readDouble("Enter product price: ");
        if (price <= 0) {
            System.out.println("Price must be greater than 0.");
            return;
        }

        int stock = readInt("Enter product stock: ");
        if (stock < 0) {
            System.out.println("Stock must not be negative.");
            return;
        }

        Product product;

        if (type == 1) {
            product = new Product(id, name, price, stock);
        } else if (type == 2) {
            String author = readText("Enter author: ");
            product = new Book(id, name, price, stock, author);
        } else {
            String brand = readText("Enter brand: ");
            product = new Cake(id, name, price, stock, brand);
        }

        int quantity = readInt("Enter quantity: ");
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return;
        }

        orders.add(new Order(customer, product, quantity));
        products.add(product);
        System.out.println("Order added successfully!");
    }

    public static void printOrders() {
        if (orders.isEmpty()) {
            System.out.println("\nNo orders yet.");
            return;
        }

        System.out.println("\n===== ORDER LIST =====");

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            System.out.println((i + 1) + ". Customer: " + order.getCustomer());
            System.out.println("   Product: " + order.getProduct());
            System.out.println("   Quantity: " + order.getQuantity()
                    + " | Total: " + order.getTotal());
        }
    }

    public static void updateOrder() {
        if (orders.isEmpty()) {
            System.out.println("\nNo orders yet.");
            return;
        }

        printOrders();
        int number = readInt("Enter order number to update: ");

        if (number < 1 || number > orders.size()) {
            System.out.println("Order not found.");
            return;
        }

        int quantity = readInt("Enter new quantity: ");
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return;
        }

        orders.get(number - 1).setQuantity(quantity);
        System.out.println("Order updated successfully!");
    }

    public static void deleteOrder() {
        if (orders.isEmpty()) {
            System.out.println("\nNo orders yet.");
            return;
        }

        printOrders();
        int number = readInt("Enter order number to delete: ");

        if (number < 1 || number > orders.size()) {
            System.out.println("Order not found.");
            return;
        }

        orders.remove(number - 1);
        System.out.println("Order deleted successfully!");
    }

    public static void checkExpensive() {
        String id = readText("Enter product ID: ");
        Product product = findProduct(id);

        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        if (product.isExpensive()) {
            System.out.println(product.getName() + " is expensive.");
        } else {
            System.out.println(product.getName() + " is not expensive.");
        }
    }

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        int choice;

        do {
            System.out.println("\n===== HIT SHOP =====");
            System.out.println("1. Add order");
            System.out.println("2. View orders");
            System.out.println("3. Update order");
            System.out.println("4. Delete order");
            System.out.println("5. Check if a product is expensive");
            System.out.println("0. Exit");
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    addOrder();
                    break;
                case 2:
                    printOrders();
                    break;
                case 3:
                    updateOrder();
                    break;
                case 4:
                    deleteOrder();
                    break;
                case 5:
                    checkExpensive();
                    break;
                case 0:
                    System.out.println("Thank you for using HIT SHOP!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);

        sc.close();
    }
}
