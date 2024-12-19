import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Giriş Yap");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        JLabel emailLabel = new JLabel("E-posta:");
        emailLabel.setBounds(50, 50, 100, 30);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 50, 200, 30);
        add(emailField);

        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setBounds(50, 100, 100, 30);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 200, 30);
        add(passwordField);

        JButton loginButton = new JButton("Giriş Yap");
        loginButton.setBounds(150, 150, 100, 30);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Tüm alanları doldurun.");
                    return;
                }

                // Giriş kontrolü
                try (Connection connection = Database.getConnection()) {
                    String query = "SELECT * FROM Kullanicilar WHERE Email = ? AND Sifre = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, email);
                    statement.setString(2, password);
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Giriş başarılı!");
                        dispose(); // Giriş ekranını kapat
                        int userId = resultSet.getInt("KullaniciID");
                        String name = resultSet.getString("Ad");
                        String surname = resultSet.getString("Soyad");
                        String role = resultSet.getString("Rol");
                        UserSession.getInstance().setUserDetails(userId, name, surname, email, role);
                        System.out.println(role);
                        // Yeni ekranı açabilirsiniz
                        MainFrame.display();
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "E-posta veya şifre yanlış!");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        add(loginButton);

        JButton registerButton = new JButton("Kaydol");
        registerButton.setBounds(260, 150, 90, 30);
        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
        add(registerButton);
    }

    public static void display() {
        LoginFrame frame = new LoginFrame();
        frame.setVisible(true);
    }
}
