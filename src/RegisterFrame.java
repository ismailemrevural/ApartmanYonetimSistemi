import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class RegisterFrame extends JFrame {
    private JTextField nameField, surnameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JComboBox<String> blokComboBox, katComboBox, daireNoComboBox, daireTipiComboBox;

    public RegisterFrame() {
        setTitle("Kaydol");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        JLabel nameLabel = new JLabel("Ad:");
        nameLabel.setBounds(50, 50, 100, 30);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 50, 200, 30);
        add(nameField);

        JLabel surnameLabel = new JLabel("Soyad:");
        surnameLabel.setBounds(50, 100, 100, 30);
        add(surnameLabel);

        surnameField = new JTextField();
        surnameField.setBounds(150, 100, 200, 30);
        add(surnameField);

        JLabel emailLabel = new JLabel("E-posta:");
        emailLabel.setBounds(50, 150, 100, 30);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 150, 200, 30);
        add(emailField);

        JLabel phoneLabel = new JLabel("Telefon:");
        phoneLabel.setBounds(50, 200, 100, 30);
        add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(150, 200, 200, 30);
        add(phoneField);
        // Telefon numarası alanı sadece sayı olmalı ve 10 haneli olmalı
        phoneField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || phoneField.getText().length() >= 10) {
                    e.consume();
                }
            }
        });

        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setBounds(50, 250, 100, 30);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 250, 200, 30);
        add(passwordField);

        // Blok seçimi
        JLabel blokLabel = new JLabel("Blok Seçiniz:");
        blokLabel.setBounds(50, 300, 100, 30);
        add(blokLabel);

        blokComboBox = new JComboBox<>();
        blokComboBox.setBounds(150, 300, 200, 30);
        loadBloklar(); // Blok verilerini yükle
        add(blokComboBox);

        // Kat seçimi
        JLabel katLabel = new JLabel("Kat Seçiniz:");
        katLabel.setBounds(50, 350, 100, 30);
        add(katLabel);

        katComboBox = new JComboBox<>();
        katComboBox.setBounds(150, 350, 200, 30);
        loadKatlar(); // Kat verilerini yükle
        add(katComboBox);

        // Daire numarası seçimi
        JLabel daireNoLabel = new JLabel("Daire No:");
        daireNoLabel.setBounds(50, 400, 100, 30);
        add(daireNoLabel);

        daireNoComboBox = new JComboBox<>();
        daireNoComboBox.setBounds(150, 400, 200, 30);
        loadDaireNo(); // Daire No verilerini yükle
        add(daireNoComboBox);

        // Daire Tipi seçimi
        JLabel daireTipiLabel = new JLabel("Daire Tipi:");
        daireTipiLabel.setBounds(50, 450, 100, 30);
        add(daireTipiLabel);

        daireTipiComboBox = new JComboBox<>();
        daireTipiComboBox.setBounds(150, 450, 200, 30);
        loadDaireTipleri(); // Daire Tipi verilerini yükle
        add(daireTipiComboBox);

        JButton registerButton = new JButton("Kaydol");
        registerButton.setBounds(150, 500, 100, 30);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String surname = surnameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();
                String password = new String(passwordField.getPassword());
                String selectedBlok = (String) blokComboBox.getSelectedItem();
                String selectedDaireTipi = (String) daireTipiComboBox.getSelectedItem();
                int selectedDaireNo = Integer.parseInt((String) daireNoComboBox.getSelectedItem());

                // Alanları doğrulama
                if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Tüm alanları doldurun.");
                    return;
                }

                // Veritabanında bu daiere numarası var mı kontrolü
                try (Connection connection = Database.getConnection()) {
                    String query = "SELECT DaireNo FROM Daireler WHERE DaireNo = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, (String) daireNoComboBox.getSelectedItem());
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "Bu daire numarası zaten kullanımda!");
                        return;
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // Veritabanında bu e-posta adresi var mı kontrolü
                try (Connection connection = Database.getConnection()) {
                    String query = "SELECT Email FROM Kullanicilar WHERE Email = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, email);
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "Bu e-posta adresi zaten kullanımda!");
                        return;
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // Veritabanında hiç kullanıcı var mı kontrol et bool değişkene ata
                boolean hasUsers = false;
                try (Connection connection = Database.getConnection()) {
                    String query = "SELECT COUNT(*) AS KullaniciSayisi FROM Kullanicilar";
                    PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        int userCount = resultSet.getInt("KullaniciSayisi");
                        hasUsers = userCount > 0;
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }



                // Veritabanına kullanıcı kaydını yapma
                try (Connection connection = Database.getConnection()) {
                    // Kullanıcıyı veritabanına ekleme
                    String query = "INSERT INTO Kullanicilar (Ad, Soyad, Email, Telefon, Sifre, Rol) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, name);
                    statement.setString(2, surname);
                    statement.setString(3, email);
                    statement.setString(4, phone);
                    statement.setString(5, password);
                    if(hasUsers) {
                        statement.setString(6, "Sakin");
                    } else {
                        statement.setString(6, "Yonetici");
                    }

                    statement.executeUpdate();
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    int userId = -1;
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1); // Kullanıcı ID'si

                        // Kullanıcıyı kaydettikten sonra Daireler tablosuna ekleme
                        // Blok ve daire tipi bilgilerini almak
                        String queryDaireler = "INSERT INTO Daireler (BlokID, TipID, KullaniciID, DaireNo, KatNo) "
                                + "VALUES ((SELECT BlokID FROM Bloklar WHERE BlokAdi = ?), "
                                + "(SELECT TipID FROM DaireTipleri WHERE TipAdi = ?), ?, ?, ?)";
                        PreparedStatement daireStatement = connection.prepareStatement(queryDaireler);
                        daireStatement.setString(1, selectedBlok);  // Blok
                        daireStatement.setString(2, selectedDaireTipi); // Daire tipi
                        daireStatement.setInt(3, userId); // Kullanıcı ID'si
                        daireStatement.setInt(4, selectedDaireNo); // Daire numarasını uygun şekilde alın
                        daireStatement.setInt(5, 1); // Kat numarasını uygun şekilde alın

                        daireStatement.executeUpdate();

                        JOptionPane.showMessageDialog(RegisterFrame.this, "Kayıt başarılı!");
                        dispose(); // Kayıt formunu kapat
                        LoginFrame.display(); // Giriş formunu aç
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        add(registerButton);
    }

    // Blok verilerini yükleyen metot
    private void loadBloklar() {
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT BlokAdi FROM Bloklar";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                blokComboBox.addItem(resultSet.getString("BlokAdi"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Kat sayısını yükleyen metot
    private void loadKatlar() {
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT KatSayisi FROM Bloklar WHERE BlokAdi = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, (String) blokComboBox.getSelectedItem());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int katSayisi = resultSet.getInt("KatSayisi");
                for (int i = 1; i <= katSayisi; i++) {
                    katComboBox.addItem(String.valueOf(i));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Apartman tablosunda bulunan daire sayısına göre daire numarası seçimi metodu
    private void loadDaireNo () {
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT DaireSayisi FROM Apartman WHERE ApartmanID = 1";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Apartman tablosunda bulunan daire sayısına göre daire numarası seçimi
            if (resultSet.next()) {
                int daireSayisi = resultSet.getInt("DaireSayisi");
                for (int i = 1; i <= daireSayisi; i++) {
                    daireNoComboBox.addItem(String.valueOf(i));
                }
            }

            while (resultSet.next()) {
                daireNoComboBox.addItem(resultSet.getString("DaireNo"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    // Daire tipi verilerini yükleyen metot
    private void loadDaireTipleri() {
        try (Connection connection = Database.getConnection()) { // Her çağrıda yeni bağlantı
            String query = "SELECT TipAdi FROM DaireTipleri";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                daireTipiComboBox.addItem(resultSet.getString("TipAdi"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
