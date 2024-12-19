import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ApplicationContext extends Database {
    private ApplicationState currentState;

    public void setState(ApplicationState state) {
        this.currentState = state;
    }

    public void execute() {
        if (currentState != null) {
            currentState.handle();
        }
    }

    // Durumu veritabanı durumuna göre belirleyen bir yöntem
    public static ApplicationState determineState() {
        boolean apartmentExists = checkApartmentInDatabase();
        if (apartmentExists) {
            return new LoginState();
        } else {
            return new ApartmentCreationState();
        }
    }

    // Veritabanında apartman kaydı olup olmadığını kontrol eden yöntem
    private static boolean checkApartmentInDatabase() {
        // Veritabanı kontrolü yapılır (örnek bir bağlantı simülasyonu)
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT COUNT(*) FROM Apartman";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Kayıt varsa true döner
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}