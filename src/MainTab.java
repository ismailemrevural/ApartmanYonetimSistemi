import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class MainTab extends JPanel implements TabView {
    private JLabel welcomeLabel;
    private JLabel apartmentInfoLabel;

    public MainTab() {
        setLayout(new BorderLayout());

        // Hoşgeldiniz mesajı
        welcomeLabel = new JLabel("Hoşgeldiniz!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setPreferredSize(new Dimension(500, 50));
        add(welcomeLabel, BorderLayout.NORTH);

        // Apartman ve kullanıcı bilgilerini gösteren panel
        apartmentInfoLabel = new JLabel("Yükleniyor...", SwingConstants.CENTER);
        apartmentInfoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(apartmentInfoLabel, BorderLayout.CENTER);

        // Kullanıcı bilgilerini yükle
        loadUserData();
    }

    private void loadUserData() {
        // Veritabanı bağlantısı ve giriş yapan kullanıcının bilgileri
        try (Connection connection = Database.getConnection()) {
            // Giriş yapan kullanıcının bilgilerini almak için sorgu
            String query = "SELECT K.Ad AS KullaniciAdi, K.Soyad, A.Ad AS ApartmanAdi, B.BlokAdi, D.KatNo, D.DaireNo " +
                    "FROM Kullanicilar K " +
                    "JOIN Daireler D ON K.KullaniciID = D.KullaniciID " +
                    "JOIN Bloklar B ON D.BlokID = B.BlokID " +
                    "JOIN Apartman A ON B.ApartmanID = A.ApartmanID " +
                    "WHERE K.KullaniciID = ?"; // Kullanıcı ID'sine göre sorgu

            // Giriş yapan kullanıcının ID'sini al (UserSession kullanarak)
            int userId = UserSession.getInstance().getUserId();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String userName = resultSet.getString("KullaniciAdi") + " " + resultSet.getString("Soyad");
                String apartmentName = resultSet.getString("ApartmanAdi");
                String blockName = resultSet.getString("BlokAdi");
                int floorNumber = resultSet.getInt("KatNo");
                String apartmentNumber = resultSet.getString("DaireNo");

                // Hoşgeldiniz mesajı ve apartman bilgileri
                String welcomeMessage = "Hoşgeldiniz, " + userName + "!";
                String apartmentInfo = "Apartman: " + apartmentName + "\n" +
                        "Blok: " + blockName + " - Kat: " + floorNumber + " - Daire: " + apartmentNumber;

                welcomeLabel.setText(welcomeMessage);
                apartmentInfoLabel.setText("<html>" + apartmentInfo.replaceAll("\n", "<br>") + "</html>");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Kullanıcı bilgileri yüklenirken hata oluştu: " + e.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public JPanel getPanel() {
        return this;
    }
}
