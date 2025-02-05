import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ClientPanel extends JPanel {
    private final JPanel parentPanel;
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtEmail, txtPhone;
    private JComboBox<Integer> comboTableId;

    public ClientPanel(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        setLayout(new BorderLayout());

        // Initialize components
        model = new DefaultTableModel(new Object[]{"ID", "Name", "Email", "Phone", "Table ID"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        txtName = new JTextField(15);
        txtEmail = new JTextField(15);
        txtPhone = new JTextField(15);
        comboTableId = new JComboBox<>();

        JButton btnAddClient = new JButton("Add Client");
        JButton btnModifyClient = new JButton("Modify Client");
        JButton btnDeleteClient = new JButton("Delete Client");
        JButton btnViewProducts = new JButton("View Products");

        // Style buttons
        styleButton(btnAddClient, new Color(46, 89, 137), Color.WHITE);
        styleButton(btnModifyClient, new Color(46, 89, 137), Color.WHITE);
        styleButton(btnDeleteClient, new Color(46, 89, 137), Color.WHITE);
        styleButton(btnViewProducts, new Color(46, 89, 137), Color.WHITE);

        // Form panel with GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createStyledLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createStyledLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createStyledLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPhone, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(createStyledLabel("Table ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(comboTableId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(btnAddClient, gbc);

        gbc.gridy = 5;
        formPanel.add(btnModifyClient, gbc);

        gbc.gridy = 6;
        formPanel.add(btnDeleteClient, gbc);

        gbc.gridy = 7;
        formPanel.add(btnViewProducts, gbc);

        // Add components to panel
        JLabel titleLabel = new JLabel("Client Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        // Load initial data
        loadClients();
        loadTableIds();

        // Action listeners
        btnAddClient.addActionListener(e -> addClient());
        btnModifyClient.addActionListener(e -> modifyClient());
        btnDeleteClient.addActionListener(e -> deleteClient());
        btnViewProducts.addActionListener(e -> showReadOnlyProductPanel());
    }

    // Method to load clients into JTable
    private void loadClients() {
        model.setRowCount(0); // Reset table
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM clients")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getInt("table_id")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading clients from database: " + ex.getMessage());
        }
    }

    // Method to load available table IDs into JComboBox
    private void loadTableIds() {
        comboTableId.removeAllItems();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM tables WHERE available = true")) {
            while (rs.next()) {
                comboTableId.addItem(rs.getInt("id"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading table IDs from database: " + ex.getMessage());
        }
    }

    // Method to add a client
    private void addClient() {
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();
        Integer tableId = (Integer) comboTableId.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || tableId == null) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO clients (name, email, phone, table_id) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, tableId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Client added successfully!");
            loadClients();
            txtName.setText("");
            txtEmail.setText("");
            txtPhone.setText("");
            loadTableIds();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding client to database: " + ex.getMessage());
        }
    }

    // Method to modify a client
    private void modifyClient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client to modify.");
            return;
        }

        int clientId = (int) model.getValueAt(selectedRow, 0);
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();
        Integer tableId = (Integer) comboTableId.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || tableId == null) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE clients SET name = ?, email = ?, phone = ?, table_id = ? WHERE id = ?")) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, tableId);
            ps.setInt(5, clientId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Client modified successfully!");
            loadClients();
            txtName.setText("");
            txtEmail.setText("");
            txtPhone.setText("");
            loadTableIds();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error modifying client in database: " + ex.getMessage());
        }
    }

    // Method to delete a client
    private void deleteClient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client to delete.");
            return;
        }

        int clientId = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this client?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM clients WHERE id = ?")) {
            ps.setInt(1, clientId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Client deleted successfully!");
            loadClients();
            loadTableIds();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting client from database: " + ex.getMessage());
        }
    }

    // Method to show ReadOnlyProductPanel
    private void showReadOnlyProductPanel() {
        CardLayout layout = (CardLayout) parentPanel.getLayout();
        layout.show(parentPanel, "ReadOnlyProductPanel");
    }

    // Method to style buttons
    private void styleButton(JButton button, Color bgColor, Color textColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(button.getText());
    }

    // Method to create styled labels
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(0, 123, 255));
        return label;
    }

    public JMenuItem createMenuItem(String title) {
        JMenuItem menuItem = new JMenuItem(title);
        menuItem.addActionListener(e -> showPanel());
        return menuItem;
    }

    private void showPanel() {
        parentPanel.add(this, "ClientPanel");
        CardLayout layout = (CardLayout) parentPanel.getLayout();
        layout.show(parentPanel, "ClientPanel");
    }
}