import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.logging.Logger;

public class ClientPanel extends JPanel {
    private final JPanel parentPanel;
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtEmail, txtPhone;
    private static final Logger logger = Logger.getLogger(ClientPanel.class.getName());
    private JComboBox<String> comboTableCapacity;
    private JComboBox<String> comboTableList;

    public ClientPanel(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Table setup
        model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Table ID"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(new Color(0, 123, 255));
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form for adding clients
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBackground(new Color(240, 240, 240));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        comboTableCapacity = new JComboBox<>();
        comboTableCapacity.setFont(new Font("Arial", Font.PLAIN, 14));

        comboTableList = new JComboBox<>();
        comboTableList.setFont(new Font("Arial", Font.PLAIN, 14));

        txtName = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();

        // Style text fields
        txtName.setFont(new Font("Arial", Font.PLAIN, 14));
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPhone.setFont(new Font("Arial", Font.PLAIN, 14));

        // Styled buttons
        JButton btnAdd = new JButton("Add Client");
        JButton btnModify = new JButton("Modify Client");
        JButton btnDelete = new JButton("Delete Client");

        btnAdd.setBackground(new Color(0, 123, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
        btnAdd.setFocusPainted(false);

        btnModify.setBackground(new Color(255, 193, 7));
        btnModify.setForeground(Color.WHITE);
        btnModify.setFont(new Font("Arial", Font.BOLD, 14));
        btnModify.setFocusPainted(false);

        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Arial", Font.BOLD, 14));
        btnDelete.setFocusPainted(false);

        // Add components to form
        formPanel.add(new JLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(txtPhone);
        formPanel.add(new JLabel("Table Capacity:"));
        formPanel.add(comboTableCapacity);
        formPanel.add(new JLabel("Table List:"));
        formPanel.add(comboTableList);
        formPanel.add(btnAdd);
        formPanel.add(btnModify);
        formPanel.add(btnDelete);

        // Add components to main panel
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        // Load initial data
        loadClients();
        loadAvailableTables();
        loadAllTables();

        // Action Listeners
        btnAdd.addActionListener(e -> addClient());
        btnModify.addActionListener(e -> modifyClient());
        btnDelete.addActionListener(e -> deleteClient());
    }

    private void loadClients() {
        model.setRowCount(0);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM clients")) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getInt("tableId")
                    });
                }
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL Driver not found. Please check your setup.");
            logger.severe("Driver not found: " + e.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading client data.");
            logger.severe("Error loading clients: " + ex.getMessage());
        }
    }

    private void loadAvailableTables() {
        comboTableCapacity.removeAllItems();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, capacity FROM tables WHERE status = 'Available'")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int capacity = rs.getInt("capacity");
                    comboTableCapacity.addItem("ID: " + id + " - Capacity: " + capacity);
                }
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL Driver not found. Please check your setup.");
            logger.severe("Driver not found: " + e.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading available tables.");
            logger.severe("Error loading available tables: " + ex.getMessage());
        }
    }

    private void loadAllTables() {
        comboTableList.removeAllItems();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, tableNumber, capacity FROM tables")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String tableNumber = rs.getString("tableNumber");
                    int capacity = rs.getInt("capacity");
                    comboTableList.addItem("ID: " + id + " - Table Number: " + tableNumber + " - Capacity: " + capacity);
                }
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL Driver not found. Please check your setup.");
            logger.severe("Driver not found: " + e.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading all tables.");
            logger.severe("Error loading all tables: " + ex.getMessage());
        }
    }

    private void addClient() {
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();
        String tableCapacityStr = (String) comboTableCapacity.getSelectedItem();
       /* if (tableCapacityStr == null) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }
        int tableId = Integer.parseInt(tableCapacityStr.split(" ")[1]); */

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO clients (name, email, phone, tableId) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, phone);
                //ps.setInt(4, tableId);
                ps.executeUpdate();

                // Get the generated client ID
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int clientId = generatedKeys.getInt(1);

                    // Update the table status and assign the client
                    try (PreparedStatement psUpdateTable = conn.prepareStatement("UPDATE tables SET status = 'Occupied', client = ? WHERE id = ?")) {
                        psUpdateTable.setInt(1, clientId);
                      //  psUpdateTable.setInt(2, tableId);
                        psUpdateTable.executeUpdate();
                    }

                    // Decrement the number of available tables with the same capacity
                    loadAvailableTables();

                    // Load the updated client data
                    loadClients();
                    JOptionPane.showMessageDialog(this, "Client added successfully!");
                    txtName.setText("");
                    txtEmail.setText("");
                    txtPhone.setText("");
                    comboTableCapacity.setSelectedIndex(-1);
                }
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL Driver not found. Please check your setup.");
            logger.severe("Driver not found: " + e.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding client.");
            logger.severe("Error adding client: " + ex.getMessage());
        }
    }

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
        String tableCapacityStr = (String) comboTableCapacity.getSelectedItem();
        if (tableCapacityStr == null) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }
        int tableId = Integer.parseInt(tableCapacityStr.split(" ")[1]);

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE clients SET name = ?, email = ?, phone = ?, tableId = ? WHERE id = ?")) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setInt(4, tableId);
                ps.setInt(5, clientId);
                ps.executeUpdate();

                // Update the table status and assign the client
                try (PreparedStatement psUpdateTable = conn.prepareStatement("UPDATE tables SET status = 'Occupied', client = ? WHERE id = ?")) {
                    psUpdateTable.setInt(1, clientId);
                    psUpdateTable.setInt(2, tableId);
                    psUpdateTable.executeUpdate();
                }

                // Decrement the number of available tables with the same capacity
                loadAvailableTables();

                JOptionPane.showMessageDialog(this, "Client modified successfully!");
                loadClients();
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL Driver not found. Please check your setup.");
            logger.severe("Driver not found: " + e.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error modifying client.");
            logger.severe("Error modifying client: " + ex.getMessage());
        }
    }

    private void deleteClient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client to delete.");
            return;
        }

        int clientId = (int) model.getValueAt(selectedRow, 0);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM clients WHERE id = ?")) {
                ps.setInt(1, clientId);
                ps.executeUpdate();

                // Update the table status
                try (PreparedStatement psUpdateTable = conn.prepareStatement("UPDATE tables SET status = 'Available', client = NULL WHERE client = ?")) {
                    psUpdateTable.setInt(1, clientId);
                    psUpdateTable.executeUpdate();
                }

                // Increment the number of available tables with the same capacity
                loadAvailableTables();

                JOptionPane.showMessageDialog(this, "Client deleted successfully!");
                loadClients();
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL Driver not found. Please check your setup.");
            logger.severe("Driver not found: " + e.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting client.");
            logger.severe("Error deleting client: " + ex.getMessage());
        }
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