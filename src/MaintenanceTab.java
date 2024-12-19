import javax.swing.*;

public class MaintenanceTab extends AbstractMaintenanceTab {
    @Override
    protected String getLoadQuery() {
        return "SELECT IslemID, Konu, Aciklama, Fiyat, Tarih FROM BakimOnarimlar ORDER BY Tarih DESC";
    }

    @Override
    protected String getAddQuery() {
        return "INSERT INTO BakimOnarimlar (Konu, Aciklama, Fiyat, Tarih) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM BakimOnarimlar WHERE IslemID = ?";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }
}