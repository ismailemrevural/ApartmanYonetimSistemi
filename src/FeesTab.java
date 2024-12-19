import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FeesTab extends JPanel implements TabView {
    private JComboBox<String> daireTipiComboBox;
    private JTextField amountField;
    private JTable aidatlarTable;
    private DefaultTableModel tableModel;
    private JCheckBox[] monthCheckBoxes;
    private JCheckBox allMonthsCheckBox;
    private JTabbedPane yearTabPane;
    private String selectedDaireTipi;

    public FeesTab() {
        setLayout(new BorderLayout());

        // Sol panel: Aidat Belirleme
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 0));

        // Daire Tipi Seçimi
        JPanel daireTipiPanel = new JPanel();
        daireTipiPanel.setLayout(new GridLayout(2, 1));
        JLabel daireTipiLabel = new JLabel("Daire Tipi:");
        daireTipiComboBox = new JComboBox<>();
        loadDaireTipleri(); // Veritabanından daire tiplerini yükler
        daireTipiPanel.add(daireTipiLabel);
        daireTipiPanel.add(daireTipiComboBox);
        leftPanel.add(daireTipiPanel, BorderLayout.NORTH);
        // Daire tipi seçimine göre tabloyu güncelleyin
        daireTipiComboBox.addActionListener(e -> loadCurrentFees());
        daireTipiComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedDaireTipi = (String) daireTipiComboBox.getSelectedItem();
                loadCurrentFees();  // Bu metot, daire tipine göre filtreleme yapacak
            }
        });


        // Yıl ve Ay Sekmeleri
        yearTabPane = new JTabbedPane();
        JPanel annualFeesPanel = new JPanel(new GridLayout(1, 1)); // Yıllık aidat paneli
        JPanel monthlyFeesPanel = new JPanel(new GridLayout(4, 3)); // Aylık aidat paneli

        // Yıllık Aidat Paneli
        allMonthsCheckBox = new JCheckBox("Tüm Aylar");
        allMonthsCheckBox.addActionListener(e -> updateTableByMonths());
        annualFeesPanel.add(allMonthsCheckBox);
        allMonthsCheckBox.setSelected(true);

        // Aylık Aidat Paneli
        String[] months = {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};
        monthCheckBoxes = new JCheckBox[12];
        for (int i = 0; i < months.length; i++) {
            monthCheckBoxes[i] = new JCheckBox(months[i]);
            monthCheckBoxes[i].addActionListener(e -> updateTableByMonths());
            monthlyFeesPanel.add(monthCheckBoxes[i]);
        }

        yearTabPane.add("Yıllık Aidat", annualFeesPanel);
        yearTabPane.add("Aylık Aidat", monthlyFeesPanel);
        leftPanel.add(yearTabPane, BorderLayout.CENTER);

        // Sekme değişimini dinleyen listener
        yearTabPane.addChangeListener(e -> updateTableByTab());

        // Aidat Miktarı ve Kaydet Butonu
        JPanel savePanel = new JPanel();
        savePanel.setLayout(new GridLayout(3, 1));
        JLabel amountLabel = new JLabel("Aidat Tutarı:");
        amountField = new JTextField();
        JButton saveButton = new JButton("Kaydet");
        saveButton.addActionListener(e -> saveFees(amountField.getText()));
        savePanel.add(amountLabel);
        savePanel.add(amountField);
        savePanel.add(saveButton);
        leftPanel.add(savePanel, BorderLayout.SOUTH);

        if (UserSession.getInstance().getRole().equalsIgnoreCase("Yonetici")) {
            add(leftPanel, BorderLayout.WEST);
        }


        // Sağ panel: Güncel Aidatlar Tablosu
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        JLabel tableLabel = new JLabel("Güncel Aidatlar");
        rightPanel.add(tableLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Fatura No" , "Sakin", "Daire No", "Daire Tipi", "Son Ödeme Tarihi", "Ödeme Tarihi", "Tutar", "Durum"}, 0);
        aidatlarTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(aidatlarTable);
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Tabloyu Yenile");
        refreshButton.addActionListener(e -> loadCurrentFees());
        rightPanel.add(refreshButton, BorderLayout.SOUTH);

        // "Ödendi", "Ödenmedi" comboBox'ı ve güncelle butonunu rightPanel'e ekleyin
        JPanel updatePanel = new JPanel();
        updatePanel.setLayout(new GridLayout(1, 2));
        JComboBox<String> durumComboBox = new JComboBox<>(new String[]{"Odendi", "Odenmedi"});
        JButton updateButton = new JButton("Güncelle");
        updateButton.addActionListener(e -> {
            int selectedRow = aidatlarTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen bir satır seçin!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String durum = (String) durumComboBox.getSelectedItem();
            int aidatID = Integer.parseInt(aidatlarTable.getValueAt(selectedRow, 0).toString());
            updateFeesStatus(aidatID, durum);
        });

        updatePanel.add(durumComboBox);
        updatePanel.add(updateButton);


        // Silme işlemi
        JButton deleteButton = new JButton("Sil");
        deleteButton.addActionListener(e -> {
            int selectedRow = aidatlarTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen bir satır seçin!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int aidatID = Integer.parseInt(aidatlarTable.getValueAt(selectedRow, 0).toString());
            try (Connection connection = Database.getConnection()) {
                String query = "DELETE FROM Aidatlar WHERE AidatID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, aidatID);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Aidat başarıyla silindi.", "Başarı", JOptionPane.INFORMATION_MESSAGE);
                loadCurrentFees(); // Tabloyu güncelle
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        updatePanel.add(deleteButton); // Silme butonunu ekliyoruz

        if(UserSession.getInstance().getRole().equalsIgnoreCase("Yonetici")) {
            add(updatePanel, BorderLayout.SOUTH);  // Update paneli doğru şekilde ekliyoruz
        }

        add(rightPanel, BorderLayout.CENTER); // Son olarak sağ paneli ana panele ekliyoruz


        loadCurrentFees(); // Güncel aidatları yükle
    }

    // Veritabanından daire tiplerini yükleyen metot
    private void loadDaireTipleri() {
        try (Connection connection = Database.getConnection()) {
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

    // Seçilen aylara göre tabloyu güncelleyen metot
    private void updateTableByMonths() {
        tableModel.setRowCount(0); // Tabloyu temizle
        List<Integer> selectedMonths = new ArrayList<>();
        if (allMonthsCheckBox.isSelected()) {
            for (int i = 1; i <= 12; i++) {
                selectedMonths.add(i); // Tüm aylar seçili
            }
        } else {
            for (int i = 0; i < monthCheckBoxes.length; i++) {
                if (monthCheckBoxes[i].isSelected()) {
                    selectedMonths.add(i + 1); // Ayları 1 tabanlı indeksle ekle
                }
            }
        }

        if (selectedMonths.isEmpty()) {
            return; // Hiçbir ay seçili değilse işlem yapma
        }

        try (Connection connection = Database.getConnection()) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT A.AidatID AS 'Fatura No', CONCAT(K.Ad, ' ', K.soyad) AS Sakin, D.DaireNo, DT.TipAdi, A.SonOdemeTarihi, A.OdemeTarihi, A.Tutar, A.Durum ")
                    .append("FROM Aidatlar A ")
                    .append("JOIN Daireler D ON A.DaireID = D.DaireID ")
                    .append("JOIN DaireTipleri DT ON D.TipID = DT.TipID ")
                    .append("JOIN Kullanicilar K ON D.KullaniciID = K.KullaniciID ")
                    .append("WHERE DT.TipAdi = ? ")
                    .append("AND MONTH(A.SonOdemeTarihi) IN (");

            for (int i = 0; i < selectedMonths.size(); i++) {
                queryBuilder.append(selectedMonths.get(i));
                if (i < selectedMonths.size() - 1) {
                    queryBuilder.append(",");
                }
            }
            queryBuilder.append(")");

            PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());
            statement.setString(1, selectedDaireTipi);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Vector<String> row = new Vector<>();
                row.add(String.valueOf(resultSet.getInt("Fatura No")));
                row.add(resultSet.getString("Sakin"));
                row.add(resultSet.getString("DaireNo"));
                row.add(resultSet.getString("TipAdi"));
                row.add(resultSet.getString("SonOdemeTarihi"));
                row.add(resultSet.getString("OdemeTarihi"));
                row.add(resultSet.getString("Tutar"));
                row.add(resultSet.getString("Durum"));
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Sekme değiştiğinde tabloyu güncelleyen metot
    private void updateTableByTab() {
        int selectedTabIndex = yearTabPane.getSelectedIndex();
        if (selectedTabIndex == 0) {  // Yıllık aidat sekmesi
            // Yıllık aidat için tabloyu güncelle
            allMonthsCheckBox.setSelected(true); // Tüm ayları seçili yap
            loadCurrentFees();
        } else if (selectedTabIndex == 1) {  // Aylık aidat sekmesi
            allMonthsCheckBox.setSelected(false); // Tüm ayları seçili olmaktan çıkar
            // Aylık aidatlar için seçilen aylara göre tabloyu güncelle
            updateTableByMonths();
        }
    }

    // Güncel aidatları tabloya yükleyen metot
    // Güncel aidatları tabloya yükleyen metot
    private void loadCurrentFees() {
        tableModel.setRowCount(0); // Önce tabloyu temizle
        StringBuilder queryBuilder = new StringBuilder();
        if (UserSession.getInstance().getRole().equalsIgnoreCase("Yonetici")) {
            selectedDaireTipi = (String) daireTipiComboBox.getSelectedItem(); // Seçili daire tipi
            queryBuilder.append("SELECT A.AidatID AS 'Fatura No', CONCAT(K.Ad, ' ', K.soyad) AS Sakin, D.DaireNo, DT.TipAdi, A.SonOdemeTarihi, A.OdemeTarihi, A.Tutar, A.Durum ")
                    .append("FROM Aidatlar A ")
                    .append("JOIN Daireler D ON A.DaireID = D.DaireID ")
                    .append("JOIN DaireTipleri DT ON D.TipID = DT.TipID ")
                    .append("JOIN Kullanicilar K ON D.KullaniciID = K.KullaniciID ");

            // Eğer bir daire tipi seçildiyse, sorguya filtre ekleyin
            if (selectedDaireTipi != null && !selectedDaireTipi.isEmpty()) {
                queryBuilder.append("WHERE DT.TipAdi = ? ");
            }

            // Seçilen ayları ekleyin
            List<Integer> selectedMonths = getSelectedMonths();
            if (!selectedMonths.isEmpty()) {
                if (selectedDaireTipi != null && !selectedDaireTipi.isEmpty()) {
                    queryBuilder.append("AND MONTH(A.SonOdemeTarihi) IN (");
                } else {
                    queryBuilder.append("WHERE MONTH(A.SonOdemeTarihi) IN (");
                }

                for (int i = 0; i < selectedMonths.size(); i++) {
                    queryBuilder.append(selectedMonths.get(i));
                    if (i < selectedMonths.size() - 1) {
                        queryBuilder.append(",");
                    }
                }
                queryBuilder.append(")");
            }
        } else {
            queryBuilder.append("SELECT A.AidatID AS 'Fatura No', CONCAT(K.Ad, ' ', K.soyad) AS Sakin, D.DaireNo, DT.TipAdi, A.SonOdemeTarihi, A.OdemeTarihi, A.Tutar, A.Durum ")
                    .append("FROM Aidatlar A ")
                    .append("JOIN Daireler D ON A.DaireID = D.DaireID ")
                    .append("JOIN DaireTipleri DT ON D.TipID = DT.TipID ")
                    .append("JOIN Kullanicilar K ON D.KullaniciID = K.KullaniciID ")
                    .append("WHERE K.KullaniciID = ? ");
        }

        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());

            if (!UserSession.getInstance().getRole().equalsIgnoreCase("Yonetici")) {
                statement.setInt(1, UserSession.getInstance().getUserId());
            }else {
                // Daire tipi parametresi ekle
                if (selectedDaireTipi != null && !selectedDaireTipi.isEmpty()) {
                    statement.setString(1, selectedDaireTipi);
                }
            }


            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Vector<String> row = new Vector<>();
                row.add(String.valueOf(resultSet.getInt("Fatura No")));
                row.add(resultSet.getString("Sakin"));
                row.add(resultSet.getString("DaireNo"));
                row.add(resultSet.getString("TipAdi"));
                row.add(resultSet.getString("SonOdemeTarihi"));
                row.add(resultSet.getString("OdemeTarihi"));
                row.add(resultSet.getString("Tutar"));
                row.add(resultSet.getString("Durum"));
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Seçilen ayları döndüren metot
    private List<Integer> getSelectedMonths() {
        List<Integer> selectedMonths = new ArrayList<>();
        if (allMonthsCheckBox.isSelected()) {
            for (int i = 1; i <= 12; i++) {
                selectedMonths.add(i); // Tüm aylar seçili
            }
        } else {
            for (int i = 0; i < monthCheckBoxes.length; i++) {
                if (monthCheckBoxes[i].isSelected()) {
                    selectedMonths.add(i + 1); // Ayları 1 tabanlı indeksle ekle
                }
            }
        }
        return selectedMonths;
    }


    // Aidatları kaydeden metot
    private void saveFees(String amount) {
        if (selectedDaireTipi == null || selectedDaireTipi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen bir daire tipi seçin!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Integer> selectedMonths = getSelectedMonths();
        if (selectedMonths.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen en az bir ay seçin!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = Database.getConnection()) {
            // Daire tipi ve daireler için kullanıcıları alın
            String query = "SELECT D.DaireID, D.KullaniciID, D.DaireNo " +
                    "FROM Daireler D " +
                    "JOIN DaireTipleri DT ON D.TipID = DT.TipID " +
                    "WHERE DT.TipAdi = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, selectedDaireTipi);
            ResultSet resultSet = statement.executeQuery();
            // Eğer daire tipine göre kullanıcılar bulunamazsa hata ver
            if (!resultSet.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this, "Seçilen daire tipine ait daire bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kullanıcılar ve daireler için ödeme kayıtlarını ekle
            while (resultSet.next()) {
                int daireID = resultSet.getInt("DaireID");
                int kullaniciID = resultSet.getInt("KullaniciID");
                String daireNo = resultSet.getString("DaireNo");

                // Her seçilen ay için ödeme kaydını ekleyin
                for (int month : selectedMonths) {
                    // Ödeme tarihi, seçilen ayın 15'ine ayarlanır
                    String paymentDate = String.format("2024-%02d-15", month); // Yıl olarak 2024 yazıldı, bunu değiştirebilirsiniz

                    // Aidatlar tablosuna ödeme kaydını ekleyin
                    String insertQuery = "INSERT INTO Aidatlar (DaireID, Tutar, SonOdemeTarihi) VALUES (?, ?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setInt(1, daireID);
                    insertStatement.setBigDecimal(2, new BigDecimal(amount)); // Tutarı BigDecimal olarak ekleyin
                    insertStatement.setString(3, paymentDate);
                    insertStatement.executeUpdate();
                }
            }

            // Başarılı işlemi bildiren mesaj
            JOptionPane.showMessageDialog(this, "Aidatlar başarıyla kaydedildi.", "Başarı", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Aidat durumunu güncelleyen metot
    private void updateFeesStatus(int aidatID, String durum) {
        try (Connection connection = Database.getConnection()) {
            String query = "";
            if (durum.equals("Odendi")) {
                query = "UPDATE Aidatlar SET Durum = ?, OdemeTarihi = CURDATE() WHERE AidatID = ?";
            } else if (durum.equals("Odenmedi")) {
                query = "UPDATE Aidatlar SET Durum = ?, OdemeTarihi = NULL WHERE AidatID = ?";
            }
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, durum);
            statement.setInt(2, aidatID);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Aidat durumu güncellendi.", "Başarı", JOptionPane.INFORMATION_MESSAGE);
            loadCurrentFees(); // Tabloyu güncelle
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }



    @Override
    public JPanel getPanel() {
        return this;
    }
}
