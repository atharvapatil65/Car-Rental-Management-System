import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.sql.SQLException;

public class ManageCarPanel extends JPanel {
    private List<Car> cars;
    private JTextField carIdField, brandField, modelField, priceField;
    private JComboBox<String> statusCombo, typeCombo;
    private DefaultTableModel tableModel;
    
    public ManageCarPanel(List<Car> cars) {
        this.cars = cars;
        setLayout(new BorderLayout(20, 20));
        setBackground(CarRentalManagementSystem.SECONDARY_COLOR);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        initComponents();
        loadCarsFromDatabase();
    }

    private void loadCarsFromDatabase() {
        try {
            cars.clear();
            cars.addAll(Car.getAllCars());
            updateCarTable();
        } catch (SQLException e) {
            UIUtils.showErrorDialog(this, "Error loading cars from database: " + e.getMessage());
        }
    }
    
    private void initComponents() {
        add(createFormPanel(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.CENTER);
        add(createTablePanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CarRentalManagementSystem.PRIMARY_COLOR, 2),
            new EmptyBorder(20, 20, 20, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        carIdField = UIUtils.createStyledTextField();
        brandField = UIUtils.createStyledTextField();
        modelField = UIUtils.createStyledTextField();
        statusCombo = new JComboBox<>(new String[]{"Available", "Booked"});
        statusCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        priceField = UIUtils.createStyledTextField();
        typeCombo = new JComboBox<>(new String[]{"Sedan", "SUV", "Hatchback", "Convertible", "Van"});
        typeCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        
        UIUtils.addFormField(formPanel, "Car ID", carIdField, gbc, 0);
        UIUtils.addFormField(formPanel, "Brand", brandField, gbc, 1);
        UIUtils.addFormField(formPanel, "Model", modelField, gbc, 2);
        UIUtils.addFormField(formPanel, "Type", typeCombo, gbc, 3);
        UIUtils.addFormField(formPanel, "Status", statusCombo, gbc, 4);
        UIUtils.addFormField(formPanel, "Price per Day", priceField, gbc, 5);
        
        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(CarRentalManagementSystem.SECONDARY_COLOR);
        
        JButton addBtn = UIUtils.createStyledButton("Add Car", CarRentalManagementSystem.PRIMARY_COLOR);
        JButton updateBtn = UIUtils.createStyledButton("Update Car", CarRentalManagementSystem.PRIMARY_COLOR);
        JButton removeBtn = UIUtils.createStyledButton("Remove Car", CarRentalManagementSystem.ACCENT_COLOR);
        JButton clearBtn = UIUtils.createStyledButton("Clear Fields", Color.GRAY);
        
        addBtn.addActionListener(e -> addCar());
        updateBtn.addActionListener(e -> updateCar());
        removeBtn.addActionListener(e -> removeCar());
        clearBtn.addActionListener(e -> clearFields());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(clearBtn);
        
        return buttonPanel;
    }
    
    private JScrollPane createTablePanel() {
        String[] columnNames = {"Car ID", "Brand", "Model", "Status", "Price/Day", "Type"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable carTable = new JTable(tableModel);
        carTable.setFont(new Font("Arial", Font.PLAIN, 14));
        carTable.setRowHeight(25);
        carTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        carTable.getTableHeader().setBackground(CarRentalManagementSystem.PRIMARY_COLOR);
        carTable.getTableHeader().setForeground(Color.darkGray);
        
        carTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = carTable.getSelectedRow();
            if (selectedRow != -1) {
                Car selectedCar = cars.get(selectedRow);
                populateFields(selectedCar);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(carTable);
        scrollPane.setPreferredSize(new Dimension(getWidth(), 300));
        return scrollPane;
    }
    
     private void addCar() {
        try {
            Car newCar = createCarFromFields();
            newCar.save(); // Save to database
            cars.add(newCar);
            updateCarTable();
            clearFields();
        } catch (SQLException e) {
            UIUtils.showErrorDialog(this, "Error adding car to database: " + e.getMessage());
        } catch (NumberFormatException ex) {
            UIUtils.showErrorDialog(this, "Invalid input. Please check your entries.");
        }
    }
    
    private void updateCar() {
        int selectedRow = ((JTable) ((JScrollPane) getComponent(2)).getViewport().getView()).getSelectedRow();
        if (selectedRow != -1) {
            try {
                Car updatedCar = createCarFromFields();
                updatedCar.update(); // Update in database
                cars.set(selectedRow, updatedCar);
                updateCarTable();
            } catch (SQLException e) {
                UIUtils.showErrorDialog(this, "Error updating car in database: " + e.getMessage());
            } catch (NumberFormatException ex) {
                UIUtils.showErrorDialog(this, "Invalid input. Please check your entries.");
            }
        } else {
            UIUtils.showWarningDialog(this, "Please select a car to update.");
        }
    }
    
    private void removeCar() {
        int selectedRow = ((JTable) ((JScrollPane) getComponent(2)).getViewport().getView()).getSelectedRow();
        if (selectedRow != -1) {
            try {
                Car car = cars.get(selectedRow);
                car.delete(); // Delete from database
                cars.remove(selectedRow);
                updateCarTable();
                clearFields();
            } catch (SQLException e) {
                UIUtils.showErrorDialog(this, "Error removing car from database: " + e.getMessage());
            }
        } else {
            UIUtils.showWarningDialog(this, "Please select a car to remove.");
        }
    }
    
    private Car createCarFromFields() {
        String carId = carIdField.getText();
        String brand = brandField.getText();
        String model = modelField.getText();
        String type = (String) typeCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();
        double price = Double.parseDouble(priceField.getText());
        return new Car(carId, brand, model, status, price, type);
    }
    
    private void updateCarTable() {
        tableModel.setRowCount(0);
        for (Car car : cars) {
            tableModel.addRow(new Object[]{
                car.getCarId(),
                car.getBrand(),
                car.getModel(),
                car.getType(),
                car.getStatus(),
                String.format("₹%.2f", car.getPrice()),
            });
        }
    }
    
    private void clearFields() {
        carIdField.setText("");
        brandField.setText("");
        modelField.setText("");
        typeCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        priceField.setText("");
    }
    
    private void populateFields(Car car) {
        carIdField.setText(car.getCarId());
        brandField.setText(car.getBrand());
        modelField.setText(car.getModel());
        typeCombo.setSelectedItem(car.getType());
        statusCombo.setSelectedItem(car.getStatus());
        priceField.setText(String.valueOf(car.getPrice()));
    }
}