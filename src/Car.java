import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Car {
    private String carId;
    private String brand;
    private String model;
    private String status;
    private double price;
    private String type;
    
    public Car(String carId, String brand, String model, String status, double price, String type) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.status = status;
        this.price = price;
        this.type = type;
    }
    
    // Getters and setters remain the same
    public String getCarId() { return carId; }
    public void setCarId(String carId) { this.carId = carId; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // Database operations
    public void save() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO cars (car_id, brand, model, status, price, type) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, carId);
            stmt.setString(2, brand);
            stmt.setString(3, model);
            stmt.setString(4, status);
            stmt.setDouble(5, price);
            stmt.setString(6, type);
            stmt.executeUpdate();
        }
    }

    public void update() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "UPDATE cars SET brand=?, model=?, status=?, price=?, type=? WHERE car_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, brand);
            stmt.setString(2, model);
            stmt.setString(3, status);
            stmt.setDouble(4, price);
            stmt.setString(5, type);
            stmt.setString(6, carId);
            stmt.executeUpdate();
        }
    }

    public void delete() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "DELETE FROM cars WHERE car_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, carId);
            stmt.executeUpdate();
        }
    }

    public static List<Car> getAllCars() throws SQLException {
        List<Car> cars = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM cars";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                cars.add(new Car(
                    rs.getString("car_id"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getString("status"),
                    rs.getDouble("price"),
                    rs.getString("type")
                ));
            }
        }
        return cars;
    }
}