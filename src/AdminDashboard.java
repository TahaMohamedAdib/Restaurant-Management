import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard(String role) {
        setTitle("Admin Dashboard - Restaurant Management");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel parentPanel = new JPanel(new CardLayout());
        parentPanel.setBackground(new Color(240, 240, 240));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(0, 123, 255));
        tabbedPane.setForeground(Color.WHITE);

        ImageIcon ordersIcon = new ImageIcon("path/to/orders_icon.png");
        ImageIcon clientsIcon = new ImageIcon("path/to/clients_icon.png");
        ImageIcon productsIcon = new ImageIcon("path/to/products_icon.png");
        ImageIcon tablesIcon = new ImageIcon("path/to/tables_icon.png");

        tabbedPane.addTab("Orders", ordersIcon, new OrderPanel(parentPanel));
        tabbedPane.addTab("Clients", clientsIcon, new ClientPanel(parentPanel));

        if ("Admin".equals(role)) {
            tabbedPane.addTab("Products", productsIcon, new ProductPanel(parentPanel));
            tabbedPane.addTab("Tables", tablesIcon, new TablePanel());
        }

        add(tabbedPane, BorderLayout.CENTER);

        JLabel header = new JLabel("Restaurant Management System", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setForeground(new Color(0, 123, 255));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        JLabel footer = new JLabel("© 2023 Restaurant Management System. All rights reserved.", SwingConstants.CENTER);
        footer.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.setForeground(Color.GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(footer, BorderLayout.SOUTH);
    }
}