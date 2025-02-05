import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReadOnlyProductPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public ReadOnlyProductPanel() {
        setLayout(new BorderLayout());

        // Initialize components
        model = new DefaultTableModel(new Object[]{"ID", "Name", "Price"}, 0);
        table = new JTable(model);
        table.setEnabled(false); // Make the table read-only
        JScrollPane scrollPane = new JScrollPane(table);

        // Add components to panel
        JLabel titleLabel = new JLabel("Product List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load initial data
        loadProducts();
    }

    // Method to load products into JTable
    private void loadProducts() {
        model.setRowCount(0); // Reset table
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM produits")) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price")
                    });
                }
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL Driver not found. Please check your setup.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading products from database.");
        }
    }
}