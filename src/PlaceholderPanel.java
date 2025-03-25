import javax.swing.*;
import java.awt.*;

public class PlaceholderPanel extends JPanel {
    public PlaceholderPanel(String title) {
        setLayout(new BorderLayout());
        setBackground(CarRentalManagementSystem.SECONDARY_COLOR);
        JLabel label = new JLabel(title + " (Not Implemented)");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(CarRentalManagementSystem.TEXT_COLOR);
        label.setHorizontalAlignment(JLabel.CENTER);
        add(label, BorderLayout.CENTER);
    }
}
