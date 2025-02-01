import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ProductPanel extends JPanel {
    private JPanel parentPanel;
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtPrice;

    public ProductPanel(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10)); // Ajouter des marges entre les composants
        setBackground(new Color(240, 240, 240)); // Fond clair

        // Table setup
        model = new DefaultTableModel(new String[]{"ID", "Name", "Price"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14)); // Police de la table
        table.setRowHeight(30); // Hauteur des lignes
        table.setSelectionBackground(new Color(0, 123, 255)); // Couleur de sélection
        table.setSelectionForeground(Color.WHITE); // Texte en blanc lors de la sélection

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges autour de la table

        // Form for adding products
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(new Color(240, 240, 240)); // Fond clair
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges

        txtName = new JTextField();
        txtPrice = new JTextField();
        JButton btnAdd = new JButton("Add Product");
        JButton btnDelete = new JButton("Delete Product");

        // Styliser les boutons
        styleButton(btnAdd, new Color(0, 123, 255), Color.WHITE); // Bouton bleu
        styleButton(btnDelete, new Color(220, 53, 69), Color.WHITE); // Bouton rouge

        formPanel.add(createStyledLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(createStyledLabel("Price:"));
        formPanel.add(txtPrice);
        formPanel.add(btnAdd);
        formPanel.add(btnDelete);

        // Add components to the panel
        JLabel titleLabel = new JLabel("Product Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Police grande et en gras
        titleLabel.setForeground(new Color(0, 123, 255)); // Couleur bleue
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Marges
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        // Load initial data
        loadProducts();

        // Action Listeners
        btnAdd.addActionListener(e -> addProduct());
        btnDelete.addActionListener(e -> deleteProduct());
    }

    // Méthode pour styliser les boutons
    private void styleButton(JButton button, Color bgColor, Color textColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Police en gras
        button.setBackground(bgColor); // Couleur de fond
        button.setForeground(textColor); // Couleur du texte
        button.setFocusPainted(false); // Enlever le contour de focus
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Marges internes
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Curseur en forme de main
    }

    // Méthode pour créer des labels stylisés
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14)); // Police en gras
        label.setForeground(new Color(0, 123, 255)); // Couleur bleue
        return label;
    }

    private void loadProducts() {
        model.setRowCount(0); // Réinitialiser la table
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM produits")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getDouble("price")});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addProduct() {
        String name = txtName.getText();
        String price = txtPrice.getText();

        if (name.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO produits (name, price) VALUES (?, ?)")) {
            ps.setString(1, name);
            ps.setDouble(2, Double.parseDouble(price));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product added successfully!");
            loadProducts();
            txtName.setText("");
            txtPrice.setText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
            return;
        }

        int productId = (int) model.getValueAt(selectedRow, 0);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM produits WHERE id = ?")) {
            ps.setInt(1, productId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product deleted successfully!");
            loadProducts();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public JMenuItem createMenuItem(String title) {
        JMenuItem menuItem = new JMenuItem(title);
        menuItem.addActionListener(e -> showPanel());
        return menuItem;
    }

    private void showPanel() {
        parentPanel.add(this, "ProductPanel");
        CardLayout layout = (CardLayout) parentPanel.getLayout();
        layout.show(parentPanel, "ProductPanel");
    }
}