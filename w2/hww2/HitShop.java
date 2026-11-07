import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import model.Product;
import model.Customer;
import model.CartItem;

public class HitShop {
    public HitShop() {}
    private static final Customer currentCustomer =
            new Customer("C01", "Linh Nguyen", "0900000001");

    private static final List<CartItem> cart = new ArrayList<>();

    private static final Product p1 =
            new Product("P01", "T-shirt", 150000, 20);

    private static final Product p2 =
            new Product("P02", "Water Bottle", 50000, 10);

    public static void addItem(Product product, int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return;
        }

        cart.add(new CartItem(product, quantity));
        System.out.println("Product added successfully!");
    }

    public static void removeItem(int index) {
        if (index < 0 || index >= cart.size()) {
            System.out.println("Item not found.");
            return;
        }

        cart.remove(index);
        System.out.println("Item removed successfully!");
    }

    public static void printItems() {
        System.out.println("\nCustomer: " + currentCustomer);

        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        System.out.println("\n===== SHOPPING CART =====");

        for (int i = 0; i < cart.size(); i++) {
            CartItem item = cart.get(i);
            Product p = item.getProduct();

            System.out.println(
                    (i + 1) + ". " + p.getId()
                            + " | " + p.getName()
                            + " | Price: " + p.getPrice()
                            + " | Quantity: " + item.getQuantity()
                            + " | Total: " + item.getTotal()
            );
        }
    }

    public static double calculateTotal() {
        double total = 0;

        for (CartItem item : cart) {
            total += item.getTotal();
        }

        return total;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== HIT SHOP =====");
            System.out.println("1. Add product");
            System.out.println("2. Remove product");
            System.out.println("3. View cart");
            System.out.println("4. Calculate total");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\n===== PRODUCT LIST =====");
                    System.out.println(p1);
                    System.out.println(p2);

                    System.out.print("Enter product ID: ");
                    String id = sc.nextLine();

                    Product product = null;

                    if (id.equalsIgnoreCase("P01")) {
                        product = p1;
                    } else if (id.equalsIgnoreCase("P02")) {
                        product = p2;
                    }

                    if (product == null) {
                        System.out.println("Product not found.");
                        break;
                    }

                    System.out.print("Enter quantity: ");
                    int quantity = sc.nextInt();

                    addItem(product, quantity);
                    break;

                case 2:
                    printItems();

                    System.out.print("Enter item number to remove: ");
                    int number = sc.nextInt();

                    removeItem(number - 1);
                    break;

                case 3:
                    printItems();
                    break;

                case 4:
                    System.out.println(
                            "Total price: " + calculateTotal() + " VND"
                    );
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
