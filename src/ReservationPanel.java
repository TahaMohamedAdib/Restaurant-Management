import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReservationPanel extends JPanel {
    private JComboBox<String> comboClients, comboTables;
    private JTextField txtDate, txtTime, txtQuantity;
    private JButton btnAddReservation, btnDeleteReservation;
    private JTable table;
    private DefaultTableModel model;

    public ReservationPanel(JPanel parentPanel) {
        setLayout(new BorderLayout(10, 10)); // Ajouter des marges entre les composants
        setBackground(new Color(240, 240, 240)); // Fond clair

        // Initialisation des composants
        comboClients = new JComboBox<>();
        comboTables = new JComboBox<>();
        txtDate = new JTextField();
        txtTime = new JTextField();
        txtQuantity = new JTextField();
        btnAddReservation = new JButton("Add Reservation");
        btnDeleteReservation = new JButton("Delete Reservation");

        // Styliser les boutons
        styleButton(btnAddReservation, new Color(0, 123, 255), Color.WHITE); // Bouton bleu
        styleButton(btnDeleteReservation, new Color(220, 53, 69), Color.WHITE); // Bouton rouge

        // Configuration de la table
        model = new DefaultTableModel(new String[]{"ID", "Client", "Table", "Date", "Time", "Quantity"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14)); // Police de la table
        table.setRowHeight(30); // Hauteur des lignes
        table.setSelectionBackground(new Color(0, 123, 255)); // Couleur de sélection
        table.setSelectionForeground(Color.WHITE); // Texte en blanc lors de la sélection

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges autour de la table

        // Ajout des composants au panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(new Color(240, 240, 240)); // Fond clair
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges

        formPanel.add(createStyledLabel("Client:"));
        formPanel.add(comboClients);
        formPanel.add(createStyledLabel("Table:"));
        formPanel.add(comboTables);
        formPanel.add(createStyledLabel("Date (YYYY-MM-DD):"));
        formPanel.add(txtDate);
        formPanel.add(createStyledLabel("Time (HH:MM:SS):"));
        formPanel.add(txtTime);
        formPanel.add(createStyledLabel("Quantity:"));
        formPanel.add(txtQuantity);
        formPanel.add(btnAddReservation);
        formPanel.add(btnDeleteReservation);

        // Ajouter un titre stylisé
        JLabel titleLabel = new JLabel("Reservation Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Police grande et en gras
        titleLabel.setForeground(new Color(0, 123, 255)); // Couleur bleue
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Marges

        // Ajouter les composants au panel
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        // Charger les données initiales
        loadClients();
        loadTables();
        loadReservations();

        // Action listeners
        btnAddReservation.addActionListener(e -> addReservation());
        btnDeleteReservation.addActionListener(e -> deleteReservation());
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

    // Méthode pour charger les clients
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

    // Méthode pour charger les tables
    private void loadTables() {
        model.setRowCount(0); // Réinitialiser la table
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT t.id, t.table_number, t.capacity, t.status, c.name AS client_name FROM tables t LEFT JOIN clients c ON t.client_id = c.id")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("table_number"),
                        rs.getInt("capacity"),
                        rs.getString("status"),
                        rs.getString("client_name") // Afficher le nom du client
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading tables: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Méthode pour charger les réservations
    private void loadReservations() {
        model.setRowCount(0); // Réinitialiser la table
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT r.id, c.name AS client_name, t.tableNumber, r.date, r.time, r.quantity " +
                             "FROM reservations r " +
                             "JOIN clients c ON r.client_id = c.id " +
                             "JOIN tables t ON r.table_id = t.id")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("client_name"),
                        rs.getString("tableNumber"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getInt("quantity")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading reservations.");
            e.printStackTrace();
        }
    }

    // Méthode pour ajouter une réservation
    private void addReservation() {
        String selectedClient = (String) comboClients.getSelectedItem();
        String selectedTable = (String) comboTables.getSelectedItem();
        String date = txtDate.getText();
        String time = txtTime.getText();
        String quantityText = txtQuantity.getText();

        if (selectedClient == null || selectedTable == null || date.isEmpty() || time.isEmpty() || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try {
            int clientId = Integer.parseInt(selectedClient.split(" - ")[0]);
            int tableId = Integer.parseInt(selectedTable.split(" - ")[0]);
            int quantity = Integer.parseInt(quantityText);

            // Vérifier la capacité de la table
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT capacity FROM tables WHERE id = ?")) {
                ps.setInt(1, tableId);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && quantity > rs.getInt("capacity")) {
                    JOptionPane.showMessageDialog(this, "Number of guests exceeds table capacity.");
                    return;
                }
            }

            // Ajouter la réservation
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO reservations (client_id, table_id, date, time, quantity) VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, clientId);
                ps.setInt(2, tableId);
                ps.setString(3, date);
                ps.setString(4, time);
                ps.setInt(5, quantity);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Reservation added successfully!");
                loadReservations();
                txtDate.setText("");
                txtTime.setText("");
                txtQuantity.setText("");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a valid number.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding reservation.");
            e.printStackTrace();
        }
    }

    // Méthode pour supprimer une réservation
    private void deleteReservation() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to delete.");
            return;
        }

        int reservationId = (int) model.getValueAt(selectedRow, 0);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM reservations WHERE id = ?")) {
            ps.setInt(1, reservationId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Reservation deleted successfully!");
            loadReservations();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting reservation.");
            e.printStackTrace();
        }
    }
}