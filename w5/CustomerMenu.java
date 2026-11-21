import java.util.List;

import model.Cart;
import model.Customer;
import model.Order;
import model.OrderItem;
import model.Product;
import model.ProductGroup;
import service.BankPaymentService;
import service.MoMoPaymentService;
import service.OrderService;
import service.PaymentService;
import service.ProductService;

public class CustomerMenu {
    private final ProductService productService;
    private final OrderService orderService;
    private final Cart cart = new Cart();

    public CustomerMenu(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    public void run() {
        cart.clear();   // người mua mới thì giỏ mới

        int choice;
        do {
            System.out.println("\n===== HITSHOP - Welcome =====");
            System.out.println("1. Browse products");
            System.out.println("2. Add to cart");
            System.out.println("3. View cart");
            System.out.println("4. Edit cart");
            System.out.println("5. Checkout");
            System.out.println("6. My orders");
            System.out.println("0. Back");
            choice = Input.readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    browseProducts();
                    break;
                case 2:
                    addToCart();
                    break;
                case 3:
                    viewCart();
                    break;
                case 4:
                    editCart();
                    break;
                case 5:
                    checkout();
                    break;
                case 6:
                    myOrders();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    // ---------- 1. Browse ----------

    private void browseProducts() {
        ProductGroup[] groups = ProductGroup.values();
        // Thêm "All" lên đầu, nên chỉ số 0 = tất cả nhóm, chỉ số i = groups[i - 1]
        Object[] options = new Object[groups.length + 1];
        options[0] = "All";
        for (int i = 0; i < groups.length; i++) {
            options[i + 1] = groups[i];
        }

        int c = Input.readOption("Browse by group", options);
        if (c == -1) {
            return;
        }
        ProductGroup group = (c == 0) ? null : groups[c - 1];

        List<Product> list = productService.getAvailable(group);
        if (list.isEmpty()) {
            System.out.println("\nNo products available.");
            return;
        }
        System.out.println("\n===== PRODUCTS =====");
        for (Product p : list) {
            System.out.println("[" + p.getGroup() + "] " + p);
        }
    }

    // ---------- 2. Add to cart ----------

    private void addToCart() {
        Product product = productService.findById(Input.readText("Enter product ID: "));
        if (product == null) {
            System.out.println("Product not found.");
            return;
        }
        int quantity = Input.readInt("Enter quantity: ");
        try {
            cart.add(product, quantity);
            System.out.println("Added to cart.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // ---------- 3 & 4. View / Edit cart ----------

    private void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("\nYour cart is empty.");
            return;
        }
        System.out.println("\n===== YOUR CART =====");
        for (OrderItem item : cart.getItems()) {
            System.out.println("- " + item);
        }
        System.out.println("Total: " + String.format("%,.0f", cart.getTotal()));
    }

    private void editCart() {
        if (cart.isEmpty()) {
            System.out.println("\nYour cart is empty.");
            return;
        }
        viewCart();
        String id = Input.readText("Enter product ID in cart: ");

        System.out.println("1. Change quantity");
        System.out.println("2. Remove from cart");
        int choice = Input.readInt("Enter your choice: ");

        try {
            if (choice == 1) {
                cart.setQuantity(id, Input.readInt("Enter new quantity: "));
                System.out.println("Cart updated.");
            } else if (choice == 2) {
                cart.remove(id);
                System.out.println("Removed from cart.");
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // ---------- 5. Checkout ----------

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{9,11}");   // chỉ gồm 9 đến 11 chữ số
    }

    private void checkout() {
        if (cart.isEmpty()) {
            System.out.println("\nYour cart is empty.");
            return;
        }
        viewCart();

        String name = Input.readText("Enter your name: ");
        String phone = Input.readText("Enter your phone: ");
        if (!isValidPhone(phone)) {
            System.out.println("Invalid phone. Use 9-11 digits.");
            return;
        }

        try {
            Order order = orderService.checkout(new Customer(name, phone), cart);
            cart.clear();
            System.out.println("\nOrder placed!");
            System.out.println(order);

            if (Input.readYesNo("Pay now?")) {
                payOrder(order.getId());
            } else {
                System.out.println("You can pay later in \"My orders\".");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // ---------- 6. My orders ----------

    private void myOrders() {
        String phone = Input.readText("Enter your phone: ");
        int choice;
        do {
            List<Order> orders = orderService.getByPhone(phone);
            if (orders.isEmpty()) {
                System.out.println("\nNo orders found for this phone.");
                return;
            }
            System.out.println("\n===== MY ORDERS =====");
            for (Order o : orders) {
                System.out.println(o);
            }

            System.out.println("\n1. Pay an order");
            System.out.println("2. Cancel an order");
            System.out.println("0. Back");
            choice = Input.readInt("Enter your choice: ");

            if (choice == 1 || choice == 2) {
                // Chỉ thao tác được trên đơn của chính số điện thoại này
                Order order = orderService.findByIdAndPhone(
                        Input.readText("Enter order ID: "), phone);
                if (order == null) {
                    System.out.println("Order not found.");
                } else if (choice == 1) {
                    payOrder(order.getId());
                } else {
                    try {
                        orderService.cancelOrder(order.getId());
                        System.out.println("Order cancelled.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } else if (choice != 0) {
                System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    private void payOrder(String orderId) {
        Object[] methods = {"MoMo", "HITBank"};
        int m = Input.readOption("Payment method", methods);
        if (m == -1) {
            return;
        }
        PaymentService paymentService = (m == 0) ? new MoMoPaymentService() : new BankPaymentService();

        try {
            System.out.println(orderService.payOrder(orderId, paymentService));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
