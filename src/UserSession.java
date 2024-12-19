public class UserSession {
    private static UserSession instance; // Singleton instance
    private int userId;
    private String name;
    private String surname;
    private String email;
    private String role; // Kullanıcının rolü (örneğin: "Yonetici" veya "Sakin")

    // Private constructor to prevent instantiation
    private UserSession() {}

    // Singleton için getInstance metodu
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Kullanıcı bilgilerini set etmek için bir metot
    public void setUserDetails(int userId, String name, String surname, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
    }

    // Getter metotları
    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    // Kullanıcı oturumunu temizleme
    public void clearSession() {
        instance = null;
    }
}
