package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class NestifyApp {
    private final Connection connection;
    private final Scanner scanner = new Scanner(System.in);
    private final UserDAO userDAO;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;
    private final BlacklistDAO blacklistDAO;
    private User currentUser;

    public NestifyApp(Connection connection) {
        this.connection = connection;
        this.userDAO = new UserDAO(connection);
        this.productDAO = new ProductDAO(connection);
        this.orderDAO = new OrderDAO(connection);
        this.blacklistDAO = new BlacklistDAO(connection);
    }

    public void run() {
        System.out.println("=== WELCOME TO NESTIFY MARKET ===");
        boolean exit = false;
        while (!exit) {
            exit = (currentUser == null) ? showGuestMenu() : showUserMenu();
        }
    }

    private boolean showGuestMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt(); scanner.nextLine();
        switch (choice) {
            case 1: registerUser(); return false;
            case 2: loginUser();    return false;
            case 3: System.out.println("Goodbye!"); return true;
            default: System.out.println("Invalid choice."); return false;
        }
    }

    private boolean showUserMenu() {
        System.out.println("\n=== USER MENU (" + currentUser.getName() + " - " + currentUser.getRole() + ") ===");
        System.out.println("1. View Profile");
        System.out.println("2. List Products");
        System.out.println("3. Search Products");
        System.out.println("4. Verify Email");

        if (canAddProduct()) {
            System.out.println("5. Add Product");
            System.out.println("6. View My Products");
        }

        if ("buyer".equals(currentUser.getRole())) {
            System.out.println("7. Place Order");
            System.out.println("8. View My Orders");
            System.out.println("9. Confirm Delivery");
            System.out.println("10. Pay Commission");
        }

        if ("admin".equals(currentUser.getRole())) {
            System.out.println("11. Manage Blacklist");
            System.out.println("12. View All Users");
        }

        System.out.println("0. Logout");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt(); scanner.nextLine();

        switch (choice) {
            case 1: viewProfile(); break;
            case 2: listProducts(); break;
            case 3: searchProducts(); break;
            case 4: verifyEmail(); break;
            case 5: if (canAddProduct()) addProduct(); break;
            case 6: if (canAddProduct()) viewSellerProducts(); break;
            case 7: if ("buyer".equals(currentUser.getRole())) placeOrder(); break;
            case 8: if ("buyer".equals(currentUser.getRole())) viewBuyerOrders(); break;
            case 9: if ("buyer".equals(currentUser.getRole())) confirmDelivery(); break;
            case 10: if ("buyer".equals(currentUser.getRole())) payCommission(); break;
            case 11: if ("admin".equals(currentUser.getRole())) manageBlacklist(); break;
            case 12: if ("admin".equals(currentUser.getRole())) viewAllUsers(); break;
            case 0: currentUser = null; System.out.println("Logged out."); return false;
            default: System.out.println("Invalid choice."); break;
        }
        return false;
    }

    private boolean canAddProduct() {
        String r = currentUser.getRole();
        return "seller".equals(r) || "admin".equals(r);
    }

    private void registerUser() {
        System.out.println("\n=== REGISTER NEW USER ===");
        System.out.print("Name: ");       String name = scanner.nextLine();
        System.out.print("Email: ");      String email = scanner.nextLine();
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
            System.out.println("Invalid email format."); return;
        }
        System.out.print("Password: ");   String pwd = scanner.nextLine();
        System.out.print("Role [buyer/seller/admin]: "); String role = scanner.nextLine().toLowerCase();
        if ("admin".equals(role)) {
            System.out.print("Admin key: ");
            if (!"NestifyAdmin123".equals(scanner.nextLine())) {
                System.out.println("Invalid admin key."); return;
            }
        }

        String otp = String.valueOf((int)(Math.random()*9000)+1000);
        try {
            EmailUtil.sendEmail(email, "OTP Verification", "Your OTP: " + otp);
            System.out.println("OTP sent to " + email);
        } catch (Exception e) {
            System.err.println("Failed to send OTP: " + e.getMessage()); return;
        }

        System.out.print("Enter OTP: ");
        if (!otp.equals(scanner.nextLine())) {
            System.out.println("Incorrect OTP."); return;
        }

        try {
            boolean ok = userDAO.registerUser(new User(0, name, email, pwd, role, true));
            System.out.println(ok ? "Registration successful!" : "Registration failed.");
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    private void loginUser() {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Email: ");    String email = scanner.nextLine();
        System.out.print("Password: "); String pwd   = scanner.nextLine();
        try {
            currentUser = userDAO.loginUser(email, pwd);
            System.out.println(currentUser != null
                    ? "Welcome, " + currentUser.getName()
                    : "Invalid credentials.");
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }
    }

    private void verifyEmail() {
        System.out.println("\n=== VERIFY EMAIL ===");
        try {
            String otp = String.valueOf((int)(Math.random()*9000)+1000);
            EmailUtil.sendEmail(currentUser.getEmail(), "Verify Email", "Your OTP: " + otp);
            System.out.print("Enter OTP: ");
            if (otp.equals(scanner.nextLine()) && userDAO.verifyEmail(currentUser.getUserId())) {
                currentUser.setEmailVerified(true);
                System.out.println("Email verified!");
            } else {
                System.out.println("Verification failed.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void viewProfile() {
        System.out.println("\n=== PROFILE ===");
        System.out.println("Name : " + currentUser.getName());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Role : " + currentUser.getRole());
        System.out.println("Verified: " + currentUser.isEmailVerified());
    }

    private void listProducts() {
        final int size = 5;
        int page = 1;
        while (true) {
            try {
                List<Product> list = productDAO.getProductsByPage(page, size);
                if (list.isEmpty()) {
                    System.out.println("No more products."); break;
                }
                System.out.println("\n-- Products (Page " + page + ") --");
                for (Product p : list) {
                    System.out.printf("ID:%d | %s | $%.2f%n",
                            p.getProductId(), p.getName(), p.getPrice());
                }
                System.out.print("[1]Next [2]Prev [0]Exit: ");
                int o = scanner.nextInt(); scanner.nextLine();
                if (o == 1) page++;
                else if (o == 2 && page>1) page--;
                else break;
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
                break;
            }
        }
    }

    private void searchProducts() {
        System.out.print("Keyword: ");
        String kw = scanner.nextLine();
        try {
            List<Product> res = productDAO.searchProducts(kw);
            if (res.isEmpty()) {
                System.out.println("No results for \"" + kw + "\".");
            } else {
                System.out.println("\n-- Search Results --");
                for (Product p : res) {
                    System.out.printf("ID:%d | %s | $%.2f%n",
                            p.getProductId(), p.getName(), p.getPrice());
                }
            }
        } catch (SQLException e) {
            System.err.println("Search error: " + e.getMessage());
        }
    }

    private void addProduct() {
        System.out.println("\n=== ADD PRODUCT ===");
        System.out.print("Name: ");        String n = scanner.nextLine();
        System.out.print("Desc: ");        String d = scanner.nextLine();
        double pr = 0;
        while (true) {
            try {
                System.out.print("Price: ");
                pr = scanner.nextDouble(); scanner.nextLine();
                if (pr < 0) throw new InputMismatchException();
                break;
            } catch (Exception e) {
                System.err.println("Invalid price."); scanner.nextLine();
            }
        }
        System.out.print("Status[new/second_hand]: ");
        String st = scanner.nextLine();
        try {
            boolean ok = productDAO.addProduct(new Product(
                    0, currentUser.getUserId(), n, d, pr, st
            ));
            System.out.println(ok ? "Product added." : "Add failed.");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void viewSellerProducts() {
        System.out.println("\n=== MY PRODUCTS ===");
        try {
            List<Product> all = productDAO.getAllProducts();
            for (Product p : all) {
                if (p.getSellerId() == currentUser.getUserId()) {
                    System.out.printf("ID:%d | %s | $%.2f%n",
                            p.getProductId(), p.getName(), p.getPrice());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void placeOrder() {
        System.out.println("\n=== PLACE ORDER ===");
        System.out.print("Product ID: ");
        int pid = scanner.nextInt();
        System.out.print("Quantity:   ");
        int qty = scanner.nextInt(); scanner.nextLine();

        try {
            Product p = productDAO.getProductById(pid);
            if (p == null) {
                System.out.println("Not found."); return;
            }
            double total = p.getPrice() * qty;
            Order o = new Order(0, currentUser.getUserId(), pid, "pending", total, "pending");
            if (orderDAO.createOrder(o)) {
                System.out.println("Order #" + o.getOrderId() + " placed. Confirm delivery when done.");
            } else {
                System.out.println("Order failed.");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void viewBuyerOrders() {
        System.out.println("\n=== YOUR ORDERS ===");
        try {
            List<Order> list = orderDAO.getOrdersByBuyer(currentUser.getUserId());
            for (Order o : list) {
                System.out.printf("Order %d | Product %d | $%.2f | status:%s | delivery:%s%n",
                        o.getOrderId(), o.getProductId(), o.getTotalPrice(),
                        o.getStatus(), o.getDeliveryStatus());
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void confirmDelivery() {
        System.out.print("Order ID to confirm: ");
        int id = scanner.nextInt(); scanner.nextLine();
        try {
            if (orderDAO.updateDeliveryStatus(id, "delivered")) {
                System.out.println("Delivery confirmed.");
            } else {
                System.out.println("Confirmation failed.");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void payCommission() {
        System.out.print("Order ID to pay commission: ");
        int id = scanner.nextInt(); scanner.nextLine();
        boolean ok = new PaymentService(connection).processCommission(id);
        System.out.println(ok ? "Commission paid, order completed." : "Commission payment failed.");
    }

    private void manageBlacklist() {
        System.out.print("User ID to blacklist: ");
        int uid = scanner.nextInt(); scanner.nextLine();
        System.out.print("Reason: ");
        String r = scanner.nextLine();
        try {
            boolean ok = blacklistDAO.blacklistUser(uid, r);
            System.out.println(ok ? "User blacklisted." : "Blacklist failed.");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void viewAllUsers() {
        System.out.println("\n=== ALL USERS ===");
        try {
            List<User> us = userDAO.getAllUsers();
            for (User u : us) {
                System.out.printf("ID:%d | %s | %s%n",
                        u.getUserId(), u.getName(), u.getRole());
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
