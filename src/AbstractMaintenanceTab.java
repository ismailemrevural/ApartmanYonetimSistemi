import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;

public abstract class AbstractMaintenanceTab extends JPanel implements TabView {
    protected JTable maintenanceTable;
    protected DefaultTableModel tableModel;
    protected JButton addButton, deleteButton;
    protected JTextField subjectField, descriptionField, priceField;
    protected JDateChooser dateChooser;

    public AbstractMaintenanceTab() {
        setLayout(new BorderLayout());

        // Table model setup
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{
                "IslemID", "Konu", "Açıklama", "Fiyat", "Tarih"
        });

        maintenanceTable = new JTable(tableModel);

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(maintenanceTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));

        inputPanel.add(new JLabel("Konu:"));
        subjectField = new JTextField();
        inputPanel.add(subjectField);

        inputPanel.add(new JLabel("Açıklama:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        inputPanel.add(new JLabel("Fiyat:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        inputPanel.add(new JLabel("Tarih:"));
        dateChooser = new JDateChooser();
        inputPanel.add(dateChooser);

        if (UserSession.getInstance().getRole().equalsIgnoreCase("Yonetici")) {
            add(inputPanel, BorderLayout.NORTH);
        }

        // Button panel
        JPanel buttonPanel = new JPanel();

        addButton = new JButton("Kayıt Ekle");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMaintenanceRecord();
            }
        });
        buttonPanel.add(addButton);

        deleteButton = new JButton("Seçilen Kaydı Sil");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMaintenanceRecord();
            }
        });
        buttonPanel.add(deleteButton);

        if (UserSession.getInstance().getRole().equalsIgnoreCase("Yonetici")) {
            add(buttonPanel, BorderLayout.SOUTH);
        }
        // Load maintenance records
        loadMaintenanceRecords();
    }

    // Template method for loading maintenance records
    protected void loadMaintenanceRecords() {
        try (Connection connection = Database.getConnection()) {
            String query = getLoadQuery();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            tableModel.setRowCount(0); // Clear the table
            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getInt("IslemID"),
                        resultSet.getString("Konu"),
                        resultSet.getString("Aciklama"),
                        resultSet.getDouble("Fiyat"),
                        resultSet.getDate("Tarih").toLocalDate()
                });
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Bakım/Onarım kayıtları yüklenirken hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Template method for adding a maintenance record
    protected void addMaintenanceRecord() {
        String subject = subjectField.getText();
        String description = descriptionField.getText();
        String price = priceField.getText();
        LocalDate date = dateChooser.getDate() != null ? dateChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null;

        if (subject.isEmpty() || description.isEmpty() || price.isEmpty() || date == null) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = Database.getConnection()) {
            String query = getAddQuery();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, subject);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, Double.parseDouble(price));
            preparedStatement.setDate(4, Date.valueOf(date));

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Bakım/Onarım kaydı başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

            // Clear input fields
            subjectField.setText("");
            descriptionField.setText("");
            priceField.setText("");
            dateChooser.setDate(null);

            // Reload maintenance records
            loadMaintenanceRecords();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Bakım/Onarım kaydı eklenirken hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Template method for deleting a maintenance record
    protected void deleteMaintenanceRecord() {
        int selectedRow = maintenanceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silmek için bir kayıt seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int recordId = (int) maintenanceTable.getValueAt(selectedRow, 0);

        int confirmation = JOptionPane.showConfirmDialog(this, "Bu kaydı silmek istediğinizden emin misiniz?", "Silme Onayı", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            try (Connection connection = Database.getConnection()) {
                String query = getDeleteQuery();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, recordId);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Bakım/Onarım kaydı başarıyla silindi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

                // Reload maintenance records
                loadMaintenanceRecords();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Bakım/Onarım kaydı silinirken hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected abstract String getLoadQuery();
    protected abstract String getAddQuery();
    protected abstract String getDeleteQuery();
}