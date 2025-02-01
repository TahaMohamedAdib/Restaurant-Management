import javax.swing.*;
import java.awt.*;

public class RestaurantApp extends JFrame {
    public RestaurantApp() {
        setTitle("Gestion de restaurant");
        setSize(1000, 700); // Taille augmentée pour plus d'espace
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Utiliser un look and feel moderne
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Créer la barre de menu
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(30, 58, 95)); // Bleu nuit
        menuBar.setForeground(Color.WHITE); // Couleur du texte de la barre de menu

        JMenu menuProducts = new JMenu("Products");
        JMenu menuClients = new JMenu("Clients");
        JMenu menuOrders = new JMenu("Orders");
        JMenu menuTables = new JMenu("Tables");

        // Styliser les menus
        styleMenu(menuProducts);
        styleMenu(menuClients);
        styleMenu(menuOrders);
        styleMenu(menuTables);

        menuBar.add(menuProducts);
        menuBar.add(menuClients);
        menuBar.add(menuOrders);
        menuBar.add(menuTables);
        setJMenuBar(menuBar);

        // Créer le panel principal avec CardLayout
        JPanel mainPanel = new JPanel(new CardLayout());
        mainPanel.setBackground(new Color(240, 240, 240)); // Fond clair
        add(mainPanel);

        // Créer les panels
        ProductPanel productPanel = new ProductPanel(mainPanel);
        ClientPanel clientPanel = new ClientPanel(mainPanel);
        OrderPanel orderPanel = new OrderPanel(mainPanel);
        TablePanel tablePanel = new TablePanel();

        // Ajouter les panels au CardLayout
        mainPanel.add(productPanel, "ProductPanel");
        mainPanel.add(clientPanel, "ClientPanel");
        mainPanel.add(orderPanel, "OrderPanel");
        mainPanel.add(tablePanel, "TablePanel");

        // Ajouter les éléments de menu
        menuProducts.add(productPanel.createMenuItem("Manage Products"));
        menuClients.add(clientPanel.createMenuItem("Manage Clients"));
        menuOrders.add(orderPanel.createMenuItem("Manage Orders"));
        menuTables.add(new JMenuItem("Manage Tables")).addActionListener(e -> {
            CardLayout layout = (CardLayout) mainPanel.getLayout();
            layout.show(mainPanel, "TablePanel");
        });
    }

    // Méthode pour styliser les menus
    private void styleMenu(JMenu menu) {
        menu.setFont(new Font("Arial", Font.BOLD, 14)); // Police en gras
        menu.setForeground(Color.WHITE); // Texte en blanc
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Démarrer avec la fenêtre de bienvenue
            new WelcomeFrame().setVisible(true);
        });
    }
}