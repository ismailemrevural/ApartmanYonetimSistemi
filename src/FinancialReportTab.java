import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;

public class FinancialReportTab extends JPanel implements TabView {
    private JLabel totalIncomeLabel;
    private JLabel totalExpensesLabel;
    private JLabel netIncomeLabel;
    private JButton generateReportButton;

    public FinancialReportTab() {
        setLayout(new BorderLayout());

        // Başlıkları ve etiketleri ekleyelim
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(4, 1));

        totalIncomeLabel = new JLabel("Toplam Gelir: 0.00 TL", SwingConstants.CENTER);
        totalExpensesLabel = new JLabel("Toplam Gider: 0.00 TL", SwingConstants.CENTER);
        netIncomeLabel = new JLabel("Net Gelir: 0.00 TL", SwingConstants.CENTER);

        infoPanel.add(totalIncomeLabel);
        infoPanel.add(totalExpensesLabel);
        infoPanel.add(netIncomeLabel);

        add(infoPanel, BorderLayout.CENTER);
        generateReport();

        // Rapor oluştur butonu
        generateReportButton = new JButton("Raporu Oluştur");
        generateReportButton.addActionListener(e -> generateReport());
        add(generateReportButton, BorderLayout.SOUTH);
    }

    private void generateReport() {
        double totalIncome = 0;
        double totalExpenses = 0;

        try (Connection connection = Database.getConnection()) {
            // Aidatları sorgulama - OdemeTarihi null olmayanlar
            String incomeQuery = "SELECT Tutar FROM Aidatlar WHERE OdemeTarihi IS NOT NULL";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(incomeQuery);

            while (resultSet.next()) {
                totalIncome += resultSet.getDouble("Tutar");
            }

            // Bakım onarımları sorgulama
            String expensesQuery = "SELECT Fiyat FROM BakimOnarimlar";
            ResultSet resultSetExpenses = statement.executeQuery(expensesQuery);

            while (resultSetExpenses.next()) {
                totalExpenses += resultSetExpenses.getDouble("Fiyat");
            }

            // Net geliri hesaplama
            double netIncome = totalIncome - totalExpenses;

            // Sonuçları ekrana yazma
            DecimalFormat df = new DecimalFormat("0.00");
            totalIncomeLabel.setText("Toplam Gelir: " + df.format(totalIncome) + " TL");
            totalExpensesLabel.setText("Toplam Gider: " + df.format(totalExpenses) + " TL");
            netIncomeLabel.setText("Net Gelir: " + df.format(netIncome) + " TL");

            // Grafikte göstermek isterseniz, örneğin bar chart, burada ekleyebilirsiniz.
            // Grafik eklemek için JFreeChart veya başka bir grafik kütüphanesi kullanılabilir.

            resultSet.close();
            resultSetExpenses.close();
            statement.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Veri alınırken hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public JPanel getPanel() {
        return this;
    }
}
