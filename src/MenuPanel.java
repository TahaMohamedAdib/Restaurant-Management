import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class MenuPanel extends JPanel {
    private JTextField txtName, txtDescription;
    private JButton btnAddMenu, btnDeleteMenu;
    private JTable table;
    private DefaultTableModel model;

    public MenuPanel(JPanel parentPanel) {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 240));

        txtName = new JTextField();
        txtDescription = new JTextField();
        btnAddMenu = new JButton("Add Menu");
        btnDeleteMenu = new JButton("Delete Menu");

        styleButton(btnAddMenu, new Color(0, 123, 255), Color.WHITE);
        styleButton(btnDeleteMenu, new Color(220, 53, 69), Color.WHITE);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Description"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(0, 123, 255));
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(new Color(240, 240, 240));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(createStyledLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(createStyledLabel("Description:"));
        formPanel.add(txtDescription);
        formPanel.add(btnAddMenu);
        formPanel.add(btnDeleteMenu);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadMenus();

        btnAddMenu.addActionListener(e -> addMenu());
        btnDeleteMenu.addActionListener(e -> deleteMenu());
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

    private void loadMenus() {
        model.addRow(new Object[]{1, "Menu 1", "Description 1"});
        model.addRow(new Object[]{2, "Menu 2", "Description 2"});
    }

    private void addMenu() {
        String name = txtName.getText();
        String description = txtDescription.getText();

        if (!name.isEmpty() && !description.isEmpty()) {
            model.addRow(new Object[]{model.getRowCount() + 1, name, description});
            txtName.setText("");
            txtDescription.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMenu() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a menu to delete!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}