import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OrderPanel extends JPanel {
    private final JPanel parentPanel;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> comboClients, comboProducts;
    private JTextField txtQuantity;

    public OrderPanel(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 240));

        model = new DefaultTableModel(new String[]{"Order ID", "Client", "Product", "Quantity", "Total Price"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(0, 123, 255));
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(new Color(240, 240, 240));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        comboClients = new JComboBox<>();
        comboProducts = new JComboBox<>();
        txtQuantity = new JTextField();
        JButton btnAddOrder = new JButton("Add Order");
        JButton btnDeleteOrder = new JButton("Delete Order");

        styleButton(btnAddOrder, new Color(0, 123, 255), Color.WHITE);
        styleButton(btnDeleteOrder, new Color(220, 53, 69), Color.WHITE);

        formPanel.add(createStyledLabel("Client:"));
        formPanel.add(comboClients);
        formPanel.add(createStyledLabel("Product:"));
        formPanel.add(comboProducts);
        formPanel.add(createStyledLabel("Quantity:"));
        formPanel.add(txtQuantity);
        formPanel.add(btnAddOrder);
        formPanel.add(btnDeleteOrder);

        JLabel titleLabel = new JLabel("Order Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        loadClients();
        loadProducts();
        loadOrders();

        btnAddOrder.addActionListener(e -> addOrder());
        btnDeleteOrder.addActionListener(e -> deleteOrder());
    }

    private void styleButton(JButton button, Color bgColor, Color textColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

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
        parentPanel.add(this, "OrderPanel");
        CardLayout layout = (CardLayout) parentPanel.getLayout();
        layout.show(parentPanel, "OrderPanel");
    }

    private void loadClients() {
        comboClients.removeAllItems();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM clients")) {
            while (rs.next()) {
                comboClients.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading clients.");
            e.printStackTrace();
        }
    }

    private void loadProducts() {
        comboProducts.removeAllItems();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM produits")) {
            while (rs.next()) {
                comboProducts.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products.");
            e.printStackTrace();
        }
    }

    private void loadOrders() {
        model.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT o.id, c.name AS client_name, p.name AS product_name, o.quantity, (p.price * o.quantity) AS total_price " +
                             "FROM commandes o " +
                             "JOIN clients c ON o.client_id = c.id " +
                             "JOIN produits p ON o.product_id = p.id")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("client_name"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("total_price")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders.");
            e.printStackTrace();
        }
    }

    private void addOrder() {
        String selectedClient = (String) comboClients.getSelectedItem();
        String selectedProduct = (String) comboProducts.getSelectedItem();
        String quantityText = txtQuantity.getText();

        if (selectedClient == null || selectedProduct == null || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try {
            int clientId = Integer.parseInt(selectedClient.split(" - ")[0]);
            int productId = Integer.parseInt(selectedProduct.split(" - ")[0]);
            int quantity = Integer.parseInt(quantityText);

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO commandes (client_id, product_id, quantity) VALUES (?, ?, ?)")) {
                ps.setInt(1, clientId);
                ps.setInt(2, productId);
                ps.setInt(3, quantity);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Order added successfully!");
                loadOrders();
                txtQuantity.setText("");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a valid number.");
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding order.");
            e.printStackTrace();
        }
    }

    private void deleteOrder() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to delete.");
            return;
        }

        int orderId = (int) model.getValueAt(selectedRow, 0);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM commandes WHERE id = ?")) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Order deleted successfully!");
            loadOrders();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting order.");
            e.printStackTrace();
        }
    }
}