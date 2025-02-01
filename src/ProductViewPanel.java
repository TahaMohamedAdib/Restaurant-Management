import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ProductViewPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel model;

    public ProductViewPanel() {
        setLayout(new BorderLayout(10, 10)); // Ajouter des marges entre les composants
        setBackground(new Color(240, 240, 240)); // Fond clair

        // Configurer la table
        model = new DefaultTableModel(new String[]{"ID", "Name", "Price"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14)); // Police de la table
        table.setRowHeight(30); // Hauteur des lignes
        table.setSelectionBackground(new Color(0, 123, 255)); // Couleur de sélection
        table.setSelectionForeground(Color.WHITE); // Texte en blanc lors de la sélection

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges autour de la table

        // Ajouter un titre stylisé
        JLabel titleLabel = new JLabel("Product List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Police grande et en gras
        titleLabel.setForeground(new Color(0, 123, 255)); // Couleur bleue
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Marges

        // Ajouter les composants au panel
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Charger les produits
        loadProducts();
    }

    private void loadProducts() {
        model.setRowCount(0); // Réinitialiser la table
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM produits")) {
            while (rs.next()) {
                // Ajouter une ligne à la table
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products.");
        }
    }
}