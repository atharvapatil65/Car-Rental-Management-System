import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CarRentalManagementSystem extends JFrame {
    private JPanel mainPanel, contentPanel;
    private SidebarPanel sidebarPanel;
    private JButton manageCarBtn, manageCustomerBtn, manageRentalBtn;
    private CardLayout cardLayout;
    private List<Car> cars;
    private List<Customer> customers;

    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    public static final Color SECONDARY_COLOR = new Color(236, 240, 241);
    public static final Color ACCENT_COLOR = new Color(231, 76, 60);
    public static final Color TEXT_COLOR = new Color(52, 73, 94);

    public CarRentalManagementSystem() {
        cars = new ArrayList<>();
        customers = new ArrayList<>();
        setTitle("Car Rental Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initComponents();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SECONDARY_COLOR);
        
        initSidebar();
        initContentPanel();
        
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        contentPanel.add(new ManageRentalPanel(), "MANAGE_RENTAL");
        
        add(mainPanel);
    }

    private void initSidebar() {
        sidebarPanel = new SidebarPanel(this);
        manageCarBtn = sidebarPanel.getManageCarBtn();
        manageCustomerBtn = sidebarPanel.getManageCustomerBtn();
        manageRentalBtn = sidebarPanel.getManageRentalBtn();
    }
    
    private void initContentPanel() {
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(SECONDARY_COLOR);
        
        contentPanel.add(new ManageCarPanel(cars), "MANAGE_CAR");
        contentPanel.add(new ManageCustomerPanel(customers), "MANAGE_CUSTOMER");
        contentPanel.add(new PlaceholderPanel("Manage Rentals"), "MANAGE_RENTAL");
        
        manageCarBtn.addActionListener(e -> cardLayout.show(contentPanel, "MANAGE_CAR"));
        manageCustomerBtn.addActionListener(e -> cardLayout.show(contentPanel, "MANAGE_CUSTOMER"));
        manageRentalBtn.addActionListener(e -> cardLayout.show(contentPanel, "MANAGE_RENTAL"));
    }
}
