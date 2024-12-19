public class SmsNotifier implements NotificationObserver {
    @Override
    public void update(String title, String content, String recipientInfo) {
        // SMS gönderim API entegrasyonu burada yapılabilir
        System.out.println("SMS Gönderiliyor: " + recipientInfo);
        System.out.println("Başlık: " + title);
        System.out.println("İçerik: " + content);
    }
}
