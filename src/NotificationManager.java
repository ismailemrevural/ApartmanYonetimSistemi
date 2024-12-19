import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    private final List<NotificationObserver> observers = new ArrayList<>();

    // Gözlemci ekleme
    public void addObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    // Gözlemci çıkarma
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    // Tüm gözlemcileri bilgilendir
    public void notifyObservers(String title, String content, String recipientInfo) {
        for (NotificationObserver observer : observers) {
            observer.update(title, content, recipientInfo);
        }
    }
}
