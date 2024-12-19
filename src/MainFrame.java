import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;

    public MainFrame() {
        setTitle("Apartman Yönetim Sistemi");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ana panel (kenar boşlukları için)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(0, 0, 20, 20)); // Alt ve sağ kenarda 50 piksel boşluk

        tabbedPane = new JTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Ana paneli Frame'e ekle
        add(mainPanel);

        loadTabs();
    }

    private void loadTabs() {
        String[] tabNames;
        if (UserSession.getInstance().getRole().equalsIgnoreCase("Yonetici")) {
            tabNames = new String[]{"Ana Sayfa", "Kullanıcılar", "Aidatlar", "Bakım/Onarım", "Bildirimler", "Gelir/Gider"};
        } else {
            tabNames = new String[]{"Ana Sayfa", "Aidatlar", "Bakım/Onarım", "Bildirimler", "Gelir/Gider"};
        }
        for (String tabName : tabNames) {
            TabView tab = TabFactory.createTab(tabName);
            tabbedPane.addTab(tabName, tab.getPanel());
        }
    }

    public static void display() {
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }
}
