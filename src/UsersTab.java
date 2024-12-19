import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UsersTab extends JPanel implements TabView {
    private JTable usersTable;
    private DefaultTableModel tableModel;

    public UsersTab() {
        setLayout(new BorderLayout());

        // Table model setup
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{
                "KullanıcıID", "Ad Soyad", "Telefon", "Blok", "Daire Tipi", "Daire No", "Rol", "Sil", "Yetki Ver"
        });

        usersTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7 || column == 8; // Sadece "Sil" ve "Yetki Ver" butonlarına tıklanabilir
            }
        };

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(usersTable);
        add(scrollPane, BorderLayout.CENTER);

        // Populate the table with user data
        loadUsers();

        // Add button renderers and editors
        usersTable.getColumn("Sil").setCellRenderer(new ButtonRenderer());
        usersTable.getColumn("Sil").setCellEditor(new ButtonEditor(new JCheckBox(), "Sil"));

        usersTable.getColumn("Yetki Ver").setCellRenderer(new ButtonRenderer());
        usersTable.getColumn("Yetki Ver").setCellEditor(new ButtonEditor(new JCheckBox(), "Yetki Ver"));
    }

    private void loadUsers() {
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT K.KullaniciID, CONCAT(K.Ad, ' ', K.Soyad) AS AdSoyad, K.Telefon, B.BlokAdi, DT.TipAdi, D.DaireNo, K.Rol " +
                    "FROM Kullanicilar K " +
                    "LEFT JOIN Daireler D ON K.KullaniciID = D.KullaniciID " +
                    "LEFT JOIN Bloklar B ON D.BlokID = B.BlokID " +
                    "LEFT JOIN DaireTipleri DT ON D.TipID = DT.TipID";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                if (UserSession.getInstance().getUserId() == resultSet.getInt("KullaniciID")) {
                    tableModel.addRow(new Object[]{
                            resultSet.getInt("KullaniciID"),
                            resultSet.getString("AdSoyad"),
                            resultSet.getString("Telefon"),
                            resultSet.getString("BlokAdi"),
                            resultSet.getString("TipAdi"),
                            resultSet.getString("DaireNo"),
                            resultSet.getString("Rol"),
                            null,
                            null
                    });
                    continue;
                }
                tableModel.addRow(new Object[]{
                        resultSet.getInt("KullaniciID"),
                        resultSet.getString("AdSoyad"),
                        resultSet.getString("Telefon"),
                        resultSet.getString("BlokAdi"),
                        resultSet.getString("TipAdi"),
                        resultSet.getString("DaireNo"),
                        resultSet.getString("Rol"),
                        "Sil",
                        "Yetki Ver"
                });
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Kullanıcılar yüklenirken hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    // Renderer class for buttons
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Editor class for buttons
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String action;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox, String action) {
            super(checkBox);
            this.action = action;
            button = new JButton();
            button.setOpaque(true);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    int row = usersTable.getSelectedRow();
                    int userId = (int) tableModel.getValueAt(row, 0);

                    if (action.equals("Sil")) {
                        deleteUser(userId);
                    } else if (action.equals("Yetki Ver")) {
                        grantPermission(userId);
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            int currentUserId = UserSession.getInstance().getUserId();
            int rowUserId = (int) table.getValueAt(row, 0); // Satırdaki KullanıcıID

            button.setEnabled(true);
            if (currentUserId == rowUserId) {
                button.setEnabled(false); // Kendi kullanıcısını silemez veya yetki veremez
            }
            button.setText((value == null) ? "" : value.toString());
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return action;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }

        private void deleteUser(int userId) {
            try (Connection connection = Database.getConnection()) {
                String query = "DELETE FROM Kullanicilar WHERE KullaniciID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userId);
                preparedStatement.executeUpdate();

                JOptionPane.showMessageDialog(UsersTab.this, "Kullanıcı başarıyla silindi.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);

                // Refresh table
                tableModel.setRowCount(0);
                loadUsers();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(UsersTab.this, "Kullanıcı silinirken hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void grantPermission(int userId) {
            try (Connection connection = Database.getConnection()) {
                // Check current role before updating
                String currentRoleQuery = "SELECT Rol FROM Kullanicilar WHERE KullaniciID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(currentRoleQuery);
                preparedStatement.setInt(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String currentRole = resultSet.getString("Rol");
                    String newRole = currentRole.equals("Yonetici") ? "Sakin" : "Yonetici"; // Toggle between Yonetici and Sakin
                    String updateRoleQuery = "UPDATE Kullanicilar SET Rol = ? WHERE KullaniciID = ?";
                    preparedStatement = connection.prepareStatement(updateRoleQuery);
                    preparedStatement.setString(1, newRole);
                    preparedStatement.setInt(2, userId);
                    preparedStatement.executeUpdate();

                    JOptionPane.showMessageDialog(UsersTab.this, "Kullanıcının rolü başarıyla güncellendi.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);

                    // Refresh table
                    tableModel.setRowCount(0);
                    loadUsers();
                }

                resultSet.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(UsersTab.this, "Yetki verilirken hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
