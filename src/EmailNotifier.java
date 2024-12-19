public class EmailNotifier implements NotificationObserver {
    @Override
    public void update(String title, String content, String recipientInfo) {
        // E-posta gönderim API entegrasyonu burada yapılabilir
        System.out.println("E-posta Gönderiliyor: " + recipientInfo);
        System.out.println("Başlık: " + title);
        System.out.println("İçerik: " + content);
    }
}
