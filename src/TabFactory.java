public class TabFactory {
    public static TabView createTab(String tabName) {
        switch (tabName) {
            case "Ana Sayfa":
                return new MainTab();
            case "Kullanıcılar":
                return new UsersTab();
            case "Aidatlar":
                return new FeesTab();
            case "Bakım/Onarım":
                return new MaintenanceTab();
            case "Bildirimler":
                return new NotificationsTab();
            case "Gelir/Gider":
                return new FinancialReportTab();
            default:
                throw new IllegalArgumentException("Geçersiz sekme adı: " + tabName);
        }
    }
}
