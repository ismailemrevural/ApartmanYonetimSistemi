import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NotificationsTab extends JPanel implements TabView {
    private JTable notificationsTable;
    private DefaultTableModel tableModel;

    public NotificationsTab() {
        setLayout(new BorderLayout());

        // Table model setup
        tableModel = new DefaultTableModel();
        if (UserSession.getInstance().getRole().equalsIgnoreCase("Yonetici")) {
            tableModel.setColumnIdentifiers(new Object[]{
                    "BildirimID", "Gönderen", "Alıcı", "Başlık", "İçerik", "Gönderim Tarihi"
            });
        } else {
            tableModel.setColumnIdentifiers(new Object[]{
                    "Başlık", "İçerik", "Gönderim Tarihi"
            });
        }

        notificationsTable = new JTable(tableModel);

        if (UserSession.getInstance().getRole().equalsIgnoreCase("Sakin")) {
            notificationsTable.getColumnModel().getColumn(0).setPreferredWidth(75);
            notificationsTable.getColumnModel().getColumn(1).setPreferredWidth(500);
            notificationsTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        }

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(notificationsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add "Borçluları Bildir" button
        JButton notifyDebtorsButton = new JButton("Borçluları Bildir");
        notifyDebtorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyDebtors();
            }
        });
        add(notifyDebtorsButton, BorderLayout.SOUTH);

        // Load notifications
        loadNotifications();
    }

    private void loadNotifications() {
        try (Connection connection = Database.getConnection()) {
            if (UserSession.getInstance().getRole().equalsIgnoreCase("Yonetici")) {
                String query = "SELECT B.BildirimID, " +
                        "       CONCAT(G.Ad, ' ', G.Soyad) AS Gonderen, " +
                        "       CONCAT(A.Ad, ' ', A.Soyad) AS Alici, " +
                        "       B.Baslik, B.Icerik, B.GonderimTarihi " +
                        "FROM Bildirimler B " +
                        "LEFT JOIN Kullanicilar G ON B.GonderenID = G.KullaniciID " +
                        "LEFT JOIN Kullanicilar A ON B.AliciID = A.KullaniciID " +
                        "ORDER BY B.GonderimTarihi DESC";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                tableModel.setRowCount(0); // Clear the table
                while (resultSet.next()) {
                    tableModel.addRow(new Object[]{
                            resultSet.getInt("BildirimID"),
                            resultSet.getString("Gonderen"),
                            resultSet.getString("Alici"),
                            resultSet.getString("Baslik"),
                            resultSet.getString("Icerik"),
                            resultSet.getTimestamp("GonderimTarihi").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    });
                }
                resultSet.close();
                statement.close();
            }else {
                String query = "SELECT Baslik, Icerik, GonderimTarihi " +
                        "FROM Bildirimler " +
                        "WHERE AliciID = ? " +
                        "ORDER BY GonderimTarihi DESC";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, UserSession.getInstance().getUserId());
                ResultSet resultSet = statement.executeQuery();

                tableModel.setRowCount(0); // Clear the table
                while (resultSet.next()) {
                    tableModel.addRow(new Object[]{
                            resultSet.getString("Baslik"),
                            resultSet.getString("Icerik"),
                            resultSet.getTimestamp("GonderimTarihi").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    });
                }
                resultSet.close();
                statement.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Bildirimler yüklenirken hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void notifyDebtors() {
        try (Connection connection = Database.getConnection()) {
            // Find overdue payments
            String overdueQuery = "SELECT A.AidatID, D.KullaniciID, CONCAT(K.Ad, ' ', K.Soyad) AS KullaniciAdi, D.DaireNo, K.Email, K.Telefon, A.SonOdemeTarihi " +
                    "FROM Aidatlar A " +
                    "JOIN Daireler D ON A.DaireID = D.DaireID " +
                    "JOIN Kullanicilar K ON D.KullaniciID = K.KullaniciID " +
                    "WHERE A.SonOdemeTarihi < CURDATE() AND A.OdemeTarihi IS NULL";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(overdueQuery);

            // OBSERVER tasarım deseni kullanılarak bildirim gönderme işlemi
            NotificationManager notificationManager = new NotificationManager();
            notificationManager.addObserver(new SmsNotifier()); // SMS gönderici eklendi
            notificationManager.addObserver(new EmailNotifier()); // E-posta gönderici eklendi

            int notificationsSent = 0;
            while (resultSet.next()) {
                int userId = resultSet.getInt("KullaniciID");
                String userName = resultSet.getString("KullaniciAdi");
                String apartmentNo = resultSet.getString("DaireNo");
                String mail = resultSet.getString("Email");
                String phone = resultSet.getString("Telefon");
                LocalDate dueDate = resultSet.getDate("SonOdemeTarihi").toLocalDate();

                // Bildirim ekleme
                String insertNotificationQuery = "INSERT INTO Bildirimler (GonderenID, AliciID, Baslik, Icerik) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertNotificationQuery);

                String title = "Aidat Borcu Hatırlatma";
                String content = String.format(
                        "Sayın %s, %s numaralı daireniz için %s tarihinde ödenmesi gereken aidat henüz ödenmemiştir.",
                        userName, apartmentNo, dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );

                preparedStatement.setInt(1, UserSession.getInstance().getUserId());  // GönderenID
                preparedStatement.setInt(2, userId);  // AliciID
                preparedStatement.setString(3, title);
                preparedStatement.setString(4, content);
                preparedStatement.executeUpdate();

                // Gözlemcilere bildirim gönderme işlemi
                notificationManager.notifyObservers(title, content, phone);
                notificationManager.notifyObservers(title, content, mail);


                notificationsSent++;
            }

            resultSet.close();
            statement.close();

            JOptionPane.showMessageDialog(this, notificationsSent + " kullanıcıya bildirim gönderildi.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the notifications table
            loadNotifications();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Borçlulara bildirim gönderilirken hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public JPanel getPanel() {
        return this;
    }
}
