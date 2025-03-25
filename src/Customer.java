import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String customerId;
    private String name;
    private String email;
    private String phone;

    public Customer(String customerId, String name, String email, String phone) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters and setters remain the same
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    // Database operations
    public void save() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO customers (customer_id, name, email, phone) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, customerId);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.executeUpdate();
        }
    }

    public void update() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "UPDATE customers SET name=?, email=?, phone=? WHERE customer_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, customerId);
            stmt.executeUpdate();
        }
    }

    public void delete() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "DELETE FROM customers WHERE customer_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, customerId);
            stmt.executeUpdate();
        }
    }

    public static List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM customers";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                customers.add(new Customer(
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone")
                ));
            }
        }
        return customers;
    }
}