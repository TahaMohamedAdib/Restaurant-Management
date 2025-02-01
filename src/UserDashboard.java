import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {
    public UserDashboard() {
        setTitle("User Dashboard - Restaurant Management");
        setSize(1000, 700); // Taille augmentée pour plus d'espace
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Utiliser un look and feel moderne
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Créer un panel parent pour CardLayout
        JPanel parentPanel = new JPanel(new CardLayout());
        parentPanel.setBackground(new Color(240, 240, 240)); // Fond clair

        // Créer un tabbed pane avec des onglets stylisés
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14)); // Police en gras
        tabbedPane.setBackground(new Color(0, 123, 255)); // Couleur de fond des onglets
        tabbedPane.setForeground(Color.WHITE); // Texte blanc

        // Ajouter des onglets avec des icônes (remplace les chemins par tes propres icônes)
        ImageIcon ordersIcon = new ImageIcon("path/to/orders_icon.png");
        ImageIcon productsIcon = new ImageIcon("path/to/products_icon.png");

        tabbedPane.addTab("Orders", ordersIcon, new OrderPanel(parentPanel));
        tabbedPane.addTab("Products", productsIcon, new ProductViewPanel()); // Nouveau panel pour consulter les produits

        // Ajouter un en-tête avec le nom de l'application
        JLabel header = new JLabel("Restaurant Management System", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24)); // Police grande et en gras
        header.setForeground(new Color(0, 123, 255)); // Couleur bleue
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Marges

        // Ajouter un pied de page avec des informations
        JLabel footer = new JLabel("© 2023 Restaurant Management System. Tous droits réservés.", SwingConstants.CENTER);
        footer.setFont(new Font("Arial", Font.PLAIN, 12)); // Police petite
        footer.setForeground(Color.GRAY); // Couleur grise
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Marges

        // Ajouter les composants à la fenêtre
        add(header, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserDashboard().setVisible(true));
    }
}