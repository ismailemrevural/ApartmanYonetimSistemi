import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.*;


public class ApartmentCreationFrame extends JFrame {
    private JTextField apartmentNameField;
    private JTextField floorCountField;
    private JTextField unitCountField;
    private JTextArea addressArea;
    private JTextField blockInputField;
    private JTextField unitTypeInputField;
    private JList<String> blockList;
    private JList<String> unitTypeList;
    private DefaultListModel<String> blockListModel;
    private DefaultListModel<String> unitTypeListModel;

    public ApartmentCreationFrame() {
        // JFrame ayarları
        setTitle("Apartman Oluştur");
        setSize(745, 390);
        setLocationRelativeTo(null);  // Ekranın ortasına yerleştir
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);  // null layout kullanılıyor

        // Apartman adı
        JLabel apartmentNameLabel = new JLabel("Apartman Adı:");
        apartmentNameLabel.setBounds(30, 30, 100, 25);  // Konum ve boyut
        add(apartmentNameLabel);

        apartmentNameField = new JTextField();
        apartmentNameField.setBounds(150, 30, 200, 25);
        add(apartmentNameField);

        // Kat sayısı
        JLabel floorCountLabel = new JLabel("Kat Sayısı:");
        floorCountLabel.setBounds(30, 70, 100, 25);
        add(floorCountLabel);

        floorCountField = new JTextField();
        floorCountField.setBounds(150, 70, 200, 25);
        add(floorCountField);

        // Kat sayısı sadece sayısal değer almalı
        floorCountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();  // Karakteri engelle
                }
            }
        });

        // Daire sayısı
        JLabel unitCountLabel = new JLabel("Daire Sayısı:");
        unitCountLabel.setBounds(30, 110, 100, 25);
        add(unitCountLabel);

        unitCountField = new JTextField();
        unitCountField.setBounds(150, 110, 200, 25);
        add(unitCountField);

        // Daire sayısı sadece sayısal değer almalı
        unitCountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();  // Karakteri engelle
                }
            }
        });

        // Adres
        JLabel addressLabel = new JLabel("Adres:");
        addressLabel.setBounds(380, 30, 100, 25);
        add(addressLabel);

        addressArea = new JTextArea();
        addressArea.setBounds(500, 30, 200, 100);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        add(addressArea);

        // Blok ekleme alanı
        JLabel blockLabel = new JLabel("Bloklar:");
        blockLabel.setBounds(30, 150, 100, 25);
        add(blockLabel);

        blockInputField = new JTextField();
        blockInputField.setBounds(150, 150, 120, 25);
        add(blockInputField);

        JButton addBlockButton = new JButton("Ekle");
        addBlockButton.setBounds(280, 150, 70, 25);
        addBlockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String blockName = blockInputField.getText();
                if (!blockName.isEmpty()) {
                    blockListModel.addElement(blockName);
                    blockInputField.setText("");  // Alanı temizle
                }
            }
        });
        add(addBlockButton);

        // Bloklar listesi
        blockListModel = new DefaultListModel<>();
        blockList = new JList<>(blockListModel);
        blockList.setBounds(150, 180, 200, 80);
        add(blockList);

        // Blok silme butonu
        JButton removeBlockButton = new JButton("Sil");
        removeBlockButton.setBounds(150, 270, 200, 25);
        removeBlockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Seçili öğeyi sil
                int selectedIndex = blockList.getSelectedIndex();
                if (selectedIndex != -1) {
                    blockListModel.remove(selectedIndex);
                }
            }
        });
        add(removeBlockButton);

        // Daire tipi ekleme alanı
        JLabel unitTypeLabel = new JLabel("Daire Tipi:");
        unitTypeLabel.setBounds(380, 150, 100, 25);
        add(unitTypeLabel);

        unitTypeInputField = new JTextField();
        unitTypeInputField.setBounds(500, 150, 120, 25);
        add(unitTypeInputField);

        JButton addUnitTypeButton = new JButton("Ekle");
        addUnitTypeButton.setBounds(630, 150, 70, 25);
        addUnitTypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String unitType = unitTypeInputField.getText();
                if (!unitType.isEmpty()) {
                    unitTypeListModel.addElement(unitType);
                    unitTypeInputField.setText("");  // Alanı temizle
                }
            }
        });
        add(addUnitTypeButton);

        // Daire tipleri listesi
        unitTypeListModel = new DefaultListModel<>();
        unitTypeList = new JList<>(unitTypeListModel);
        unitTypeList.setBounds(500, 180, 200, 80);
        add(unitTypeList);

        // Daire tipi silme butonu
        JButton removeUnitTypeButton = new JButton("Sil");
        removeUnitTypeButton.setBounds(500, 270, 200, 25);
        removeUnitTypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Seçili öğeyi sil
                int selectedIndex = unitTypeList.getSelectedIndex();
                if (selectedIndex != -1) {
                    unitTypeListModel.remove(selectedIndex);
                }
            }
        });
        add(removeUnitTypeButton);

        // Kaydet butonu
        JButton saveButton = new JButton("Kaydet ve Oluştur");
        saveButton.setBounds(265, 310, 200, 30);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kullanıcıdan alınan veriler
                String apartmentName = apartmentNameField.getText();
                String floorCountStr = floorCountField.getText();
                String unitCountStr = unitCountField.getText();
                String address = addressArea.getText();

                // Boş alanları kontrol et
                if (apartmentName.isEmpty() || floorCountStr.isEmpty() || unitCountStr.isEmpty() || address.isEmpty()) {
                    JOptionPane.showMessageDialog(ApartmentCreationFrame.this, "Tüm alanları doldurun.");
                    return;
                }

                // Daire Tipi ve Blokların en az bir tane olup olmadığını kontrol et
                if (blockListModel.isEmpty()) {
                    JOptionPane.showMessageDialog(ApartmentCreationFrame.this, "En az bir blok eklemelisiniz.");
                    return;
                }

                if (unitTypeListModel.isEmpty()) {
                    JOptionPane.showMessageDialog(ApartmentCreationFrame.this, "En az bir daire tipi eklemelisiniz.");
                    return;
                }

                // Kat sayısı ve daire sayısı integer'a dönüştürülüyor
                int floorCount = Integer.parseInt(floorCountStr);
                int unitCount = Integer.parseInt(unitCountStr);

                // Blok ve daire tiplerini listele
                ArrayList<String> blocks = new ArrayList<>();
                for (int i = 0; i < blockListModel.size(); i++) {
                    blocks.add(blockListModel.get(i));
                }

                ArrayList<String> unitTypes = new ArrayList<>();
                for (int i = 0; i < unitTypeListModel.size(); i++) {
                    unitTypes.add(unitTypeListModel.get(i));
                }

                // Veritabanına kaydetme işlemi
                saveApartmentData(apartmentName, floorCount, unitCount, address, blocks, unitTypes);
            }
        });
        add(saveButton);
    }

    private void saveApartmentData(String apartmentName, int floorCount, int unitCount, String address,
                                   ArrayList<String> blocks, ArrayList<String> unitTypes) {
        Connection connection = Database.getConnection();
        PreparedStatement psApartment = null;
        PreparedStatement psBlock = null;
        PreparedStatement psUnitType = null;
        ResultSet rs = null;

        try {
            // 1. Apartman verisini kaydet
            String apartmentSQL = "INSERT INTO Apartman (Ad, KatSayisi, DaireSayisi, Adres) VALUES (?, ?, ?, ?)";
            psApartment = connection.prepareStatement(apartmentSQL, Statement.RETURN_GENERATED_KEYS);
            psApartment.setString(1, apartmentName);
            psApartment.setInt(2, floorCount);
            psApartment.setInt(3, unitCount);
            psApartment.setString(4, address);
            psApartment.executeUpdate();

            // Apartman ID'sini al
            rs = psApartment.getGeneratedKeys();
            int apartmentID = -1;
            if (rs.next()) {
                apartmentID = rs.getInt(1);
            }

            // 2. Bloklar verisini kaydet
            String blockSQL = "INSERT INTO Bloklar (ApartmanID, BlokAdi, KatSayisi) VALUES (?, ?, ?)";
            psBlock = connection.prepareStatement(blockSQL);
            for (String blockName : blocks) {
                psBlock.setInt(1, apartmentID);
                psBlock.setString(2, blockName);
                psBlock.setInt(3, floorCount); // Her blokta aynı kat sayısı varsayılıyor
                psBlock.executeUpdate();
            }

            // 3. Daire Tipleri verisini kaydet
            String unitTypeSQL = "INSERT INTO DaireTipleri (ApartmanID, TipAdi) VALUES (?, ?)";
            psUnitType = connection.prepareStatement(unitTypeSQL);
            for (String unitType : unitTypes) {
                psUnitType.setInt(1, apartmentID);
                psUnitType.setString(2, unitType);
                psUnitType.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Apartman başarıyla oluşturuldu.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (psApartment != null) psApartment.close();
                if (psBlock != null) psBlock.close();
                if (psUnitType != null) psUnitType.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void display() {
        ApartmentCreationFrame frame = new ApartmentCreationFrame();
        frame.setVisible(true);
    }
}
