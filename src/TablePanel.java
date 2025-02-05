import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TablePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private Map<Integer, Table> tables = new HashMap<>();
    private JTextField capacityField;

    public TablePanel() {
        setLayout(new BorderLayout());

        // Initialize components
        model = new DefaultTableModel(new Object[]{"ID", "Capacity"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        capacityField = new JTextField();

        JButton btnAddTable = new JButton("Add Table");
        JButton btnModifyTable = new JButton("Modify Table");
        JButton btnDeleteTable = new JButton("Delete Table");

        // Style buttons
        styleButton(btnAddTable, new Color(46, 89, 137), Color.WHITE);
        styleButton(btnModifyTable, new Color(46, 89, 137), Color.WHITE);
        styleButton(btnDeleteTable, new Color(46, 89, 137), Color.WHITE);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.add(createStyledLabel("Capacity:"));
        formPanel.add(capacityField);
        formPanel.add(btnAddTable);
        formPanel.add(btnModifyTable);
        formPanel.add(btnDeleteTable);

        // Add components to panel
        JLabel titleLabel = new JLabel("Table Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        // Load tables
        loadTables();

        // Action listeners
        btnAddTable.addActionListener(e -> addTable());
        btnModifyTable.addActionListener(e -> modifyTable());
        btnDeleteTable.addActionListener(e -> deleteTable());
    }

    // Method to load tables into JTable
    private void loadTables() {
        model.setRowCount(0); // Reset table
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM tables")) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getInt("capacity")
                    });
                }
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL Driver not found. Please check your setup.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading tables from database.");
        }
    }

    // Method to add a new table
    private void addTable() {
        int capacity;
        try {
            capacity = Integer.parseInt(capacityField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save to database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO tables (capacity, status) VALUES (?, 'Available')")) {
                ps.setInt(1, capacity);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Table added successfully!");
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL Driver not found. Please check your setup.", "Driver Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding table to database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Update JTable
        loadTables();
    }

    // Method to modify an existing table
    private void modifyTable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a table to modify.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int tableId = (int) model.getValueAt(selectedRow, 0);
        int capacity = Integer.parseInt(capacityField.getText());

        // Update database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE tables SET capacity = ? WHERE id = ?")) {
                ps.setInt(1, capacity);
                ps.setInt(2, tableId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Table modified successfully!");
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL Driver not found. Please check your setup.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error modifying table in database.");
        }

        // Update JTable
        loadTables();
    }

    // Method to delete a table
    private void deleteTable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a table to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int tableId = (int) model.getValueAt(selectedRow, 0);

        // Delete from database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM tables WHERE id = ?")) {
                ps.setInt(1, tableId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Table deleted successfully!");
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL Driver not found. Please check your setup.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting table from database.");
        }

        // Update JTable
        loadTables();
    }

    // Method to style buttons
    private void styleButton(JButton button, Color bgColor, Color textColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Method to create styled labels
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(0, 123, 255));
        return label;
    }
}