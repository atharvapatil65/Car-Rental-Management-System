import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

public class ManageRentalPanel extends JPanel {
    private JTextField rentalIdField, customerIdField, carIdField, totalCostField, securityDepositField;
    private JComboBox<String> statusComboBox;
    private JDateChooser startDateChooser, endDateChooser;
    private JTable rentedCarsTable, availableCarsTable;
    private DefaultTableModel rentedCarsModel, availableCarsModel;
    private JButton addButton, updateButton, removeButton, clearButton;

    public ManageRentalPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(CarRentalManagementSystem.SECONDARY_COLOR);
        initComponents();
        loadRentedCars();
        loadAvailableCars();
    }

    private void initComponents() {
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CarRentalManagementSystem.SECONDARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize form components
        rentalIdField = UIUtils.createStyledTextField();
        customerIdField = UIUtils.createStyledTextField();
        carIdField = UIUtils.createStyledTextField();
        totalCostField = UIUtils.createStyledTextField();
        securityDepositField = UIUtils.createStyledTextField();
        statusComboBox = new JComboBox<>(new String[]{"Active", "Completed", "Cancelled"});
        startDateChooser = new JDateChooser();
        endDateChooser = new JDateChooser();

        // Add form fields
        UIUtils.addFormField(formPanel, "Rental ID:", rentalIdField, gbc, 0);
        UIUtils.addFormField(formPanel, "Customer ID:", customerIdField, gbc, 1);
        UIUtils.addFormField(formPanel, "Car ID:", carIdField, gbc, 2);
        UIUtils.addFormField(formPanel, "Start Date:", startDateChooser, gbc, 3);
        UIUtils.addFormField(formPanel, "End Date:", endDateChooser, gbc, 4);
        UIUtils.addFormField(formPanel, "Total Cost:", totalCostField, gbc, 5);
        UIUtils.addFormField(formPanel, "Security Deposit:", securityDepositField, gbc, 6);
        UIUtils.addFormField(formPanel, "Status:", statusComboBox, gbc, 7);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(CarRentalManagementSystem.SECONDARY_COLOR);

        addButton = UIUtils.createStyledButton("Add Rental", CarRentalManagementSystem.PRIMARY_COLOR);
        updateButton = UIUtils.createStyledButton("Update Rental", CarRentalManagementSystem.PRIMARY_COLOR);
        removeButton = UIUtils.createStyledButton("Remove Rental", CarRentalManagementSystem.ACCENT_COLOR);
        clearButton = UIUtils.createStyledButton("Clear Fields", new Color(149, 165, 166));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);

        // Tables Panel
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        tablesPanel.setBackground(CarRentalManagementSystem.SECONDARY_COLOR);

        // Rented Cars Table
        rentedCarsModel = new DefaultTableModel(
            new Object[]{"Rental ID", "Car ID", "Brand", "Model", "Customer ID", "Customer Name", "Start Date", "End Date", "Total Cost", "Security Deposit", "Status"}, 0
        );
        rentedCarsTable = new JTable(rentedCarsModel);
        JScrollPane rentedScrollPane = new JScrollPane(rentedCarsTable);
        rentedScrollPane.setBorder(BorderFactory.createTitledBorder("Rented Cars"));

        // Available Cars Table
        availableCarsModel = new DefaultTableModel(
            new Object[]{"Car ID", "Brand", "Model", "Type", "Price per Day"}, 0
        );
        availableCarsTable = new JTable(availableCarsModel);
        JScrollPane availableScrollPane = new JScrollPane(availableCarsTable);
        availableScrollPane.setBorder(BorderFactory.createTitledBorder("Available Cars"));

        tablesPanel.add(rentedScrollPane);
        tablesPanel.add(availableScrollPane);

        // Add form and buttons together in BorderLayout.NORTH
        JPanel formAndButtonPanel = new JPanel(new BorderLayout());
        formAndButtonPanel.add(formPanel, BorderLayout.CENTER);
        formAndButtonPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add everything to the main panel
        add(formAndButtonPanel, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);

        // Add button listeners
        addButton.addActionListener(e -> addRental());
        updateButton.addActionListener(e -> updateRental());
        removeButton.addActionListener(e -> removeRental());
        clearButton.addActionListener(e -> clearFields());
    }

    private void loadRentedCars() {
        rentedCarsModel.setRowCount(0);
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT r.*, c.brand, c.model, cu.name " +
                "FROM rentals r " +
                "JOIN cars c ON r.car_id = c.car_id " +
                "JOIN customers cu ON r.customer_id = cu.customer_id " +
                "WHERE r.status = 'Active'")) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rentedCarsModel.addRow(new Object[]{
                    rs.getString("rental_id"),
                    rs.getString("car_id"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getDouble("total_cost"),
                    rs.getDouble("security_deposit"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            UIUtils.showErrorDialog(this, "Error loading rented cars: " + e.getMessage());
        }
    }

    private void loadAvailableCars() {
        availableCarsModel.setRowCount(0);
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM cars WHERE status = 'Available'")) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                availableCarsModel.addRow(new Object[]{
                    rs.getString("car_id"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getString("type"),
                    rs.getDouble("price")
                });
            }
        } catch (SQLException e) {
            UIUtils.showErrorDialog(this, "Error loading available cars: " + e.getMessage());
        }
    }

    private void addRental() {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO rentals (rental_id, car_id, customer_id, start_date, end_date, total_cost, security_deposit, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, rentalIdField.getText());
            stmt.setString(2, carIdField.getText());
            stmt.setString(3, customerIdField.getText());
            stmt.setDate(4, new java.sql.Date(startDateChooser.getDate().getTime()));
            stmt.setDate(5, new java.sql.Date(endDateChooser.getDate().getTime()));
            stmt.setDouble(6, Double.parseDouble(totalCostField.getText()));
            stmt.setDouble(7, Double.parseDouble(securityDepositField.getText()));
            stmt.setString(8, statusComboBox.getSelectedItem().toString());

            stmt.executeUpdate();

            // Update car status
            updateCarStatus(carIdField.getText(), "Rented");

            clearFields();
            loadRentedCars();
            loadAvailableCars();
        } catch (SQLException e) {
            UIUtils.showErrorDialog(this, "Error adding rental: " + e.getMessage());
        }
    }

    private void updateRental() {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "UPDATE rentals SET start_date = ?, end_date = ?, total_cost = ?, security_deposit = ?, status = ? " +
                "WHERE rental_id = ?")) {
            
            stmt.setDate(1, new java.sql.Date(startDateChooser.getDate().getTime()));
            stmt.setDate(2, new java.sql.Date(endDateChooser.getDate().getTime()));
            stmt.setDouble(3, Double.parseDouble(totalCostField.getText()));
            stmt.setDouble(4, Double.parseDouble(securityDepositField.getText()));
            stmt.setString(5, statusComboBox.getSelectedItem().toString());
            stmt.setString(6, rentalIdField.getText());

            stmt.executeUpdate();

            // If rental is completed, update car status to Available
            if (statusComboBox.getSelectedItem().toString().equals("Completed")) {
                updateCarStatus(carIdField.getText(), "Available");
            }

            clearFields();
            loadRentedCars();
            loadAvailableCars();
        } catch (SQLException e) {
            UIUtils.showErrorDialog(this, "Error updating rental: " + e.getMessage());
        }
    }

    private void removeRental() {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM rentals WHERE rental_id = ?")) {
            
            stmt.setString(1, rentalIdField.getText());
            stmt.executeUpdate();

            // Update car status to Available
            updateCarStatus(carIdField.getText(), "Available");

            clearFields();
            loadRentedCars();
            loadAvailableCars();
        } catch (SQLException e) {
            UIUtils.showErrorDialog(this, "Error removing rental: " + e.getMessage());
        }
    }

    private void updateCarStatus(String carId, String status) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "UPDATE cars SET status = ? WHERE car_id = ?")) {
            
            stmt.setString(1, status);
            stmt.setString(2, carId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            UIUtils.showErrorDialog(this, "Error updating car status: " + e.getMessage());
        }
    }

    private void clearFields() {
        rentalIdField.setText("");
        customerIdField.setText("");
        carIdField.setText("");
        startDateChooser.setDate(null);
        endDateChooser.setDate(null);
        totalCostField.setText("");
        securityDepositField.setText("");
        statusComboBox.setSelectedIndex(0);
    }
}
